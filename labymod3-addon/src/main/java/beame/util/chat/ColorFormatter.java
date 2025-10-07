package beame.util.chat;

import net.minecraft.util.text.TextFormatting;

import java.util.HashMap;
import java.util.Map;

public class ColorFormatter {
// leaked by itskekoff; discord.gg/sk3d yZWSVRJH
    private static HashMap<String, String> formats = new HashMap<String, String>() {{
        put("${black}", TextFormatting.BLACK + "");
        put("${dark_blue}", TextFormatting.DARK_BLUE + "");
        put("${dark_green}", TextFormatting.DARK_GREEN + "");
        put("${dark_aqua}", TextFormatting.DARK_AQUA + "");
        put("${dark_red}", TextFormatting.DARK_RED + "");
        put("${dark_purple}", TextFormatting.DARK_PURPLE + "");
        put("${orange}", TextFormatting.GOLD + "");
        put("${gray}", TextFormatting.GRAY + "");
        put("${dark_gray}", TextFormatting.DARK_GRAY + "");
        put("${blue}", TextFormatting.BLUE + "");
        put("${green}", TextFormatting.GREEN + "");
        put("${aqua}", TextFormatting.AQUA + "");
        put("${red}", TextFormatting.RED + "");
        put("${purple}", TextFormatting.LIGHT_PURPLE + "");
        put("${yellow}", TextFormatting.YELLOW + "");
        put("${white}", TextFormatting.WHITE + "");
        put("${bold}", TextFormatting.BOLD + "");
        put("${reset}", TextFormatting.RESET + "");
    }};

    public static String get(String input) {
        String finish = input;
        for(Map.Entry<String, String> format : formats.entrySet()) {
            finish = finish.replace(format.getKey(), format.getValue());
        }
        return finish;
    }
}
