package beame.components.modules.player;

import beame.Essence;
import beame.feature.notify.NotificationManager;
import beame.util.math.TimerUtil;
import events.Event;
import events.impl.packet.EventPacket;
import events.impl.render.EventRender;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.util.text.TextFormatting;
import beame.setting.SettingList.LabelSetting;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AutoTransfer extends Module {
// leaked by itskekoff; discord.gg/sk3d GaE4wEdP
    public final LabelSetting info = new LabelSetting(".transfer <anarchy>", true);

    public AutoTransfer() { super("Auto Transfer", Category.Player); addSettings(info); }

    private final TimerUtil stopWatch = new TimerUtil(), changeServerStopWatch = new TimerUtil();
    private boolean allItemsToSell = false;
    private boolean connectedToServer = false;
    private final List<Item> playerItems = new ArrayList<>();
    private int sellCount = 0;
    private boolean isReadyToSell;

    public int anarchy = -1;

    @Override
    public void event(Event event) {
        if(event instanceof EventPacket){
            if (((EventPacket) event).getPacket() instanceof SChatPacket chatPacket) {
                String chatMessage = chatPacket.getChatComponent().getString().toLowerCase(Locale.ROOT);
                if (chatMessage.contains("?????????? ?????????") && !playerItems.isEmpty()) allItemsToSell = true;
                if (chatMessage.contains("?? ??? ??????????")) connectedToServer = true;
                if (chatMessage.contains("????????? ?? ???????")) {
                    sellCount++;
                }
            }
        }
        if(event instanceof EventRender) {
            if(anarchy == -1){
                Essence.getHandler().notificationManager.pushNotify("Setup anarchy! .transfer <anarchy>", NotificationManager.Type.Info);
                toggle();
                return;
            }

            if (mc.player.ticksExisted < 500 && !isReadyToSell) {
                int ticksRemaining = 500 - mc.player.ticksExisted;
                int secondsRemaining = ticksRemaining / 20;
                toggle();
                return;
            }

            if (mc.ingameGUI.getTabList().header != null) {
                String serverHeader = TextFormatting.getTextWithoutFormattingCodes(mc.ingameGUI.getTabList().header.getString());
                if (serverHeader != null && serverHeader.contains("" + anarchy)) connectedToServer = true;
            }

            int itemCountToSell = 1;

            int sellPrice = 10;

            if (!isReadyToSell) {
                for (int i = 0; i < 9; i++) {
                    if (mc.player.inventory.getStackInSlot(i).getItem() == Items.AIR) {
                        continue;
                    }
                    if (stopWatch.hasTimeElapsed(100)) {
                        mc.player.inventory.currentItem = i;
                        mc.player.sendChatMessage("/ah dsell " + sellPrice);
                        playerItems.add(mc.player.inventory.getStackInSlot(i).getItem());
                        stopWatch.reset();
                    }
                }
            }

            if (sellCount >= itemCountToSell || allItemsToSell) {
                isReadyToSell = true;
                int anarchyNumber = anarchy;

                if (!connectedToServer) {
                    if (changeServerStopWatch.hasTimeElapsed(100)) {
                        mc.player.sendChatMessage("/an" + anarchyNumber);
                        connectedToServer = true;
                        changeServerStopWatch.reset();
                    }
                    return;
                }

                if (mc.player.openContainer instanceof ChestContainer container) {
                    IInventory lowerChestInventory = container.getLowerChestInventory();

                    for (int index = 0; index < lowerChestInventory.getSizeInventory(); ++index) {
                        if (stopWatch.hasTimeElapsed(200) && lowerChestInventory.getStackInSlot(index).getItem() != Items.AIR) {
                            if (playerItems.contains(lowerChestInventory.getStackInSlot(index).getItem())) {
                                mc.playerController.windowClick(container.windowId, index, 0, ClickType.QUICK_MOVE, mc.player);
                                stopWatch.reset();
                            } else {
                                resetAndToggle();
                                toggle();
                            }
                        }
                    }
                } else {
                    if (stopWatch.hasTimeElapsed(500)) {
                        mc.player.sendChatMessage("/ah " + mc.player.getNameClear());
                        stopWatch.reset();
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        resetAndToggle();
        super.onDisable();
    }

    private void resetAndToggle() {
        allItemsToSell = false;
        connectedToServer = false;
        playerItems.clear();
        isReadyToSell = false;
        sellCount = 0;
        anarchy = -1;
    }
}
