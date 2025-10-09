package beame.util.other;

import org.lwjgl.glfw.GLFWNativeWin32;
import com.sun.jna.*;
import com.sun.jna.platform.win32.WinDef.*;
import com.sun.jna.win32.*;

public class WindowStyle {
// leaked by itskekoff; discord.gg/sk3d 8EdaZtCX
    public interface DwmApi extends StdCallLibrary {
        DwmApi INSTANCE = Native.loadLibrary("dwmapi", DwmApi.class);
        int DwmSetWindowAttribute(HWND hwnd, int dwAttribute, Pointer pvAttribute, int cbAttribute);
    }

    public static void setDarkMode(long windowHandle) {
        long hwnd = GLFWNativeWin32.glfwGetWin32Window(windowHandle);
        HWND hwndJna = new HWND(new Pointer(hwnd));

        int DWMWA_USE_IMMERSIVE_DARK_MODE = 20;
        Memory darkModeEnabled = new Memory(4);
        darkModeEnabled.setInt(0, 1);

        DwmApi.INSTANCE.DwmSetWindowAttribute(hwndJna, DWMWA_USE_IMMERSIVE_DARK_MODE, darkModeEnabled, 4);
    }
}
