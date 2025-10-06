package beame.components.modules.player;

import beame.components.command.AbstractCommand;
import beame.module.Category;
import beame.module.Module;
import beame.setting.SettingList.BindSetting;
import beame.util.math.TimerUtil;
import beame.util.player.InventoryUtility;
import beame.util.player.KeyboardUtil;
import events.Event;
import events.EventKey;
import events.impl.player.EventUpdate;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;

public class LDHelper extends Module {
    private static final long STAL_USE_INTERVAL_MS = 150L;

    private final BindSetting glowstoneBind = new BindSetting("Glowstone пыль", KeyboardUtil.KEY_PERIOD.keyCode);
    private final BindSetting stalBind = new BindSetting("C418 — stal", -1);

    private final TimerUtil stalTimer = new TimerUtil();
    private boolean stalActive;

    public LDHelper() {
        super("LDHelper", Category.Player, true, "Помощник для быстрого использования LD предметов.");
        addSettings(glowstoneBind, stalBind);
    }

    @Override
    protected void onEnable() {
        stalTimer.reset();
        stalActive = false;
    }

    @Override
    protected void onDisable() {
        stalActive = false;
    }

    @Override
    public void event(Event event) {
        if (mc.player == null || mc.playerController == null || mc.player.connection == null) {
            return;
        }

        if (event instanceof EventKey keyEvent) {
            if (mc.currentScreen instanceof ChatScreen) {
                return;
            }

            if (keyEvent.key == glowstoneBind.get() && !keyEvent.isReleased()) {
                if (!useItemInstant(Items.GLOWSTONE_DUST)) {
                    AbstractCommand.addMessage("Glowstone пыль не найдена!");
                }
            }

            if (keyEvent.key == stalBind.get() && !keyEvent.isReleased()) {
                stalActive = !stalActive;
                if (stalActive) {
                    stalTimer.reset();
                    if (!useItemInstant(Items.MUSIC_DISC_STAL)) {
                        AbstractCommand.addMessage("Пластинка C418 — Stal не найдена!");
                        stalActive = false;
                    }
                }
            }
            return;
        }

        if (event instanceof EventUpdate && stalActive && stalTimer.passed(STAL_USE_INTERVAL_MS)) {
            if (!useItemInstant(Items.MUSIC_DISC_STAL)) {
                AbstractCommand.addMessage("Пластинка C418 — Stal не найдена!");
                stalActive = false;
            } else {
                stalTimer.reset();
            }
        }
    }

    private boolean useItemInstant(Item item) {
        int slot = InventoryUtility.getItemSlot(item);
        if (slot == -1) {
            return false;
        }

        int originalHotbarSlot = mc.player.inventory.currentItem;

        if (slot < 9) {
            if (slot != originalHotbarSlot) {
                mc.player.connection.sendPacket(new CHeldItemChangePacket(slot));
            }
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            if (slot != originalHotbarSlot) {
                mc.player.connection.sendPacket(new CHeldItemChangePacket(originalHotbarSlot));
            }
            return true;
        }

        mc.playerController.pickItem(slot);
        mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
        mc.playerController.pickItem(slot);
        return true;
    }
}
