package beame.components.modules.render;

import events.Event;
import beame.module.Category;
import beame.module.Module;
import beame.setting.SettingList.SliderSetting;

public class FieldOfView extends Module {
// leaked by itskekoff; discord.gg/sk3d E7owKnV5
    public FieldOfView() {
        super("Field Of View", Category.Visuals, true, "Модифицирует увеличение поле-зрения игрока");
        addSettings(fov);
    }

    public final SliderSetting fov = new SliderSetting("Сила", 70.0F, 10.0F, 200.0F, 0.1F);

    @Override
    public void event(Event event) { }
}
