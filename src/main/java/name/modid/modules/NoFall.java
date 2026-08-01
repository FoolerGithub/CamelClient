package name.modid.modules;

import name.modid.base.Module;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.lwjgl.glfw.GLFW;

public class NoFall extends Module {
    public NoFall() {
        // Активация на клавишу N
        super("NoFall", GLFW.GLFW_KEY_N);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.getNetworkHandler() == null) return;

        // Проверяем: если игрок летит вниз (скорость по Y отрицательная)
        // и расстояние падения больше 2 блоков, начинаем обман
        if (mc.player.getVelocity().y < 0 && mc.player.fallDistance > 3.0F) {

            // Отправляем на сервер специальный пакет перемещения,
            // в котором принудительно говорим, что мы стоим на земле (true)
            mc.getNetworkHandler().sendPacket(
                    new PlayerMoveC2SPacket.OnGroundOnly(true, mc.player.horizontalCollision)
            );

            // Дополнительно сбрасываем дистанцию падения на клиенте, чтобы не трясся экран
            mc.player.fallDistance = 0;
        }
    }
}
