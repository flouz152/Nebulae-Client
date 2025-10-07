package beame.util;

import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.ClientBossInfo;

import java.util.Map;
import java.util.UUID;

@UtilityClass
public class ClientHelper implements IMinecraft {
// leaked by itskekoff; discord.gg/sk3d 2aCgcABa

    public String getServerIP() {
        if (mc.world == null) return "mainmenu";

        if (mc.isSingleplayer()) return "local";

        if (mc.getCurrentServerData() != null) {
            return mc.getCurrentServerData().serverIP.toLowerCase();
        }

        return "";
    }

    public boolean isPvP() {
        for (Map.Entry<UUID, ClientBossInfo> bossInfo : mc.ingameGUI.getBossOverlay().getMapBossInfos().entrySet()) {
            if (bossInfo.getValue().getName().getString().toLowerCase().contains("pvp") || bossInfo.getValue().getName().getString().toLowerCase().contains("пвп")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isConnectedToServer(String ip) {
        return mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP != null && mc.getCurrentServerData().serverIP.contains(ip);
    }

    public static boolean isFuntime() {
        return isConnectedToServer("funtime");
    }
}
