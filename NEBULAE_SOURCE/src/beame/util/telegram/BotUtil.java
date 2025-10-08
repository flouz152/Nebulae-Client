package beame.util.telegram;

import beame.util.telegram.API.TGBot;

public class BotUtil {
// leaked by itskekoff; discord.gg/sk3d sgO5wiVP
    public TGBot bot;
    public String url;
    public String chat_id;

    public boolean inited = false;

    public BotUtil(String token, String chat_id) {
        this.bot = new TGBot(token);
        this.url = TGBot.url;
        this.chat_id = chat_id;
        inited = true;
    }

    public void Init() { this.testMessage(); }

    public void sendMessage(String content){
        if(bot == null){ return; }

        try {
            this.bot.sendMessage(this.chat_id, content);
        } catch (Exception exception) {}
    }

    public void testMessage() {
        if(bot == null){ return; }

        try {
            this.bot.sendMessage(this.chat_id, "Test Telegram API message!");
        } catch (Exception exception) {}
    }
}