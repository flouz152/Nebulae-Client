package beame.module;

import com.mojang.blaze3d.matrix.MatrixStack;

public interface IRenderable {
// leaked by itskekoff; discord.gg/sk3d X5hOVx43

    default void render(MatrixStack stack, float mouseX, float mouseY) {
    }
    default void mouseClicked(float mouseX, float mouseY, int mouse) {
    }
    default void charTyped(char codePoint, int modifiers) {
    }
    default void mouseReleased(float mouseX, float mouseY, int mouse) {
    }
    default void keyPressed(int key, int scanCode, int modifiers) {
    }
    default void mouseDragged(float mouseX, float mouseY, int mouseButton, double deltaX, double deltaY) {
    }
}
