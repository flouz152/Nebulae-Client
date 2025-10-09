package beame.components.modules.render;

import events.Event;
import events.impl.player.EventMotion;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.util.math.vector.Vector2f;

public class FreeLook extends Module {
// leaked by itskekoff; discord.gg/sk3d yxdYc1KM
    public FreeLook() {
        super("FreeLook", Category.Visuals);
    }

    private Vector2f rotation = new Vector2f(0, 0);

    @Override
    public void event(Event event) {
        if (event instanceof EventMotion) {
            mc.player.rotationYawHead = rotation.x;
            mc.player.renderYawOffset = rotation.x;
            mc.player.rotationPitchHead = rotation.y;
        }
    }

    @Override
    public void onEnable () {
        mc.gameSettings.setPointOfView(PointOfView.THIRD_PERSON_BACK);
        rotation = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
        super.onEnable();
    }


    @Override
    public void onDisable () {
        mc.gameSettings.setPointOfView(PointOfView.FIRST_PERSON);
        rotation = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
        super.onDisable();
    }
}

