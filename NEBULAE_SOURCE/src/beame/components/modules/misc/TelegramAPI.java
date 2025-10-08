package beame.components.modules.misc;

import events.Event;
import events.impl.render.EventRender;
import beame.module.Category;
import beame.module.Module;

public class TelegramAPI extends Module {
// leaked by itskekoff; discord.gg/sk3d ntDEXJ6e
    public TelegramAPI() {
        super("Telegram API", Category.Misc);
//        addSettings(tokenInput, beame.setting);
    }
//
//    private final StringSetting tokenInput = new StringSetting("Token34", "token");
//    StringSetting beame.setting = new StringSetting("Numeric Value", "123", true);



    @Override
    public void event(Event event) {
        if (event instanceof EventRender) {

        }
    }
}
