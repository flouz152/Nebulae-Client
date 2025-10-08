package beame.util.server;

import net.minecraft.client.Minecraft;

import java.util.Objects;

public class GetCurrentServer {
// leaked by itskekoff; discord.gg/sk3d r4p9a0NF

    private final Minecraft mc = Minecraft.getInstance();

    private final static String funtime = "funtime.su";

    public String getFuntime() {
        return funtime;
    }

    public boolean isFuntime() {
        assert mc.getCurrentServerData() != null;
        if (mc.getCurrentServerData().serverIP != null) {
            return mc.getCurrentServerData().serverIP.contains(getFuntime());
        } else {
            return false;
        }
    }

    public boolean isReallyWorld() {
        return true;
    }

}
