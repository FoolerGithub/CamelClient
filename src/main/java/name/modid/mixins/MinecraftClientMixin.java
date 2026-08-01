package name.modid.mixins;

import name.modid.base.Client;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    // Внедряем вызов нашего onTick в самый конец оригинального метода tick() Майнкрафта
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTickInject(CallbackInfo ci) {
        Client.INSTANCE.onTick();
    }
}