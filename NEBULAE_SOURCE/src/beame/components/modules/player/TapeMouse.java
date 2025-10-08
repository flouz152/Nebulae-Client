package beame.components.modules.player;

import beame.util.math.TimerUtil;
import events.Event;
import events.impl.render.EventRender;
import beame.module.Category;
import beame.module.Module;
import beame.setting.SettingList.SliderSetting;

public class TapeMouse extends Module {
// leaked by itskekoff; discord.gg/sk3d ckOwOwI4
    public SliderSetting delay = new SliderSetting("Delay", 1.5f,  .5f,5f, 0.5f);
    public TimerUtil delayTimer = new TimerUtil();

    public TapeMouse() {
        super("Tape Mouse", Category.Player, true, "Автоматический клик мыши.");
        addSettings(delay);
    }

    @Override
    public void event(Event event) {
        if (event instanceof EventRender) {
            long Delay = delay.get().longValue()*1000;
            if (delayTimer.hasReached(Delay)){
                mc.clickMouse();
            }
        }
    }
}
