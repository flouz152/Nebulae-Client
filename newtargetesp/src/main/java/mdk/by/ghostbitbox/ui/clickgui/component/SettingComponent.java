package mdk.by.ghostbitbox.ui.clickgui.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public abstract class SettingComponent {

    private final float height;
    private float x;
    private float y;
    private float width;
    private boolean visible;
    private final Runnable saveAction;
    private static int themeColor = 0xFF6CE3B6;

    protected SettingComponent(float height, Runnable saveAction) {
        this.height = height;
        this.saveAction = saveAction;
    }

    public float getHeight() {
        return height;
    }

    protected FontRenderer font() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft == null ? null : minecraft.fontRenderer;
    }

    protected void setBounds(float x, float y, float width, boolean visible) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.visible = visible;
    }

    protected boolean isMouseOver(double mouseX, double mouseY) {
        return visible && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    protected void requestSave() {
        if (saveAction != null) {
            saveAction.run();
        }
    }

    protected float getX() {
        return x;
    }

    protected float getY() {
        return y;
    }

    protected float getWidth() {
        return width;
    }

    protected int themeColor() {
        return themeColor;
    }

    public static void setThemeColor(int color) {
        themeColor = color;
    }

    public void hide() {
        setBounds(x, y, width, false);
    }

    public abstract void render(MatrixStack matrixStack, float x, float y, float width, int mouseX, int mouseY, float partialTicks);

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    public boolean charTyped(char codePoint, int modifiers) {
        return false;
    }
}
