package events.impl.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.vector.Matrix4f;

import events.Event;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public final class Render3DLastEvent extends Event {
// leaked by itskekoff; discord.gg/sk3d sJB3gN8j
    @Getter
    public static Render3DLastEvent instance = new Render3DLastEvent();
    private WorldRenderer context;
    private MatrixStack matrix;
    private Matrix4f projectionMatrix;
    private ActiveRenderInfo activeRenderInfo;
    private float partialTicks;
    private long finishTimeNano;

    public void set(WorldRenderer context, MatrixStack matrix, Matrix4f projectionMatrix, ActiveRenderInfo activeRenderInfo, float partialTicks, long finishTimeNano) {
        this.context = context;
        this.matrix = matrix;
        this.projectionMatrix = projectionMatrix;
        this.activeRenderInfo = activeRenderInfo;
        this.partialTicks = partialTicks;
        this.finishTimeNano = finishTimeNano;
    }

}