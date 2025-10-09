package beame.components.modules.player;

import beame.Nebulae;
import events.Event;
import events.impl.render.Render2DEvent;
import beame.module.Category;
import beame.module.Module;
import beame.setting.SettingList.BooleanSetting;

public class NoInteract extends Module {
// leaked by itskekoff; discord.gg/sk3d qCzyhKiY
    public NoInteract() {
        super("NoInteract", Category.Player, true, "Отключает взаимодействия с блоками");
        addSettings(onlyAura);
    }

    public BooleanSetting onlyAura = new BooleanSetting("Только с Attack Aura", false, 0);

    public boolean interactAllowed = false;

    @Override
    public void event(Event event) {
        if (event instanceof Render2DEvent) {
            if (onlyAura.get()) {
                interactAllowed = !Nebulae.getHandler().getModuleList().aura.isState();
            } else {
                interactAllowed = false;
            }
        }
    }
}
