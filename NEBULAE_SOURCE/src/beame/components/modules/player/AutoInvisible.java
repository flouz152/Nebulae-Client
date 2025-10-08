package beame.components.modules.player;

import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Effects;
import beame.components.command.AbstractCommand;
import beame.util.math.TimerUtil2;
import beame.module.Module;
import beame.module.Category;
import events.Event;
import events.impl.player.EventUpdate;

public class AutoInvisible extends Module {
// leaked by itskekoff; discord.gg/sk3d DJI0c1h4
    private boolean isDrinkingPotion = false;
    private final TimerUtil2 potionTimer = new TimerUtil2();
    protected static final Minecraft mc = Minecraft.getInstance();

    public static boolean IS_DRINKING = false;

    public AutoInvisible() {
        super("Auto Invisble", Category.Player, true, "Автоматичски выпивает зелье невидимости");
    }

    @Override
    public void event(Event event) {
        if (event instanceof EventUpdate) {
            if (mc.player != null) {
                    if (isDrinkingPotion) {
                        stopDrinkingPotion();
                    }
                    return;
                }
                
                if (mc.player.isPotionActive(Effects.INVISIBILITY)) {
                    isDrinkingPotion = false;
                    IS_DRINKING = false;
                } else {
                    if (!isDrinkingPotion) {
                        IS_DRINKING = true;
                        findAndDrinkInvisPotion();
                    }
                }
            }
        }

    private boolean isPlayerEating() {
        return mc.player != null && mc.player.isHandActive() && 
               mc.player.getActiveItemStack().getItem().isFood();
    }
    
    private void stopDrinkingPotion() {
        mc.gameSettings.keyBindUseItem.setPressed(false);
        isDrinkingPotion = false;
        IS_DRINKING = false;
    }

    public boolean isDrinkingPotion() {
        return isDrinkingPotion;
    }

    private void findAndDrinkInvisPotion() {
        final int previousSlot = mc.player.inventory.currentItem;

        int invisPotionSlot = -1;
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.POTION) {
                invisPotionSlot = i;
                break;
            }
        }

        if (invisPotionSlot == -1) {
            AbstractCommand.addMessage("Зелье невидимости не найдено в хотбаре! Модуль выключен.");
            this.toggle();
            isDrinkingPotion = false;
            IS_DRINKING = false;
            return;
        }

        mc.player.inventory.currentItem = invisPotionSlot;
        mc.gameSettings.keyBindUseItem.setPressed(true);
        isDrinkingPotion = true;

        new Thread(() -> {
            try {
                Thread.sleep(1700);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                mc.gameSettings.keyBindUseItem.setPressed(false);
                mc.player.inventory.currentItem = previousSlot;
                mc.player.connection.sendPacket(new CHeldItemChangePacket(previousSlot));
                isDrinkingPotion = false;
                IS_DRINKING = false;
            }
        }).start();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        isDrinkingPotion = false;
        IS_DRINKING = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        isDrinkingPotion = false;
        IS_DRINKING = false;
        if (mc.player != null) {
            mc.gameSettings.keyBindUseItem.setPressed(false);
        }
    }

    public void reset() {
        isDrinkingPotion = false;
        IS_DRINKING = false;
    }

    public static boolean isDrinking() {
        return IS_DRINKING;
    }
}