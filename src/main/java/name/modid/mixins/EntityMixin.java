package name.modid.mixins;


import name.modid.base.Client;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Entity.class)

public class EntityMixin {
    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    private void onIsGlowing(CallbackInfoReturnable<Boolean> cir) {
        boolean espEnabled = Client.INSTANCE.getModules().stream()
                .anyMatch(m -> m.getClass().getSimpleName().equals("GlowESP") && m.isEnabled());

        if (espEnabled) {
            cir.setReturnValue(true);
        }
    }
}
