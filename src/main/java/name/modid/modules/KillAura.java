//package name.modid.modules;
//
//import name.modid.base.Module;
//import net.minecraft.entity.LivingEntity;
//import net.minecraft.util.Hand;
//import org.lwjgl.glfw.GLFW;
//
//import java.util.Comparator;
//
//public class KillAura extends Module {
//    private final double ATTACK_RANGE = 3.8;
//
//    public KillAura() {
//        super("KillAura", GLFW.GLFW_KEY_X);
//    }
//
//    @Override
//    public void onTick() {
//        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
//
//        LivingEntity target = mc.world.getEntitiesByClass(LivingEntity.class,
//                mc.player.getBoundingBox().expand(ATTACK_RANGE),
//                entity -> entity != mc.player && entity.isAlive())
//                .stream()
//                .min(Comparator.comparingDouble(entity -> mc.player.distanceTo(entity)))
//                .orElse(null);
//
//        if (target != null) {
//
//            double diffX = target.getX() - mc.player.getX();
//            double diffY = (target.getY() + target.getEyeHeight(target.getPose())) - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
//            double diffZ = target.getZ() - mc.player.getZ();
//            double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
//
//            float yaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F);
//            float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));
//
//            mc.player.setYaw(yaw);
//            mc.player.setPitch(pitch);
//
//            if (mc.player.getAttackCooldownProgress(0.0F) >= 1.0F) {
//                mc.interactionManager.attackEntity(mc.player, target);
//                mc.player.swingHand(Hand.MAIN_HAND);
//            }
//        }
//    }
//}
package name.modid.modules;


import name.modid.base.Module;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public class KillAura extends Module {

    // Настройки в стиле Wexside (можно вынести в конфиг)
    private final double attackRange = 3.8; // Дистанция удара
    private final float rotationSpeed = 15.0F; // Макс. градусов поворота за 1 тик (чем меньше, тем плавнее)

    public KillAura() {
        super("KillAura", GLFW.GLFW_KEY_P);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        // Ищем ближайшую живую цель (игрока или моба) в радиусе атаки
        LivingEntity target = null;
        double closestDist = attackRange;

        for (net.minecraft.entity.Entity entity : mc.world.getEntities()) {
            if (entity instanceof LivingEntity && entity != mc.player && entity.isAlive()) {
                double dist = mc.player.distanceTo(entity);
                if (dist < closestDist) {
                    closestDist = dist;
                    target = (LivingEntity) entity;
                }
            }
        }

        // Если цель найдена — плавно наводимся и бьем
        if (target != null) {
            // 1. Вычисляем идеальные математические углы до цели
            float[] perfectRotations = calculateRotations(target);

            // 2. Сглаживаем их относительно ТЕКУЩИХ углов игрока (Wexside Logic)
            float smoothYaw = limitAngleChange(mc.player.getYaw(), perfectRotations[0], rotationSpeed);
            float smoothPitch = limitAngleChange(mc.player.getPitch(), perfectRotations[1], rotationSpeed);

            // 3. Применяем ротации к игроку (для Client-Side плавности, камеру не рвет)
            mc.player.setYaw(smoothYaw);
            mc.player.setPitch(smoothPitch);

            // 4. Проверяем кулдаун и бьем, только если прицел довывелся примерно на цель
            if (mc.player.getAttackCooldownProgress(0.0F) >= 1.0F) {
                mc.interactionManager.attackEntity(mc.player, target);
                mc.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }

    // Математика вычисления углов (то, что мы разбирали в прошлый раз)
    private float[] calculateRotations(LivingEntity target) {
        double diffX = target.getX() - mc.player.getX();
        // Целимся в грудь (чуть ниже глаз цели)
        double diffY = (target.getY() + target.getEyeHeight(EntityPose.STANDING) * 0.75) - (mc.player.getY() + mc.player.getEyeHeight(EntityPose.STANDING));
        double diffZ = target.getZ() - mc.player.getZ();

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0);
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        return new float[]{yaw, pitch};
    }

    // Функция ограничения скорости поворота
    private float limitAngleChange(float current, float target, float maxChange) {
        float diff = target - current;

        // Нормализация угла в диапазон -180...180, чтобы голова не крутилась на 360 градусов
        while (diff < -180.0F) diff += 360.0F;
        while (diff >= 180.0F) diff -= 360.0F;

        // Если разница больше лимита — поворачиваем только на макс. шаг
        if (diff > maxChange) diff = maxChange;
        if (diff < -maxChange) diff = -maxChange;

        return current + diff;
    }
}