package name.modid.base;

import name.modid.modules.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.ArrayList;
import java.util.List;

public class Client implements ClientModInitializer {
    public static Client INSTANCE = new Client();
    private final List<Module> modules = new ArrayList<>();
    private FuntimeHelper funtimeHelper;

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



    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        // Регистрация тика для всех модулей
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // Находим FuntimeHelper в списке и вызываем у него onTick
            modules.stream()
                    .filter(m -> m instanceof FuntimeHelper)
                    .map(m -> (FuntimeHelper) m)
                    .forEach(FuntimeHelper::onTick);
        });
    }
}