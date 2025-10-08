package beame.util.math;

import beame.util.IMinecraft;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import org.joml.Vector2f;

@UtilityClass
public class ScaleMath implements IMinecraft {
// leaked by itskekoff; discord.gg/sk3d F5CcVuPW
    public Vec2i getMouse(int mouseX, int mouseY) {
        return new Vec2i((int)((double)mouseX * Minecraft.getInstance().getMainWindow().getGuiScaleFactor() / 2.0), (int)((double)mouseY * Minecraft.getInstance().getMainWindow().getGuiScaleFactor() / 2.0));
    }

    public Vector2f getMouse2(double mouseX, double mouseY) {
        return new Vector2f((float) (mouseX * mc.getMainWindow().getGuiScaleFactor() / 2), (float) (mouseY * mc.getMainWindow().getGuiScaleFactor() / 2));
    }
}
