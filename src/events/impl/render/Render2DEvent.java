package events.impl.render;


import com.mojang.blaze3d.matrix.MatrixStack;
import events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.client.MainWindow;
import net.minecraft.client.renderer.ActiveRenderInfo;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public final class Render2DEvent extends Event {
// leaked by itskekoff; discord.gg/sk3d W8mdUYPV
    @Getter
    private static final Render2DEvent instance = new Render2DEvent();
    private MatrixStack matrix;
    private ActiveRenderInfo activeRender;
    private MainWindow mainWindow;
    private float partialTicks;

    public void set(MatrixStack matrix, ActiveRenderInfo activeRender, MainWindow mainWindow, float partialTicks) {
        this.matrix = matrix;
        this.activeRender = activeRender;
        this.mainWindow = mainWindow;
        this.partialTicks = partialTicks;
    }
}