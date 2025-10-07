package beame.components.modules.render;

import events.Event;
import events.impl.render.Render3DLastEvent;
import beame.module.Category;
import beame.module.Module;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.RadioSetting;
import beame.setting.SettingList.SliderSetting;

import java.util.*;

public class Ambience extends Module {
// leaked by itskekoff; discord.gg/sk3d GSEhqBEk
    public Ambience() {
        super("Ambience", Category.Visuals, true, "Изменение времени дня");
        addSettings(fullBright, timeSetting, timeValue);
    }

    private final BooleanSetting fullBright = new BooleanSetting("Полная яркость", true, 0);

    private final RadioSetting timeSetting = new RadioSetting("Настройка времени",
            "Как на компьютере",
            "Как на компьютере",
            "Выбор"
    );

    private final SliderSetting timeValue = new SliderSetting("Время", 
            12f, 0f, 24f, 0.5f
    ).setVisible(() -> timeSetting.get("Выбор"));

    @Override
    public void event(Event event) {
        if(event instanceof Render3DLastEvent) {
            mc.gameSettings.gamma = fullBright.get() ? 1488 : mc.gameSettings.gamma;

            if(timeSetting.is("Как на компьютере")) {
                if (mc.world != null) {
                    Calendar calendar = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);

                    long timeInTicks = (hour % 24) * 1000;

                    mc.world.setDayTime(timeInTicks);
                }
            } else if(timeSetting.is("Выбор")) {
                if (mc.world != null) {
                    long timeInTicks = (long) (timeValue.get() * 1000);
                    mc.world.setDayTime(timeInTicks);
                }
            }
        }
    }

    @Override
    protected void onEnable() {
        super.onEnable();
    }
}
