package beame.components.modules.render;

import events.Event;
import beame.module.Category;
import beame.module.Module;
import beame.setting.SettingList.RadioSetting;
import beame.setting.SettingList.SliderSetting;

public class CustomFog extends Module {
// leaked by itskekoff; discord.gg/sk3d 7oIY4DYD
    public SliderSetting power = new SliderSetting("Сила", 20F, 1F, 30F, 1F);
    public RadioSetting color = new RadioSetting("Цвет", "Тема", "Тема", "Белый", "Серый");
    public SliderSetting themeBlackness = new SliderSetting("Затемнить тему", 10F, 0F, 50F, 1F).setVisible(() -> color.is("Тема"));

    private float[] currentColor = {1f, 0f, 0f};
    private float[] lastColor = {0f, 0f, 1f};
    private float mixProgress = 0f;
    private static final float COLOR_STEP = 0.005f;

    public CustomFog() {
        super("CustomFog", Category.Visuals, true, "Изменение цвета тумана");
        addSettings(power, color, themeBlackness);
    }

    public int getDepth() {
        return 6;
    }

    @Override
    public void event(Event event) {

    }
}
