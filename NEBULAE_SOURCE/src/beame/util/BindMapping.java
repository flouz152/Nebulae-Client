package beame.util;

import net.minecraft.client.resources.I18n;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class BindMapping {
// leaked by itskekoff; discord.gg/sk3d mwNm669e
    private static final Map<String, Integer> keyMap = new HashMap<>();
    private static final Map<Integer, String> reverseKeyMap = new HashMap<>();

    public static String getKey(int integer) {
        if (integer < 0) {
            return switch (integer) {
                case -100 -> I18n.format("key.mouse.left");
                case -99 -> I18n.format("key.mouse.right");
                case -98 -> I18n.format("key.mouse.middle");
                default -> "MOUSE" + (integer + 101);
            };
        } else {
            return BindMapping.getReverseKey(integer);
        }
    }

    public static String getReverseKey(int key) {
        return reverseKeyMap.getOrDefault(key, "");
    }


    public static Integer getKey(String key) {
        return keyMap.getOrDefault(key, -1);
    }

    static {
        putMappings();
        reverseMappings();
    }

    private static void putMappings() {
        keyMap.put("A", GLFW.GLFW_KEY_A);
        keyMap.put("B", GLFW.GLFW_KEY_B);
        keyMap.put("C", GLFW.GLFW_KEY_C);
        keyMap.put("D", GLFW.GLFW_KEY_D);
        keyMap.put("E", GLFW.GLFW_KEY_E);
        keyMap.put("F", GLFW.GLFW_KEY_F);
        keyMap.put("G", GLFW.GLFW_KEY_G);
        keyMap.put("H", GLFW.GLFW_KEY_H);
        keyMap.put("I", GLFW.GLFW_KEY_I);
        keyMap.put("J", GLFW.GLFW_KEY_J);
        keyMap.put("K", GLFW.GLFW_KEY_K);
        keyMap.put("L", GLFW.GLFW_KEY_L);
        keyMap.put("M", GLFW.GLFW_KEY_M);
        keyMap.put("N", GLFW.GLFW_KEY_N);
        keyMap.put("O", GLFW.GLFW_KEY_O);
        keyMap.put("P", GLFW.GLFW_KEY_P);
        keyMap.put("Q", GLFW.GLFW_KEY_Q);
        keyMap.put("R", GLFW.GLFW_KEY_R);
        keyMap.put("S", GLFW.GLFW_KEY_S);
        keyMap.put("T", GLFW.GLFW_KEY_T);
        keyMap.put("U", GLFW.GLFW_KEY_U);
        keyMap.put("V", GLFW.GLFW_KEY_V);
        keyMap.put("W", GLFW.GLFW_KEY_W);
        keyMap.put("X", GLFW.GLFW_KEY_X);
        keyMap.put("Y", GLFW.GLFW_KEY_Y);
        keyMap.put("Z", GLFW.GLFW_KEY_Z);
        keyMap.put("0", GLFW.GLFW_KEY_0);
        keyMap.put("1", GLFW.GLFW_KEY_1);
        keyMap.put("2", GLFW.GLFW_KEY_2);
        keyMap.put("3", GLFW.GLFW_KEY_3);
        keyMap.put("4", GLFW.GLFW_KEY_4);
        keyMap.put("5", GLFW.GLFW_KEY_5);
        keyMap.put("6", GLFW.GLFW_KEY_6);
        keyMap.put("7", GLFW.GLFW_KEY_7);
        keyMap.put("8", GLFW.GLFW_KEY_8);
        keyMap.put("9", GLFW.GLFW_KEY_9);
        keyMap.put("F1", GLFW.GLFW_KEY_F1);
        keyMap.put("F2", GLFW.GLFW_KEY_F2);
        keyMap.put("F3", GLFW.GLFW_KEY_F3);
        keyMap.put("F4", GLFW.GLFW_KEY_F4);
        keyMap.put("F5", GLFW.GLFW_KEY_F5);
        keyMap.put("F6", GLFW.GLFW_KEY_F6);
        keyMap.put("F7", GLFW.GLFW_KEY_F7);
        keyMap.put("F8", GLFW.GLFW_KEY_F8);
        keyMap.put("F9", GLFW.GLFW_KEY_F9);
        keyMap.put("F10", GLFW.GLFW_KEY_F10);
        keyMap.put("F11", GLFW.GLFW_KEY_F11);
        keyMap.put("F12", GLFW.GLFW_KEY_F12);
        keyMap.put("N1", GLFW.GLFW_KEY_KP_1);
        keyMap.put("N2", GLFW.GLFW_KEY_KP_2);
        keyMap.put("N3", GLFW.GLFW_KEY_KP_3);
        keyMap.put("N4", GLFW.GLFW_KEY_KP_4);
        keyMap.put("N5", GLFW.GLFW_KEY_KP_5);
        keyMap.put("N6", GLFW.GLFW_KEY_KP_6);
        keyMap.put("N7", GLFW.GLFW_KEY_KP_7);
        keyMap.put("N8", GLFW.GLFW_KEY_KP_8);
        keyMap.put("N9", GLFW.GLFW_KEY_KP_9);
        keyMap.put("SPACE", GLFW.GLFW_KEY_SPACE);
        keyMap.put("EN", GLFW.GLFW_KEY_ENTER);
        keyMap.put("ESC", GLFW.GLFW_KEY_ESCAPE);
        keyMap.put("HM", GLFW.GLFW_KEY_HOME);
        keyMap.put("INS", GLFW.GLFW_KEY_INSERT);
        keyMap.put("DEL", GLFW.GLFW_KEY_DELETE);
        keyMap.put("END", GLFW.GLFW_KEY_END);
        keyMap.put("PU", GLFW.GLFW_KEY_PAGE_UP);
        keyMap.put("PD", GLFW.GLFW_KEY_PAGE_DOWN);
        keyMap.put(">", GLFW.GLFW_KEY_RIGHT);
        keyMap.put("<", GLFW.GLFW_KEY_LEFT);
        keyMap.put("v", GLFW.GLFW_KEY_DOWN);
        keyMap.put("^", GLFW.GLFW_KEY_UP);
        keyMap.put("RS", GLFW.GLFW_KEY_RIGHT_SHIFT);
        keyMap.put("LS", GLFW.GLFW_KEY_LEFT_SHIFT);
        keyMap.put("RC", GLFW.GLFW_KEY_RIGHT_CONTROL);
        keyMap.put("LC", GLFW.GLFW_KEY_LEFT_CONTROL);
        keyMap.put("RA", GLFW.GLFW_KEY_RIGHT_ALT);
        keyMap.put("LA", GLFW.GLFW_KEY_LEFT_ALT);
        keyMap.put("RSuper", GLFW.GLFW_KEY_RIGHT_SUPER);
        keyMap.put("LSuper", GLFW.GLFW_KEY_LEFT_SUPER);
        keyMap.put("Menu", GLFW.GLFW_KEY_MENU);
        keyMap.put("CAPS", GLFW.GLFW_KEY_CAPS_LOCK);
        keyMap.put("NUM", GLFW.GLFW_KEY_NUM_LOCK);
        keyMap.put("SCR", GLFW.GLFW_KEY_SCROLL_LOCK);
        keyMap.put("KPDEC", GLFW.GLFW_KEY_KP_DECIMAL);
        keyMap.put("KPDIV", GLFW.GLFW_KEY_KP_DIVIDE);
        keyMap.put("KPMULT", GLFW.GLFW_KEY_KP_MULTIPLY);
        keyMap.put("KPSUB", GLFW.GLFW_KEY_KP_SUBTRACT);
        keyMap.put("KPADD", GLFW.GLFW_KEY_KP_ADD);
        keyMap.put("KPENTER", GLFW.GLFW_KEY_KP_ENTER);
        keyMap.put("KPEQUAL", GLFW.GLFW_KEY_KP_EQUAL);
        keyMap.put("'", GLFW.GLFW_KEY_APOSTROPHE);
        keyMap.put("/", GLFW.GLFW_KEY_SLASH);
        keyMap.put("-", GLFW.GLFW_KEY_MINUS);
        keyMap.put("+", GLFW.GLFW_KEY_EQUAL);
        keyMap.put("BS", GLFW.GLFW_KEY_BACKSPACE);
        keyMap.put("\\", GLFW.GLFW_KEY_BACKSLASH);
        keyMap.put(".", GLFW.GLFW_KEY_PERIOD);
        keyMap.put("CO", GLFW.GLFW_KEY_COMMA);
        keyMap.put("PAU", GLFW.GLFW_KEY_PAUSE);
        keyMap.put("TAB", GLFW.GLFW_KEY_TAB);
        keyMap.put("`", 96);
    }

    private static void reverseMappings() {
        for (Map.Entry<String, Integer> entry : keyMap.entrySet()) {
            reverseKeyMap.put(entry.getValue(), entry.getKey());
        }
    }
}

