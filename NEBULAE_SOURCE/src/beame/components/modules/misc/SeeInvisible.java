package beame.components.modules.misc;

import events.Event;
import beame.module.Category;
import beame.module.Module;
import beame.setting.SettingList.SliderSetting;

public class SeeInvisible extends Module {
// leaked by itskekoff; discord.gg/sk3d UK4mUPId
    public final SliderSetting alpha = new SliderSetting("Прозрачность",0.5F, 0.3F, 1.0F, 0.1F);

    public SeeInvisible() {
        super("SeeInvisible", Category.Misc, true, "Помогает видеть невидимых игроков");
        addSettings(alpha);
    }

    @Override
    public void event(Event event) {

    }
}
