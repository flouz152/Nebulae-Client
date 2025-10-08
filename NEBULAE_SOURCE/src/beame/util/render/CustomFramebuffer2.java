package beame.util.render;

import beame.util.IMinecraft;
import beame.util.math.MathUtil;
import beame.util.math.ScaleMath;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.math.vector.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;

public class CustomFramebuffer2 extends Framebuffer implements IMinecraft {
// leaked by itskekoff; discord.gg/sk3d M9b3p8Pa
    private boolean linear;

    public CustomFramebuffer2(int width, int height, boolean useDepth) {
        super(width, height, useDepth, Minecraft.IS_RUNNING_ON_MAC);
    }

    public CustomFramebuffer2(boolean useDepth) {
        super(1, 1, useDepth, Minecraft.IS_RUNNING_ON_MAC);
    }

    private static boolean resizeFramebuffer(CustomFramebuffer2 framebuffer) {
        if (needsNewFramebuffer(framebuffer)) {
            framebuffer.createBuffers(Math.max(mc.getMainWindow().getFramebufferWidth(), 1), Math.max(mc.getMainWindow().getFramebufferHeight(), 1), Minecraft.IS_RUNNING_ON_MAC);
            return true;
        }
        return false;
    }

    public CustomFramebuffer2 setLinear() {
        this.linear = true;
        return this;
    }

    @Override
    public void setFramebufferFilter(int framebufferFilterIn) {
        super.setFramebufferFilter(this.linear ? 9729 : framebufferFilterIn);
    }

    public void setup(boolean clear) {
        resizeFramebuffer(this);
        if (clear) this.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);
        this.bindFramebuffer(false);
    }

    public void setup() {
        setup(true);
    }

    public static void drawQuads(MatrixStack matrix, double x, double y, double width, double height) {
        Matrix4f matrix4f = matrix.getLast().getMatrix();

        x = MathUtil.step(x, 0.5);
        y = MathUtil.step(y, 0.5);
        width = MathUtil.step(width, 0.5);
        height = MathUtil.step(height, 0.5);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(matrix4f, (float) x, (float) y, 0).tex(0, 1).endVertex();
        buffer.pos(matrix4f, (float) x, (float) (y + height), 0).tex(0, 0).endVertex();
        buffer.pos(matrix4f, (float) (x + width), (float) (y + height), 0).tex(1, 0).endVertex();
        buffer.pos(matrix4f, (float) (x + width), (float) y, 0).tex(1, 1).endVertex();
        tessellator.draw();
    }

    public static void drawQuads(double x, double y, double width, double height) {

        x = MathUtil.step(x, 0.5);
        y = MathUtil.step(y, 0.5);
        width = MathUtil.step(width, 0.5);
        height = MathUtil.step(height, 0.5);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y, 0).tex(0, 1).endVertex();
        buffer.pos(x, y + height, 0).tex(0, 0).endVertex();
        buffer.pos(x + width, y + height, 0).tex(1, 0).endVertex();
        buffer.pos(x + width, y, 0).tex(1, 1).endVertex();
        tessellator.draw();
    }

    public static void drawQuads(MatrixStack matrix) {
        Vector2f window = ScaleMath.getMouse2(mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight());
        double width = window.x;
        double height = window.y;
        drawQuads(matrix, 0, 0, width, height);
    }

    public static void drawQuads() {
        Vector2f window = ScaleMath.getMouse2(mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight());
        double width = window.x;
        double height = window.y;
        drawQuads(0, 0, width, height);
    }

    public static void drawQuads(MatrixStack matrix, double width, double height) {
        drawQuads(matrix, 0, 0, width, height);
    }

    public static void drawQuads(double width, double height) {
        drawQuads(0, 0, width, height);
    }

    public static void drawQuads(MatrixStack matrix, double x, double y, double width, double height, int color) {
        Matrix4f matrix4f = matrix.getLast().getMatrix();

        x = MathUtil.step(x, 0.5);
        y = MathUtil.step(y, 0.5);
        width = MathUtil.step(width, 0.5);
        height = MathUtil.step(height, 0.5);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
        buffer.pos(matrix4f, (float) x, (float) y, 0).color(color).tex(0, 1).endVertex();
        buffer.pos(matrix4f, (float) x, (float) (y + height), 0).color(color).tex(0, 0).endVertex();
        buffer.pos(matrix4f, (float) (x + width), (float) (y + height), 0).color(color).tex(1, 0).endVertex();
        buffer.pos(matrix4f, (float) (x + width), (float) y, 0).color(color).tex(1, 1).endVertex();
        tessellator.draw();
    }

    public static void drawQuads(double x, double y, double width, double height, int color) {

        x = MathUtil.step(x, 0.5);
        y = MathUtil.step(y, 0.5);
        width = MathUtil.step(width, 0.5);
        height = MathUtil.step(height, 0.5);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
        buffer.pos(x, y, 0).color(color).tex(0, 1).endVertex();
        buffer.pos(x, y + height, 0).color(color).tex(0, 0).endVertex();
        buffer.pos(x + width, y + height, 0).color(color).tex(1, 0).endVertex();
        buffer.pos(x + width, y, 0).color(color).tex(1, 1).endVertex();
        tessellator.draw();
    }

    public static void drawQuads(MatrixStack matrix, int color) {
        Vector2f window = ScaleMath.getMouse2(mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight());
        double width = window.x;
        double height = window.y;
        drawQuads(matrix, 0, 0, width, height, color);
    }

    public static void drawQuads(int color) {
        Vector2f window = ScaleMath.getMouse2(mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight());
        double width = window.x;
        double height = window.y;
        drawQuads(0, 0, width, height, color);
    }

    public static void drawQuads(MatrixStack matrix, double width, double height, int color) {
        drawQuads(matrix, 0, 0, width, height, color);
    }

    public static void drawQuads(double width, double height, int color) {
        drawQuads(0, 0, width, height, color);
    }

    public void draw() {
        this.bindFramebufferTexture();
        drawQuads();
    }

    public void draw(int color) {
        this.bindFramebufferTexture();
        drawQuads(color);
    }

    public void draw(Framebuffer framebuffer) {
        framebuffer.bindFramebufferTexture();
        drawQuads();
    }

    public void stop() {
        unbindFramebuffer();
        mc.getFramebuffer().bindFramebuffer(true);
    }

    public static CustomFramebuffer2 createFrameBuffer(CustomFramebuffer2 framebuffer) {
        return createFrameBuffer(framebuffer, false);
    }

    public static CustomFramebuffer2 createFrameBuffer(CustomFramebuffer2 framebuffer, boolean depth) {
        if (needsNewFramebuffer(framebuffer)) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new CustomFramebuffer2(mc.getMainWindow().getFramebufferWidth(), mc.getMainWindow().getFramebufferHeight(), depth);
        }
        return framebuffer;
    }

    public static boolean needsNewFramebuffer(CustomFramebuffer2 framebuffer) {
        return framebuffer == null || framebuffer.framebufferWidth != mc.getMainWindow().getFramebufferWidth() || framebuffer.framebufferHeight != mc.getMainWindow().getFramebufferHeight();
    }
}