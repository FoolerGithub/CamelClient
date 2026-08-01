# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A Minecraft cheat/utility mod for **Minecraft 1.21.8** built on the **Fabric** mod loader. Based on the Fabric example mod template.

## Build Commands

```bash
# Full build (compile, remap, jar)
./gradlew build

# Just compile
./gradlew compileJava

# Run the mod in a development Minecraft client
./gradlew runClient

# Clean build artifacts
./gradlew clean
```

The built jar lands in `build/libs/`.

## Architecture

### Gradle + Fabric Loom

Java 21, Fabric Loader 0.19.3, Fabric API 0.136.1, Yarn mappings. The Loom plugin handles Minecraft deobfuscation, remapping, and the dev runtime. Version pins live in `gradle.properties`.

### Dual Entry Points

The mod registers **two** entry points in `fabric.mod.json`:
- `TemplateMod` (`ModInitializer`) — server-side init; calls `Client.INSTANCE.init()` to register all modules.
- `Client` (`ClientModInitializer`) — client-side init; registers the `END_CLIENT_TICK` event to call `FuntimeHelper.onTick()` each tick.

### Module System (`base/`)

- **`Module`** — abstract base. Each module has a `name`, GLFW `key` bind, and `enabled` boolean. Lifecycle hooks: `onEnable()`, `onDisable()`, `onTick()`. `toggle()` flips state and calls the appropriate hook. Holds a reference to `MinecraftClient.getInstance()`.
- **`Client`** — singleton that owns `List<Module>`. `init()` adds all module instances. `onKeyPress(key)` toggles the module whose bind matches. `onTick()` iterates enabled modules and calls their `onTick()`.

### Mixins (`mixins/`)

Mixins are the mechanism for injecting code into vanilla Minecraft. Declared in `src/main/resources/template-mod.mixins.json` (package: `name.modid.mixins`, compatibility: `JAVA_21`).

| Mixin | Target Class | Purpose |
|---|---|---|
| `MinecraftClientMixin` | `MinecraftClient` | Injects `Client.INSTANCE.onTick()` at `TAIL` of `tick()`, driving per-tick module logic |
| `KeyboardMixin` | `Keyboard` | Intercepts `onKey` for module toggle binds and FuntimeHelper fast keys (action == 1 = press only) |
| `EntityMixin` | `Entity` | Overrides `isGlowing()` to always return `true` when GlowESP is enabled |
| `HudMixin` | `InGameHud` | Renders enabled module names (green text) in the top-left HUD overlay |
| `ExampleMixin` | `MinecraftServer` | Template/placeholder — injects into `loadWorld` HEAD, does nothing |

### Modules (`modules/`)

| Module | Key | What it does |
|---|---|---|
| `Fly` | G | Enables creative flight on the player; disables on deactivation |
| `TriggerBot` | R | Auto-attacks the entity under the crosshair when attack cooldown is ready |
| `NoFall` | N | Sends `OnGroundOnly(true)` packets and resets client fall distance when falling to prevent fall damage |
| `GlowESP` | H | Makes all entities glow (via `EntityMixin.isGlowing`) — toggle only, no per-tick logic |
| `KillAura` | P | Finds nearest living entity within 3.8 blocks, smoothly rotates toward it (limited by `rotationSpeed`), auto-attacks on cooldown |
| `FuntimeHelper` | K | Meta-helper: fast-use items via hotkeys (C = netherite scrap, X = dried kelp; swaps from inventory if needed), V = `/home` command. Uses a 1-tick delay pattern for inventory swap-back |

### Key Architectural Patterns

- **Modules are registered in `Client.init()`**, not auto-discovered. Adding a new module requires both creating the class and adding it to the `modules.add(...)` list.
- **`FuntimeHelper` is special-cased** — it gets `onTick()` called both via the `END_CLIENT_TICK` event (in `Client.onInitializeClient()`) and via `Client.onTick()` (from `MinecraftClientMixin`). Its `onFastKey()` is called directly from `KeyboardMixin`. Other modules follow the standard `Module` lifecycle exclusively.
- **Mixins are the integration layer.** All vanilla Minecraft hooks go through mixins; modules never reference mixins directly. Mixins call into `Client.INSTANCE` which delegates to modules.
- **Comments are in Russian** — the codebase uses a mix of Russian comments and English Minecraft API references.
