package beame.components.modules.player;

import beame.util.math.TimerUtil;
import events.Event;
import events.impl.player.EventUpdate;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.item.Items;

public class FastEXP extends Module {
// leaked by itskekoff; discord.gg/sk3d JgRX1MFg
    public FastEXP() {
        super("FastEXP", Category.Player, true, "Ускоренное выбрасывание пузырьков опыта");
    }

    TimerUtil timer = new TimerUtil();

    @Override
    public void event(Event event) {
        if(event instanceof EventUpdate) {
            assert mc.player != null;
            if(mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE) {
                mc.rightClickDelayTimer = 0;
            }
        }
    }
}
