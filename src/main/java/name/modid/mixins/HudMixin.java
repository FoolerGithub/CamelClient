package name.modid.mixins;

import name.modid.base.Client;
import name.modid.base.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class HudMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.options.hudHidden) return;

        TextRenderer textRenderer = mc.textRenderer;
        int yOffset = 5;

        for (Module m : Client.INSTANCE.getModules()) {

        // Проходимся по модулям. Для каждого проверяем: если
        // он включён, то выводим его в худ на экран
            if (m.isEnabled()) {

                context.drawText(
                        textRenderer,
                        m.getClass().getSimpleName(),
                        5,
                        yOffset,
                        0x00FF00,
                        true
                );

                yOffset += textRenderer.fontHeight + 2;
            }
        }
    }
}