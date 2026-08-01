package name.modid.modules;

import name.modid.base.Module;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public class FuntimeHelper extends Module {

    private int pendingSwapSlot = -1;
    private int pendingOriginalHotbar = -1;
    private int delayTicks = 0;

    public FuntimeHelper() {
        super("FuntimeHelper", GLFW.GLFW_KEY_K);
    }

    public void onFastKey(int key) {
        if (!this.isEnabled() || mc.player == null || mc.interactionManager == null) return;

        // БИНД НА ТРАПКУ (Кнопка C)
        if (key == GLFW.GLFW_KEY_C) {
            useAndSwapBack(Items.NETHERITE_SCRAP);
        }

        // БИНД НА ПЛАСТ (Кнопка X)
        if (key == GLFW.GLFW_KEY_X) {
            useAndSwapBack(Items.DRIED_KELP);
        }

        // БИНД НА МЕМБРАНУ (Кнопка B)
        if (key == GLFW.GLFW_KEY_B) {
            useAndSwapBack(Items.PHANTOM_MEMBRANE);
        }

        // БИНД НА ЛИВ (/home) (Кнопка V)
        if (key == GLFW.GLFW_KEY_V) {
            mc.player.networkHandler.sendChatCommand("home");
        }
    }

    /**
     * Вызывается каждый тик игры (например, из вашего основного класса/ClientTickEvents)
     */
    public void onTick() {
        if (delayTicks > 0) {
            delayTicks--;
            if (delayTicks == 0 && pendingSwapSlot != -1 && mc.player != null && mc.interactionManager != null) {
                int syncId = mc.player.playerScreenHandler.syncId;

                // Возвращаем предмет (второй SWAP) спустя 1 тик, когда сервер успел засчитать ПКМ
                mc.interactionManager.clickSlot(syncId, pendingSwapSlot, pendingOriginalHotbar, SlotActionType.SWAP, mc.player);

                // Сбрасываем переменные
                pendingSwapSlot = -1;
                pendingOriginalHotbar = -1;
            }
        }
    }

    private void useAndSwapBack(Item targetItem) {
        int inventorySlot = -1;

        // Ищем предмет в 36 слотах
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.isOf(targetItem)) {
                inventorySlot = i;
                break;
            }
        }

        if (inventorySlot == -1) return;

        int currentHotbar = mc.player.getInventory().getSelectedSlot();
        int syncId = mc.player.playerScreenHandler.syncId;

        // СЛУЧАЙ 1: Предмет УЖЕ на панели быстрого доступа (слоты 0-8)
        if (inventorySlot < 9) {
            mc.player.getInventory().setSelectedSlot(inventorySlot);
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(inventorySlot));

            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);

            mc.player.getInventory().setSelectedSlot(currentHotbar);
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(currentHotbar));
        }
        // СЛУЧАЙ 2: Предмет лежит в глубине инвентаря (слоты 9-35)
        else {
            // 1. Достаем предмет из инвентаря в руку
            mc.interactionManager.clickSlot(syncId, inventorySlot, currentHotbar, SlotActionType.SWAP, mc.player);

            // 2. Имитируем ПКМ
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);

            // 3. Запоминаем слоты и ставим задержку в 1 тик для возврата
            this.pendingSwapSlot = inventorySlot;
            this.pendingOriginalHotbar = currentHotbar;
            this.delayTicks = 1;
        }
    }
}
