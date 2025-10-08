package beame.util.schedules;

import beame.util.http.HTTPUtil;

public class Schedules {
// leaked by itskekoff; discord.gg/sk3d 0NL1wWgJ
    static String url = "http://185.198.152.58:25555/sigmaschedule";
    static String bebra = "";

    public static String getEvents() {
        try {
            bebra = HTTPUtil.get(url);
        }
        catch (Exception ex){
            return null;
        }

        if(bebra == "-" || bebra == ""){
            return null;
        }

        return bebra;
    }
}
