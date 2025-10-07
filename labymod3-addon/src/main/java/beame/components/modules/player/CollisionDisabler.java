package beame.components.modules.player;

import events.Event;
import beame.module.Category;
import beame.module.Module;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.EnumSetting;

public class CollisionDisabler extends Module {
// leaked by itskekoff; discord.gg/sk3d kXaCyvAc
    public CollisionDisabler() {
        super("CollisionDisabler", Category.Player, true, "Отключает коллизию");
        addSettings(addtivites, randomize);
    }

    public final EnumSetting addtivites = new EnumSetting("Отключать коллизию",
            new BooleanSetting("На паутине", true),
            new BooleanSetting("На сладких ягодах", true));

    public final BooleanSetting randomize = new BooleanSetting("Рандомизация", false).setVisible(() -> addtivites.get(0).get());

    @Override
    public void event(Event event) {

    }
}
