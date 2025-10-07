package beame.components.modules.player;

import events.Event;
import beame.module.Category;
import beame.module.Module;
import beame.setting.SettingList.BooleanSetting;

public class NoPush extends Module {
// leaked by itskekoff; discord.gg/sk3d PNpTrsgt
    public NoPush() {
        super("No Push", Category.Player, true, "Отключает выталкивание игрока из блоков");
        addSettings(players, blocks);
    }

    public BooleanSetting players = new BooleanSetting("Игроки", true, 0);
    public BooleanSetting blocks = new BooleanSetting("Блоки", true, 0);

    @Override
    public void event(Event event) {

    }
}
