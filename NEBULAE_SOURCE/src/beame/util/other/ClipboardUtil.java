package beame.util.other;

import beame.util.IMinecraft;
import net.minecraft.client.gui.fonts.TextInputUtil;

public class ClipboardUtil {
// leaked by itskekoff; discord.gg/sk3d utnrs8ET
    public static String getString() {
        String result = TextInputUtil.getClipboardText(IMinecraft.mc);
        if (result == "" || result == null) {
            return "";
        }
        return result;
    }
}
