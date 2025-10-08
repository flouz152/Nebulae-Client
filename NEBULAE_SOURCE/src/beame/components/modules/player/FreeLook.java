package beame.components.modules.player;

import events.Event;
import events.impl.player.EventMotion;
import events.impl.player.RotationEvent;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.util.math.vector.Vector2f;

public class FreeLook extends Module {
// leaked by itskekoff; discord.gg/sk3d YP5RgXFA

    public FreeLook() {
        super("FreeLook", Category.Player, true, "Свободный просмотр модели игрока");
    }

    private Vector2f rotation;

    @Override
    public void event(Event event) {
        if(event instanceof RotationEvent e) {
            e.setYaw(rotation.y);
            e.setPitch(rotation.x);
        }

        if(event instanceof EventMotion e) {
            e.setYaw(rotation.y);
            e.setPitch(rotation.x);

            assert mc.player != null;
//            mc.player.yBodyRot = rotation.y;
//            mc.player.yBodyRot = rotation.y;
//            mc.player.xBodyRot0 = rotation.x;
        }
    }


    @Override
    public void onEnable() {
        assert mc.player != null;
        this.rotation = mc.player.getPitchYaw();
    }

    @Override
    public void onDisable() {
        assert mc.player != null;
        mc.player.rotationYaw = rotation.x;
        mc.player.rotationPitch = rotation.y;
    }
}
