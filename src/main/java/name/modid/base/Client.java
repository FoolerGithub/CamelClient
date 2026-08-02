package name.modid.base;

import name.modid.modules.*;
import net.fabricmc.api.ClientModInitializer;

import java.util.ArrayList;
import java.util.List;

public class Client implements ClientModInitializer {
    public static Client INSTANCE = new Client();
    private final List<Module> modules = new ArrayList<>();

    public void init() {
        modules.add(new Fly());
        modules.add(new TriggerBot());
        modules.add(new NoFall());
        modules.add(new GlowESP());
        modules.add(new KillAura());
        modules.add(new FuntimeHelper());
    }

    public List<Module> getModules() { return modules; }

    public void onKeyPress(int key) {
        for (Module m : modules) {
            if (m.getKey() == key) {
                m.toggle();
            }
        }
    }

    public void onTick() {
        for (Module m : modules) {
            if (m.isEnabled()) {
                m.onTick();
            }
        }
    }

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
    }
}
