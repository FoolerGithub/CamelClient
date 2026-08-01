package name.modid.modules;

import name.modid.base.Module;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

public class TriggerBot extends Module {

    public TriggerBot() {
        // Активация на клавишу R (можешь поменять на любую другую)
        super("TriggerBot", GLFW.GLFW_KEY_R);
    }

    @Override
    public void onTick() {
        // Если игрок в меню или мир еще не прогрузился — ничего не делаем
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        // mc.crosshairTarget показывает, на что прямо сейчас наведен прицел игрока
        HitResult hitResult = mc.crosshairTarget;

        // Проверяем, что прицел наведен именно на сущность (моба или игрока)
        if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult) hitResult;

            // Если у игрока еще не откатился удар (кулдаун оружия), то не бьем, чтобы урон был максимальным
            if (mc.player.getAttackCooldownProgress(0.0F) >= 1.0F) {
                // Бьем сущность
                mc.interactionManager.attackEntity(mc.player, entityHitResult.getEntity());
                // Проигрываем анимацию руки (взмах)
                mc.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }
}