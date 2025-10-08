package beame.components.modules.player;

import events.Event;
import beame.module.Category;
import beame.module.Module;
import beame.setting.SettingList.SliderSetting;

public class ItemScroller extends Module {
// leaked by itskekoff; discord.gg/sk3d bTlpkNQu
    public ItemScroller() {
        super("ItemScroller", Category.Player, true, "Ускоренный забор предметов из контейнера");
        addSettings(cooldown);
    }

    public final SliderSetting cooldown = new SliderSetting("Задержка", 50, 0, 150, 10);

    @Override
    public void event(Event event) {

    }
}
