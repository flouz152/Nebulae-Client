package events.impl.render;


import com.mojang.blaze3d.matrix.MatrixStack;
import events.Event;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.MainWindow;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.math.vector.Matrix4f;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRender extends Event {
// leaked by itskekoff; discord.gg/sk3d oMtE3ya8
    public float partialTicks;
    public MainWindow scaledResolution;
    public Type type;
    public MatrixStack matrixStack;
    public Matrix4f matrix;
    public ActiveRenderInfo activeRenderInfo;

    public EventRender(float partialTicks, MatrixStack stack, MainWindow scaledResolution, Type type,Matrix4f matrix, ActiveRenderInfo activeRenderInfo) {
        this.partialTicks = partialTicks;
        this.scaledResolution = scaledResolution;
        this.matrixStack = stack;
        this.type = type;
        this.matrix = matrix;
        this.activeRenderInfo = activeRenderInfo;
    }

    public boolean isRender3D() {
        return this.type == Type.RENDER3D;
    }

    public boolean isRender2D() {
        return this.type == Type.RENDER2D;
    }

    public boolean isGlassHand() {
        return type == Type.GLASS_HAND;
    }

    public enum Type {
        RENDER3D, RENDER2D, GLASS_HAND
    }
}
