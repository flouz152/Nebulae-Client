package beame.components.modules.render;

import events.Event;
import beame.module.Category;
import beame.module.Module;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.RadioSetting;
import beame.setting.SettingList.SliderSetting;

public class AspectRatio extends Module {
// leaked by itskekoff; discord.gg/sk3d hVopTSIu

    public AspectRatio() {
        super("AspectRatio", Category.Visuals, true, "Растягивание экрана по X");
        addSettings(aspectRatio, aspectRatioSwitcher, aspectRatioModes);
    }

    public final SliderSetting aspectRatio = new SliderSetting("Aspect Ratio", 1.0f, 0.5f, 2.0f, 0.05f);
    public final BooleanSetting aspectRatioSwitcher = new BooleanSetting("Стандартные разрешения", true);
    public final RadioSetting aspectRatioModes = new RadioSetting("Тип размера", "16:10", "16:10", "16:9", "4:3");

    @Override
    public void event(Event event) {
        if (aspectRatioSwitcher.get()) {
            aspectRatio.setVisible(() -> false);
            aspectRatioModes.setVisible(() -> true);
            if (aspectRatioModes.get("16:10")) {
                aspectRatio.set(1.6F);
            } else if (aspectRatioModes.get("16:9")) {
                aspectRatio.set(1.7F);
            } else if (aspectRatioModes.get("4:3")) {
                aspectRatio.set(1.3F);
            }
        } else {
            aspectRatioModes.setVisible(() -> false);
            aspectRatio.setVisible(() -> true);
        }
    }
}
