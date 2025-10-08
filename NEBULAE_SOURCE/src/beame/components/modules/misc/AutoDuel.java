package beame.components.modules.misc;

import beame.util.math.TimerUtil;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import events.Event;
import events.impl.packet.EventPacket;
import events.impl.player.EventUpdate;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SChatPacket;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.EnumSetting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AutoDuel extends Module {
// leaked by itskekoff; discord.gg/sk3d eW32iDG6

    public AutoDuel() {
        super("AutoDuel", Category.Misc, true, "Автоматическое отправление дуэлей");
        addSettings(mode);
    }


    public final EnumSetting mode = new EnumSetting("Киты",
            new BooleanSetting("Щит", false),
            new BooleanSetting("Шипы 3", false),
            new BooleanSetting("Лук", false),
            new BooleanSetting("Тотемы", false),
            new BooleanSetting("Нодебафф", false),
            new BooleanSetting("Шары", true),
            new BooleanSetting("Классик", false),
            new BooleanSetting("Читерский рай", false),
            new BooleanSetting("Незерка", false));

    private final ReallyWorld reallyWorld = new ReallyWorld();

    @Override
    public void event(Event event) {
        if (event instanceof EventUpdate eventUpdate) {
            reallyWorld.handleUpdateEvent(eventUpdate);
        }

        if (event instanceof EventPacket eventPacket) {
            reallyWorld.handlePacketEvent(eventPacket);
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        reallyWorld.sent.clear();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private class ReallyWorld {

        private final Pattern pattern = Pattern.compile("^\\w{3,16}$");
        private final List<String> sent = Lists.newArrayList();
        private final TimerUtil counter = new TimerUtil();
        private final TimerUtil counter2 = new TimerUtil();
        private final TimerUtil counterChoice = new TimerUtil();
        private final TimerUtil counterTo = new TimerUtil();

        public void handleUpdateEvent(EventUpdate updateEvent) {
            final List<String> players = getOnlinePlayers();

            if (counter2.hasReached(800L * players.size())) {
                sent.clear();
                counter2.reset();
            }

            for (final String player : players) {
                if (!sent.contains(player) && !player.equals(mc.session.getProfile().getName())) {
                    if (counter.hasReached(1000)) {
                        mc.player.sendChatMessage("/duel " + player);
                        sent.add(player);
                        counter.reset();
                    }
                }
            }

            if (mc.player.openContainer instanceof ChestContainer chest) {
                if (mc.currentScreen.getTitle().getString().contains("Выбор набора (1/1)")) {
                    handleKitSelection(chest);
                } else if (mc.currentScreen.getTitle().getString().contains("Настройка поединка")) {
                    handleDuelSetup(chest);
                }
            }
        }

        public void handlePacketEvent(EventPacket packetEvent) {
            if (packetEvent.isReceivePacket()) {
                IPacket<?> packet = packetEvent.getPacket();

                if (packet instanceof SChatPacket chat) {
                    final String text = chat.getChatComponent().getString().toLowerCase();
                    if ((text.contains("начало") && text.contains("через") && text.contains("секунд!")) ||
                            (text.equals("дуэли » во время поединка запрещено использовать команды"))) {
                        toggle();
                    }
                }
            }
        }

        private List<String> getOnlinePlayers() {
            return mc.player.connection.getPlayerInfoMap().stream()
                    .map(NetworkPlayerInfo::getGameProfile)
                    .map(GameProfile::getName)
                    .filter(profileName -> pattern.matcher(profileName).matches())
                    .collect(Collectors.toList());
        }

        private void handleKitSelection(ChestContainer chest) {
            final List<Integer> slotsID = new ArrayList<>();
            int index = 0;

            for (BooleanSetting value : mode.get()) {
                if (!value.get()) {
                    index++;
                    continue;
                }
                slotsID.add(index);
                index++;
            }

            Collections.shuffle(slotsID);
            final int slotID = slotsID.get(0);

            if (counterChoice.hasReached(90)) {
                mc.playerController.windowClick(chest.windowId, slotID, 0, ClickType.QUICK_MOVE, mc.player);
                counterChoice.reset();
            }
        }

        private void handleDuelSetup(ChestContainer chest) {
            if (counterTo.hasReached(90)) {
                mc.playerController.windowClick(chest.windowId, 0, 0, ClickType.QUICK_MOVE, mc.player);
                counterTo.reset();
            }
        }
    }
}