package beame.util.render;

import beame.util.IMinecraft;
import beame.util.color.ColorUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class RenderUtil implements IMinecraft {
// leaked by itskekoff; discord.gg/sk3d bAxs2roC
    public final List<Vec2fColored> VERTEXES = new ArrayList<>();
    final int[] LEFT_UP = new int[]{-90, 0}, RIGHT_UP = new int[]{0, 90}, RIGHT_DOWN = new int[]{90, 180}, LEFT_DOWN = new int[]{180, 270};

    public void start() {
        RenderSystem.clearCurrentColor();
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.disableAlphaTest();
        RenderSystem.disableDepthTest();
        RenderSystem.shadeModel(7425);
        defaultAlphaFunc();
    }

    public void stop() {
        RenderSystem.enableDepthTest();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.clearCurrentColor();
        RenderSystem.shadeModel(7424);
    }

    public void defaultAlphaFunc() {
        RenderSystem.alphaFunc(GL11.GL_GREATER, 0);
    }

    public void bindTexture(ResourceLocation texture) {
        mc.getTextureManager().bindTexture(texture);
    }

    public void bindTexture(int texture) {
        RenderSystem.bindTexture(texture);
    }

    public void drawRect(MatrixStack matrix, double x, double y, double width, double height, int color1, int color2, int color3, int color4, boolean bloom, boolean texture) {
        VERTEXES.clear();
        VERTEXES.add(new Vec2fColored(x, y, color1));
        VERTEXES.add(new Vec2fColored(x + width, y, color2));
        VERTEXES.add(new Vec2fColored(x + width, y + height, color3));
        VERTEXES.add(new Vec2fColored(x, y + height, color4));
        drawVertexesList2D(matrix, VERTEXES, GL11.GL_POLYGON, texture, bloom);
    }


    public void drawRect(MatrixStack matrix, double x, double y, double width, double height, int color, boolean bloom, boolean texture) {
        drawRect(matrix, x, y, width, height, color, color, color, color, bloom, texture);
    }


    public static void drawRect2(float x, float y, float width, float height, int color) {
        drawMcRect(x, y, x + width, y + height, color);
    }

    public static void drawMcRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float) (color >> 24 & 255) / 255f;
        float f = (float) (color >> 16 & 255) / 255f;
        float f1 = (float) (color >> 8 & 255) / 255f;
        float f2 = (float) (color & 255) / 255f;

        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.blendFunc(770, 771);
        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(left, bottom, 0).color(f, f1, f2, f3).endVertex();
        buffer.pos(right, bottom, 0).color(f, f1, f2, f3).endVertex();
        buffer.pos(right, top, 0).color(f, f1, f2, f3).endVertex();
        buffer.pos(left, top, 0).color(f, f1, f2, f3).endVertex();
        buffer.finishDrawing();
        WorldVertexBufferUploader.draw(buffer);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }


    public void drawVertexesList2D(MatrixStack matrix, List<Vec2fColored> vec2c, int begin, boolean texture, boolean bloom) {
        setupRenderRect(texture, bloom);
        buffer.begin(begin, texture ? DefaultVertexFormats.POSITION_TEX_COLOR : DefaultVertexFormats.POSITION_COLOR);
        int counter = 0;
        for (final Vec2fColored vec : vec2c) {
            float[] rgba = ColorUtils.getRGBAf(vec.getColor());
            buffer.pos(matrix.getLast().getMatrix(), (float) vec.getX(), (float) vec.getY());
            counter = getCounter(texture, counter, rgba);
        }
        tessellator.draw();
        endRenderRect(bloom);
    }

    public static class IntColor {

        public static float[] rgb(final int color) {
            return new float[]{
                    (color >> 16 & 0xFF) / 255f,
                    (color >> 8 & 0xFF) / 255f,
                    (color & 0xFF) / 255f,
                    (color >> 24 & 0xFF) / 255f
            };
        }

        public static int rgba(final int r,
                               final int g,
                               final int b,
                               final int a) {
            return a << 24 | r << 16 | g << 8 | b;
        }

        public static int getRed(final int hex) {
            return hex >> 16 & 255;
        }

        public static int getGreen(final int hex) {
            return hex >> 8 & 255;
        }

        public static int getBlue(final int hex) {
            return hex & 255;
        }

        public static int getAlpha(final int hex) {
            return hex >> 24 & 255;
        }
    }

    public void setupRenderRect(boolean texture, boolean bloom) {
        if (texture) RenderSystem.enableTexture();
        else RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.disableCull();
        RenderSystem.shadeModel(7425);
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, bloom ? GlStateManager.DestFactor.ONE_MINUS_CONSTANT_ALPHA : GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.alphaFunc(GL11.GL_GREATER, 0F);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glEnable(GL11.GL_POINT_SMOOTH);
    }

    public void endRenderRect(boolean bloom) {
        RenderSystem.enableAlphaTest();
        if (bloom)
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.shadeModel(7424);
        RenderSystem.enableCull();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableTexture();
        RenderSystem.clearCurrentColor();
    }

    private int getCounter(boolean texture, int counter, float[] rgba) {
        if (texture) buffer.tex(counter == 0 || counter == 3 ? 0 : 1, counter == 0 || counter == 1 ? 0 : 1);
        buffer.color(rgba[0], rgba[1], rgba[2], rgba[3]);
        buffer.endVertex();
        counter++;
        return counter;
    }

    @Getter
    @AllArgsConstructor
    public class Vec2fColored {
        private double x, y;
        private int color;

        public Vec2fColored(double x, double y) {
            this.x = x;
            this.y = y;
            this.color = -1;
        }
    }

    @Getter
    @AllArgsConstructor
    public class Vec3fColored {
        private double x, y, z;
        private int color;
    }
}
