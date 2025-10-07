package beame.components.modules.misc;

import events.Event;
import beame.module.Category;
import beame.module.Module;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PlayMusic extends Module {
// leaked by itskekoff; discord.gg/sk3d GIdquh8E
    public PlayMusic() {
        super("MusicPlayer", Category.Misc, true, "Проигрывание установленной музыки.");
    }

    @Override
    public void event(Event event) {
        String apiUrl = "https://api.music.yandex.net/v2.0/some_endpoint";
        String token = "";

        URL url = null;
        try {
            url = new URL(apiUrl + "?access_token=" + token);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            con.setRequestMethod("GET");
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        }

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String inputLine;
        StringBuilder content = new StringBuilder();

        while (true) {
            try {
                if (!((inputLine = in.readLine()) != null)) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            content.append(inputLine);
        }

        try {
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(content.toString());
    }
}
