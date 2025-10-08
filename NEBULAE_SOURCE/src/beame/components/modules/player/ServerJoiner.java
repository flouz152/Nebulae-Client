package beame.components.modules.player;

import beame.components.command.AbstractCommand;
import beame.util.ClientHelper;
import beame.util.IMinecraft;
import beame.util.math.TimerUtil;
import beame.util.other.JoinerUtils;
import events.Event;
import events.impl.packet.EventPacket;
import events.impl.player.EventUpdate;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import beame.setting.SettingList.RadioSetting;
import beame.setting.SettingList.SliderSetting;

public class ServerJoiner extends Module {
// leaked by itskekoff; discord.gg/sk3d 3PW6C6oX

    private final RadioSetting ModeJoin = new RadioSetting("Выбор сервера", "RW Гриф","RW Гриф", "Мега-Гриф", "ST Дуэли");
    private final SliderSetting griefSelection = new SliderSetting("Номер грифа", 1, 1, 50, 1).setVisible(() -> ModeJoin.get("RW Гриф"));
    private final TimerUtil timerUtil = new TimerUtil();
    private final TimerUtil reopenTimerUtil = new TimerUtil();
    private boolean shouldRetry = false;
    private boolean needToReopenCompass = false;

    public ServerJoiner() {
        super("ServerJoiner", Category.Player, true, "Автоматически заходит на выбранный сервер");
        addSettings(ModeJoin, griefSelection);
    }

    @Override
    protected void onEnable() {
        JoinerUtils.selectCompass();
        IMinecraft.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
        shouldRetry = false;
        needToReopenCompass = false;
        super.onEnable();
    }

    @Override
    public void event(Event event) {
        if (!hasCompassInHand() && !ModeJoin.get("ST Дуэли")) {
            toggle();
            return;
        }

        if (event instanceof EventUpdate) {
            if (needToReopenCompass && reopenTimerUtil.hasReached(500)) {
                IMinecraft.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                mc.playerController.processRightClick(mc.player, mc.world, Hand.MAIN_HAND);
                AbstractCommand.addMessage("§b[ServerJoiner] §fПовторно открываю компасс");
                needToReopenCompass = false;
            }
            
            handleEventUpdate();
        }

        if (event instanceof EventPacket eventPacket) {
            if (eventPacket.getPacket() instanceof SChatPacket packet) {
                String message = TextFormatting.getTextWithoutFormattingCodes(packet.getChatComponent().getString());

                if (message.contains("Прекратите спамить!")) {
                    toggle();
                    return;
                }

                if (message.contains("К сожалению сервер переполнен")
                        || message.contains("Подождите 20 секунд!")
                        || message.contains("большой поток игроков")
                        || (ModeJoin.get("ST Дуэли") && message.contains("Сервер заполнен!"))) {
                    shouldRetry = true;
                    JoinerUtils.selectCompass();
                    IMinecraft.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                }

                if (ModeJoin.get("ST Дуэли") && message.contains("Вы уже подключены на этот сервер!")) {
                    JoinerUtils.selectCompass();
                    IMinecraft.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                    AbstractCommand.addMessage("§b[ServerJoiner] §fОткрываю компасс заново");
                    needToReopenCompass = true;
                    reopenTimerUtil.reset();
                    return;
                }

                if (ModeJoin.get("ST Дуэли") && message.contains("Вы подключились к серверу 1duels")) {
                    shouldRetry = false;
                    needToReopenCompass = false;
                    toggle();
                    AbstractCommand.addMessage("§b[ServerJoiner] §fМодуль отключен: вы успешно подключились к серверу дуэлей");
                }
            }
        }
    }

    private void handleEventUpdate() {
        if (IMinecraft.mc.currentScreen == null) {
            if (shouldRetry && ModeJoin.get("ST Дуэли")) {
                JoinerUtils.selectCompass();
                IMinecraft.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                shouldRetry = false;
            }

            if (IMinecraft.mc.player.ticksExisted < 3) {
                IMinecraft.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            }
        } else if (IMinecraft.mc.currentScreen instanceof ChestScreen) {
            try {
                int numberGrief = griefSelection.get().intValue();

                ContainerScreen container = (ContainerScreen) IMinecraft.mc.currentScreen;
                for (int i = 0; i < container.getContainer().inventorySlots.size(); i++) {
                    String s = container.getContainer().inventorySlots.get(i).getStack().getDisplayName().getString();

                    if (ClientHelper.isConnectedToServer("reallyworld")) {
                        switch (ModeJoin.getIndex()) {
                            case 1 -> {
                                if (s.contains("МЕГА")) {
                                    if (timerUtil.hasTimeElapsed(50)) {
                                        IMinecraft.mc.playerController.windowClick(IMinecraft.mc.player.openContainer.windowId, i, 0, ClickType.PICKUP, IMinecraft.mc.player);
                                        timerUtil.reset();
                                    }
                                }
                            }
                            case 0 -> {
                                if (s.contains("ГРИФЕРСКОЕ ВЫЖИВАНИЕ")) {
                                    if (timerUtil.hasTimeElapsed(50)) {
                                        IMinecraft.mc.playerController.windowClick(IMinecraft.mc.player.openContainer.windowId, i, 0, ClickType.PICKUP, IMinecraft.mc.player);
                                        timerUtil.reset();
                                    }
                                }
                                if (s.contains("ГРИФ #" + numberGrief + " (1.16.5+)")) {
                                    if (timerUtil.hasTimeElapsed(50)) {
                                        IMinecraft.mc.playerController.windowClick(IMinecraft.mc.player.openContainer.windowId, i, 0, ClickType.PICKUP, IMinecraft.mc.player);
                                        timerUtil.reset();
                                    }
                                }
                            }
                            case 2 -> {
                                if (timerUtil.hasTimeElapsed(50)) {
                                    IMinecraft.mc.playerController.windowClick(IMinecraft.mc.player.openContainer.windowId, 14, 0, ClickType.PICKUP, IMinecraft.mc.player);
                                    timerUtil.reset();
                                }
                            }
                        }
                    } else {
                        if (ModeJoin.get("ST Дуэли") && timerUtil.hasTimeElapsed(50)) {
                            IMinecraft.mc.playerController.windowClick(IMinecraft.mc.player.openContainer.windowId, 14, 0, ClickType.PICKUP, IMinecraft.mc.player);
                            timerUtil.reset();
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    private boolean hasCompassInHand() {
        for (int i = 0; i < 9; i++) {
            if (IMinecraft.mc.player.inventory.getStackInSlot(i).getItem() == net.minecraft.item.Items.COMPASS) {
                return true;
            }
        }
        return false;
    }
}