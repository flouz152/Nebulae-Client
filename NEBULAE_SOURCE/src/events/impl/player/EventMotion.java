package events.impl.player;

import com.mojang.blaze3d.matrix.MatrixStack;
import events.Event;
import events.impl.render.EventRender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.client.MainWindow;
import net.minecraft.util.math.vector.Matrix4f;

@Data
@AllArgsConstructor
public class EventMotion extends Event {
// leaked by itskekoff; discord.gg/sk3d YrdBhuYB
    private double x, y, z;
    private float yaw, pitch;
    private boolean onGround;

    Runnable postMotion;
}
