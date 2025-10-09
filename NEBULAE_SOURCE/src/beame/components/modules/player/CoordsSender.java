package beame.components.modules.player;

import beame.Nebulae;
import events.Event;
import events.EventKey;
import events.impl.player.EventUpdate;
import events.impl.packet.EventPacket;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.util.text.ITextComponent;
import beame.setting.SettingList.BindSetting;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.SliderSetting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoordsSender extends Module {
// leaked by itskekoff; discord.gg/sk3d xZMtimye
    private static long lastCoordsSent = 0;
    private static final long COORDS_COOLDOWN = 4000;
    public BindSetting enter = new BindSetting("Отправить", 0);
    public final BooleanSetting friendsGps = new BooleanSetting("/GPS на друзей", false);
    public BooleanSetting autoSend = new BooleanSetting("Отправлять автоматически", true);
    public SliderSetting lowHpThreshold = new SliderSetting("Здоровье", 4, 1, 20, 1).setVisible(() -> autoSend.get());

    public CoordsSender() {
        super("CoordsSender", Category.Player, true, "Автоматически отправляет ваши коордианты в чат");
        addSettings(autoSend, lowHpThreshold, enter, friendsGps);
    }

    @Override
    public void event(Event event) {
        if (event instanceof EventKey) {
            EventKey eventKey = (EventKey) event;
            if (eventKey.key == enter.get()) {
                sendCoordinates();
            }
        }

        if (event instanceof EventUpdate) {
            if (autoSend.get() && mc.player != null && mc.player.getHealth() <= lowHpThreshold.get()) {
                sendCoordinates();
            }
        }

        if (event instanceof EventPacket) {
            EventPacket e = (EventPacket) event;
            
            if (e.getPacket() instanceof SChatPacket && friendsGps.get()) {
                SChatPacket packet = (SChatPacket) e.getPacket();
                ITextComponent component = packet.getChatComponent();
                String message = component.getString();
                
                if (message.contains("⇨")) {
                    String playerName = "";
                    int arrowIndex = message.indexOf("⇨");
                    
                    if (arrowIndex > 0) {
                        String beforeArrow = message.substring(0, arrowIndex).trim();
                        String[] parts = beforeArrow.split("\\s+");
                        if (parts.length > 0) {
                            playerName = parts[parts.length - 1].trim();
                            playerName = playerName.replaceAll("[^a-zA-Z0-9_]", "");
                        }
                    }

                    if (!playerName.isEmpty() && Nebulae.getHandler().friends.isFriend(playerName)) {
                        String afterArrow = message.substring(arrowIndex + 1).trim();
                        Pattern pattern = Pattern.compile("(-?\\d+)\\s+(-?\\d+)\\s+(-?\\d+)");
                        Matcher matcher = pattern.matcher(afterArrow);

                        if (matcher.find()) {
                            String x = matcher.group(1);
                            String y = matcher.group(2);
                            String z = matcher.group(3);
                            mc.player.sendChatMessage(".gps " + x + " " + z);
                        }
                    }
                }
            }
        }
    }

    private void sendCoordinates() {
        if (mc.player == null) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCoordsSent < COORDS_COOLDOWN) return;
        lastCoordsSent = currentTime;

        int x = (int) mc.player.getPosX();
        int y = (int) mc.player.getPosY();
        int z = (int) mc.player.getPosZ();
        String coords = String.format("%d %d %d", x, y, z);

        mc.player.sendChatMessage("!" + coords);

        new Thread(() -> {
            try {
                Thread.sleep(300);
                mc.player.sendChatMessage(coords);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}