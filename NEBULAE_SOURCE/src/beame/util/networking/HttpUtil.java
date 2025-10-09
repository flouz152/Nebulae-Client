package beame.util.networking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class HttpUtil {
// leaked by itskekoff; discord.gg/sk3d Sriom1wc

    public static String get(String link) throws Exception {
        URL url = new URL(link);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        String ret = in.readLine();
        in.close();
        return ret;
    }
}