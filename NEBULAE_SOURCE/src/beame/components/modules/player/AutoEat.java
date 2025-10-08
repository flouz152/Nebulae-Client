package beame.components.modules.player;

import beame.components.command.AbstractCommand;
import events.Event;
import events.impl.player.EventUpdate;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import beame.setting.SettingList.SliderSetting;

public class AutoEat extends Module {
// leaked by itskekoff; discord.gg/sk3d YI22CbY5

    public final SliderSetting foodStats = new SliderSetting("Порог еды", 18, 1, 20, 1);
    public AutoEat() {
        super("AutoEat",Category.Player, true, "Автоматически ест еду");
        addSettings(foodStats);
    }

    private boolean isEating;
    private int previousSlot = -1;

    @Override
    public void event(Event event) {
        if (mc.world == null || mc.player == null) return;
        if (event instanceof EventUpdate) {

            ClientPlayerEntity player = mc.player;

            if (player.getFoodStats().getFoodLevel() < foodStats.get()) {
                if (!hasFoodInHands()) {
                    int foodSlot = findFoodSlot();
                    if (foodSlot == -1) {
                        AbstractCommand.addMessage("Типо еда должна быть в слоте");
                        return;
                    }

                    previousSlot = player.inventory.currentItem;
                    player.inventory.currentItem = foodSlot;
                }

                if (!isEating) {
                    startEating();
                }
            } else if (isEating) {
                stopEating();
            }
        }
    }

    private void startEating() {
        isEating = true;
        if (mc.currentScreen == null) {
            mc.gameSettings.keyBindUseItem.setPressed(true);
        }
    }

    private void stopEating() {
        mc.gameSettings.keyBindUseItem.setPressed(false);
        isEating = false;

        if (previousSlot != -1) {
            mc.player.inventory.currentItem = previousSlot;
            previousSlot = -1;
        }
    }

    private boolean hasFoodInHands() {
        ItemStack mainHand = mc.player.getHeldItemMainhand();
        ItemStack offHand = mc.player.getHeldItemOffhand();

        return (mainHand.getItem() != Items.AIR && mainHand.getItem().isFood())
                || (offHand.getItem() != Items.AIR && offHand.getItem().isFood());
    }

    private int findFoodSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem().isFood()) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        isEating = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        stopEating();
        isEating = false;
    }
}
