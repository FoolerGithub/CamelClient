package name.modid.modules;

import name.modid.base.Module;
import org.lwjgl.glfw.GLFW;

public class Fly extends Module {
    public Fly() {
        super("Fly", GLFW.GLFW_KEY_G);
    }

    @Override
    public void onEnable() {
        if (mc.player != null) {
            mc.player.getAbilities().allowFlying = true;
            mc.player.getAbilities().flying = true;
        }
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.getAbilities().allowFlying = false;
            mc.player.getAbilities().flying = false;
        }
    }
}