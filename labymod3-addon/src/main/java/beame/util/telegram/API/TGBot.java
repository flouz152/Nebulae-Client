package beame.util.telegram.API;

public class TGBot {
// leaked by itskekoff; discord.gg/sk3d oDQio7st

    public static String token;

    public static String url;

    public TGBot(String token) {
        TGBot.token = token;
        url = "https://api.telegram.org/bot" + token + "/";
    }

    public void sendMessage(String chat_id, String text) throws Exception {
        TGUtils.requestProperty prop_chat_id = new TGUtils.requestProperty("chat_id", chat_id + "");
        TGUtils.requestProperty prop_text = new TGUtils.requestProperty("text", text + "");
        TGNetworking.Post.createRequest("sendMessage", prop_chat_id, prop_text);
    }

    public void sendMessage(int chat_id, String text) throws Exception {
        this.sendMessage("" + chat_id, text);
    }
}
