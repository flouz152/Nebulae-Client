package beame.module;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Component implements IRenderable {
// leaked by itskekoff; discord.gg/sk3d TReeLPkB
    private float x, y, width, height;
    public boolean isVisible() {
        return true;
    }

    public boolean isHovered(float mouseX, float mouseY, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

}
