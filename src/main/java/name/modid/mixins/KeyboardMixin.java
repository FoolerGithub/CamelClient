package name.modid.mixins;

import name.modid.base.Client;
import name.modid.base.KeyInterceptor;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey", at = @At("HEAD"))
    private void onKeyInject(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        // action == 1 означает НАЖАТИЕ клавиши (0 - отжатие, 2 - зажатие)
        if (action == 1) {
            // 1. Обычный бинд включения/выключения
            Client.INSTANCE.onKeyPress(key);

            // 2. Проброс нажатия всем модулям-перехватчикам клавиш
            Client.INSTANCE.getModules().stream()
                    .filter(m -> m.isEnabled() && m instanceof KeyInterceptor)
                    .map(m -> (KeyInterceptor) m)
                    .forEach(interceptor -> interceptor.onKeyPress(key));
        }
    }
}
