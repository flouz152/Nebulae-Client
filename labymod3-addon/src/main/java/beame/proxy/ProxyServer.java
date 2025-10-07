package beame.proxy;


import net.minecraft.client.gui.widget.button.Button;

public class ProxyServer {
// leaked by itskekoff; discord.gg/sk3d QeRgXMiM
    public static boolean proxyEnabled = false;
    public static Proxy proxy = new Proxy();
    public static Proxy lastUsedProxy = new Proxy();
    public static Button proxyMenuButton;

    public static String getLastUsedProxyIp() {
        return lastUsedProxy.ipPort.isEmpty() ? "none" : lastUsedProxy.getIp();
    }
}
