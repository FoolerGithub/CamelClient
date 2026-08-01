package name.modid.base;

import net.minecraft.client.MinecraftClient;

public class Module {
    private final String name;
    private final int key;
    private boolean enabled;
    protected final MinecraftClient mc = MinecraftClient.getInstance();

    public Module(String name, int key) {
        this.name = name;
        this.key = key;
        this.enabled = false;
    }

    public void toggle() {
        this.enabled = !this.enabled;
        if (this.enabled) onEnable();
        else onDisable();
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick() {}

    public int getKey() { return key; }
    public boolean isEnabled() { return enabled; }
}