package beame.util.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HTTPUtil {
// leaked by itskekoff; discord.gg/sk3d ClYWM8I6
    public static String get(String link) throws Exception {
        URL url = new URL(link);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
        String ret = in.readLine();
        in.close();
        return ret;
    }
}
