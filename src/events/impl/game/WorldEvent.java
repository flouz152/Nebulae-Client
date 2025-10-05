package events.impl.game;

import com.mojang.blaze3d.matrix.MatrixStack;
import events.Event;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorldEvent extends Event {
// leaked by itskekoff; discord.gg/sk3d 6HRYDsIy
    private MatrixStack stack;
    private float partialTicks;

    public WorldEvent(MatrixStack stack, float partialTicks) {
        this.stack = stack;
        this.partialTicks = partialTicks;
    }
}