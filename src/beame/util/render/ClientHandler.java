package beame.util.render;

import beame.Nebulae;
import beame.components.command.AbstractCommand;
import beame.util.IMinecraft;
import beame.util.color.ColorUtils;
import beame.util.shader.ShaderUtil;
import beame.util.shader.Shaders;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.experimental.UtilityClass;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import org.joml.Vector4i;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static beame.util.IMinecraft.*;
import static beame.util.color.ColorUtils.interpolateColor;
import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.*;
import static org.lwjgl.opengl.GL11C.*;


public class ClientHandler {
// leaked by itskekoff; discord.gg/sk3d RAPKTSgG
    private static final long initTime = System.currentTimeMillis();
    public static void drawRect(
            double left,
            double top,
            double right,
            double bottom,
            int color) {
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

        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(left, bottom, 0.0F).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(right, bottom, 0.0F).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(right, top, 0.0F).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(left, top, 0.0F).color(f, f1, f2, f3).endVertex();
        bufferbuilder.finishDrawing();
        WorldVertexBufferUploader.draw(bufferbuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
    public static int reAlphaInt(final int color,
                                 final int alpha) {
        return (MathHelper.clamp(alpha, 0, 255) << 24) | (color & 16777215);
    }
    public static boolean isInRegion(final int mouseX,
                                     final int mouseY,
                                     final int x,
                                     final int y,
                                     final int width,
                                     final int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public static void drawRoundImage(ResourceLocation img, final float x, final float y, final float width, final float height, final float radius, final float alpha) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        ShaderUtil.roundimage.attach();

        ShaderUtil.roundimage.setUniform("rectSize", (float) (width * 2), (float) (height * 2));
        ShaderUtil.roundimage.setUniform("radius", radius * 2);
        ShaderUtil.roundimage.setUniform("alpha", alpha);

        mc.getTextureManager().bindTexture(img);
        drawQuads(x, y, width, height, 7);

        ShaderUtil.roundimage.detach();
        RenderSystem.popMatrix();
    }

    private static void quadsBeginC(float x, float y, float width2, float height2, int glQuads, Vector4i color) {
        IMinecraft.buffer.begin(glQuads, DefaultVertexFormats.POSITION_TEX_COLOR);
        IMinecraft.buffer.pos(x, y, 0.0).tex(0.0f, 0.0f).color(color.get(0)).endVertex();
        IMinecraft.buffer.pos(x, y + height2, 0.0).tex(0.0f, 1.0f).color(color.get(1)).endVertex();
        IMinecraft.buffer.pos(x + width2, y + height2, 0.0).tex(1.0f, 1.0f).color(color.get(2)).endVertex();
        IMinecraft.buffer.pos(x + width2, y, 0.0).tex(1.0f, 0.0f).color(color.get(3)).endVertex();
        IMinecraft.tessellator.draw();
    }

    public static void drawImage(ResourceLocation resourceLocation, float x, float y, float width, float height, int color) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        mc.getTextureManager().bindTexture(resourceLocation);
        quads(x, y, width, height, 7, color);
        RenderSystem.shadeModel(7424);
        RenderSystem.color4f(1, 1, 1, 1);
        RenderSystem.popMatrix();
    }

    public static void drawImage(MatrixStack stack, ResourceLocation resourceLocation, float x, float y, float width, float height, Color color) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.shadeModel(7425);
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        RenderSystem.color4f((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f);
        IMinecraft.mc.getTextureManager().bindTexture(resourceLocation);
        AbstractGui.blit(stack, (int)x, (int)y, 0.0f, 0.0f, (int)width, (int)height, (int)width, (int)height);
        RenderSystem.shadeModel(7424);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.clearCurrentColor();
        RenderSystem.popMatrix();
    }

    public static void drawImage(MatrixStack stack, ResourceLocation resourceLocation, float f, float f2, float f3, float f4, int n, float rotationAngle) {
        try {
            RenderSystem.pushMatrix();
            RenderSystem.disableLighting();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.shadeModel(7425);
            RenderSystem.disableCull();
            RenderSystem.disableAlphaTest();
            RenderSystem.blendFuncSeparate(770, 1, 0, 1);
            Matrix4f matrix4f = stack.getLast().getMatrix();

            float scaledSize = f3;

            RenderSystem.translatef(f + scaledSize / 2.0F, f2 + scaledSize / 2.0F, 0.0F);
            RenderSystem.rotatef(rotationAngle, 0.0F, 0.0F, 1.0F);
            RenderSystem.translatef(-(f + scaledSize / 2.0F), -(f2 + scaledSize / 2.0F), 0.0F);

            mc.getTextureManager().bindTexture(resourceLocation);
            buffer.begin(7, POSITION_TEX_COLOR);
            {
                buffer.pos(matrix4f, f, f2, 0).tex(0, 0).color(n).endVertex();
                buffer.pos(matrix4f, f, f2 + f4, 0).tex(0, 1).color(n).endVertex();
                buffer.pos(matrix4f, f + f3, f2 + f4, 0).tex(1, 1).color(n).endVertex();
                buffer.pos(matrix4f, f + f3, f2, 0).tex(1, 0).color(n).endVertex();
            }
            tessellator.draw();

            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.enableAlphaTest();
            RenderSystem.depthMask(true);
            RenderSystem.popMatrix();
        } catch (Exception ex) {

        }
    }

    public static void quads(float x, float y, float width, float height, int glQuads, int color) {
        buffer.begin(glQuads, POSITION_TEX_COLOR);
        {
            buffer.pos(x, y, 0).tex(0, 0).color(color).endVertex();
            buffer.pos(x, y + height, 0).tex(0, 1).color(color).endVertex();
            buffer.pos(x + width, y + height, 0).tex(1, 1).color(color).endVertex();
            buffer.pos(x + width, y, 0).tex(1, 0).color(color).endVertex();
        }
        tessellator.draw();
    }
    public static void drawQuadsSigma(float x, float y, float width, float height) {
        buffer.begin(7, POSITION_COLOR_TEX_LIGHTMAP);
        buffer.pos(x, y, 0).tex(0, 0).lightmap(0, 240).endVertex();
        buffer.pos(x, y + height, 0).tex(0, 1).lightmap(0, 240).endVertex();
        buffer.pos(x + width, y + height, 0).tex(1, 1).lightmap(0, 240).endVertex();
        buffer.pos(x + width, y, 0).tex(1, 0).lightmap(0, 240).endVertex();
        Tessellator.getInstance().draw();
    }
    public static void drawQuads(float x, float y, float width, float height, int glQuads) {
        buffer.begin(glQuads, POSITION_TEX);
        {
            buffer.pos(x, y, 0).tex(0, 0).endVertex();
            buffer.pos(x, y + height, 0).tex(0, 1).endVertex();
            buffer.pos(x + width, y + height, 0).tex(1, 1).endVertex();
            buffer.pos(x + width, y, 0).tex(1, 0).endVertex();
        }
        Tessellator.getInstance().draw();
    }
    public static void drawArrow(MatrixStack matrixStack, float x, float y, float size, int color) {
        RenderSystem.pushMatrix();
        RenderSystem.scalef(0.75f, 0.75f, 0.75f);

        /*RenderSystem.pushMatrix();
        RenderSystem.rotatef(25, 1, 0f, 1f);
        drawRound(x+8, y, 2, size, 1, color);
        RenderSystem.rotatef(-25-25, 1, 0f, 1f);
        drawRound(x+6.5f, y+5, 2, size, 1, color);
        RenderSystem.popMatrix();*/
        drawImage(matrixStack, new ResourceLocation("night/image/triangle.png"), x-20, y, size*2, size*2, color, 0);
        /*RenderSystem.pushMatrix();
        RenderSystem.rotatef(45, 1, 0f, 1f);
        drawRound(x+4+6, y+4, 2, size/1.5f, 1, color);
        RenderSystem.rotatef(-45-45, 1, 0f, 1f);
        drawRound(x+3, y+14, 2, size/1.5f, 1, color);
        RenderSystem.popMatrix();*/

        RenderSystem.popMatrix();
    }

    public static void drawMenuBackground(float x, float y, float width, float height, int color, int color2, float time) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();

        float[] clr = ColorUtils.rgba(color);
        float[] clr2 = ColorUtils.rgba(color2);

        ShaderUtil.mainmenu.attach();

        ShaderUtil.mainmenu.setUniformf("resolution", mc.getMainWindow().getWidth(), mc.getMainWindow().getHeight());
        ShaderUtil.mainmenu.setUniformf("time", time);
        ShaderUtil.mainmenu.setUniformf("clr1", clr[0]);
        ShaderUtil.mainmenu.setUniformf("clr2", clr[1]);
        ShaderUtil.mainmenu.setUniformf("clr3", clr[2]);
        ShaderUtil.mainmenu.setUniformf("clr4", clr2[0]-((float) 50 / 255));
        ShaderUtil.mainmenu.setUniformf("clr5", clr2[1]-((float) 50 / 255));
        ShaderUtil.mainmenu.setUniformf("clr6", clr2[2]-((float) 50 / 255));

        drawQuads(x, y, width, height, 7);

        ShaderUtil.smooth.detach();

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawRound(float x,
                                 float y,
                                 float width,
                                 float height,
                                 float radius,
                                 int color) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        ShaderUtil.smooth.attach();

        ShaderUtil.smooth.setUniformf("location", (float) (x * mc.getMainWindow().getGuiScaleFactor()),
                (float) ((mc.getMainWindow().getHeight() - (height * mc.getMainWindow().getGuiScaleFactor()))
                        - (y * mc.getMainWindow().getGuiScaleFactor())));
        ShaderUtil.smooth.setUniformf("rectSize", width * mc.getMainWindow().getGuiScaleFactor(),
                height * mc.getMainWindow().getGuiScaleFactor());
        ShaderUtil.smooth.setUniformf("radius", radius * mc.getMainWindow().getGuiScaleFactor());
        ShaderUtil.smooth.setUniform("blur", 7);
        ShaderUtil.smooth.setUniform("color",
                ColorUtils.rgba(color));
        drawQuads(x, y, width, height, 7);

        ShaderUtil.smooth.detach();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static float getRenderHurtTime(LivingEntity hurt) {
        return (float) hurt.hurtTime - (hurt.hurtTime != 0 ? mc.timer.renderPartialTicks : 0.0f);
    }

    public static float getHurtPercent(LivingEntity hurt) {
        return getRenderHurtTime(hurt) / (float) 10;
    }

    public static void drawFace(MatrixStack matrixStack,
                                LivingEntity target,
                                float x,
                                float y,
                                int width,
                                int height,
                                float radius,
                                float hurtStrength) {
        ResourceLocation entityTexture = mc.getRenderManager().getRenderer(target).getEntityTexture(target);


        matrixStack.push();
        matrixStack.scale(1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableAlphaTest();

        RenderSystem.color4f(1, 1, 1, 1);

        ShaderUtil.headshader.attach();
        ShaderUtil.headshader.setUniform("width", width);
        ShaderUtil.headshader.setUniform("height", height);
        ShaderUtil.headshader.setUniform("radius", radius);
        ShaderUtil.headshader.setUniform("hurtStrength", hurtStrength);
        ShaderUtil.headshader.setUniform("alpha", 255f);
        ShaderUtil.drawQuadss(matrixStack, x, y, width, height);
        mc.getTextureManager().bindTexture(entityTexture);
        ShaderUtil.headshader.detach();

        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
        matrixStack.pop();
    }


    public static void drawPlayerIcon(float d,
                                      float y,
                                      float u,
                                      float v,
                                      float uWidth,
                                      float vHeight,
                                      float width,
                                      float height,
                                      float tileWidth,
                                      float tileHeight,
                                      AbstractClientPlayerEntity target) {
        try {
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            ResourceLocation skin = target.getLocationSkin();
            mc.getTextureManager().bindTexture(skin);
            float hurtPercent = getHurtPercent(target);
            GL11.glColor4f(1, 1 - hurtPercent, 1 - hurtPercent, 1);
            AbstractGui.drawScaledCustomSizeModalRect(d, y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
            GL11.glColor4f(1, 1, 1, 1);
            GL11.glPopMatrix();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void drawRoundedRectOutline(MatrixStack matrixStack, double x, double y, double width, double height, double radius, float thickness, Color color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(thickness);

        float red = color.getRed() / 255.0f;
        float green = color.getGreen() / 255.0f;
        float blue = color.getBlue() / 255.0f;
        float alpha = color.getAlpha() / 255.0f;

        buffer.begin(3, DefaultVertexFormats.POSITION_COLOR);

        for (double i = x + radius; i < x + width - radius; i++) {
            buffer.pos(i, y, 0).color(red, green, blue, alpha).endVertex();
            buffer.pos(i, y + height, 0).color(red, green, blue, alpha).endVertex();
        }

        for (double i = y + radius; i < y + height - radius; i++) {
            buffer.pos(x, i, 0).color(red, green, blue, alpha).endVertex();
            buffer.pos(x + width, i, 0).color(red, green, blue, alpha).endVertex();
        }
        drawCorner(buffer, x + radius, y + radius, radius, 180, 270, red, green, blue, alpha);
        drawCorner(buffer, x + width - radius, y + radius, radius, 270, 360, red, green, blue, alpha);
        drawCorner(buffer, x + width - radius, y + height - radius, radius, 0, 90, red, green, blue, alpha);
        drawCorner(buffer, x + radius, y + height - radius, radius, 90, 180, red, green, blue, alpha);

        tessellator.draw();

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    private static void drawCorner(BufferBuilder buffer, double cx, double cy, double radius, int startAngle, int endAngle, float red, float green, float blue, float alpha) {
        for (int angle = startAngle; angle <= endAngle; angle += 5) {
            double rad = Math.toRadians(angle);
            double x = cx + Math.cos(rad) * radius;
            double y = cy + Math.sin(rad) * radius;
            buffer.pos(x, y, 0).color(red, green, blue, alpha).endVertex();
        }
    }

    public static void drawGradientRound(float x,
                                         float y,
                                         float width,
                                         float height,
                                         float radius,
                                         int color0, int color1, int color2, int color3) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        ShaderUtil.roundedgradient.attach();

        ShaderUtil.roundedgradient.setUniformf("location", (float) (x * mc.getMainWindow().getGuiScaleFactor()),
                (float) ((mc.getMainWindow().getHeight() - (height * mc.getMainWindow().getGuiScaleFactor()))
                        - (y * mc.getMainWindow().getGuiScaleFactor())));
        ShaderUtil.roundedgradient.setUniformf("rectSize", width * mc.getMainWindow().getGuiScaleFactor(),
                height * mc.getMainWindow().getGuiScaleFactor());
        ShaderUtil.roundedgradient.setUniformf("radius", radius * mc.getMainWindow().getGuiScaleFactor());
        ShaderUtil.roundedgradient.setUniform("blur", 0);
        ShaderUtil.roundedgradient.setUniform("color0", ColorUtils.rgba(color0));
        ShaderUtil.roundedgradient.setUniform("color1", ColorUtils.rgba(color1));
        ShaderUtil.roundedgradient.setUniform("color2", ColorUtils.rgba(color2));
        ShaderUtil.roundedgradient.setUniform("color3", ColorUtils.rgba(color3));
        drawQuads(x, y, width, height, 7);

        ShaderUtil.roundedgradient.detach();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawSexyRect(float x, float y, float w, float h, float round, boolean colored) {
        int clr_bg = colored ? new Color(17, 17, 17).getRGB() : new Color(60, 60, 60, 100).getRGB();
        int clr_bg2 = new Color(Nebulae.getHandler().styler.clr17, Nebulae.getHandler().styler.clr17, Nebulae.getHandler().styler.clr17).getRGB();

        int clr_0 = interpolateColor(clr_bg, Nebulae.getHandler().themeManager.getColor(0), 0.35f);
        int clr_180 = interpolateColor(clr_bg, Nebulae.getHandler().themeManager.getColor(255), 0.35f);

        int clr0 = ColorUtils.setAlpha(clr_0, 255);
        int clr1 = ColorUtils.setAlpha(clr_180, 255);
        int clr3 = colored ? interpolateColor(clr0, clr1, Nebulae.getHandler().interpolateState) : clr_bg;
        int clr2_1 = colored ? interpolateColor(clr_bg, clr0, 0.5f) : clr_bg;
        int clr2_2 = colored ? interpolateColor(clr_bg, clr1, 0.5f) : clr_bg;

        int clr = ColorUtils.setAlpha(clr_bg2, colored ? 170 : 160);

        if (!colored && w > 0 && h > 0 && !Nebulae.getHandler().getModuleList().hud.disableBlur.get()) {
            IWrapper.blurQueue.add(() -> {
                RenderSystem.pushMatrix();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.disableAlphaTest();
                RenderSystem.shadeModel(GL11.GL_SMOOTH);
                RenderSystem.depthMask(false);
                
                ClientHandler.drawGradientRound(x, y, w, h, round, clr_bg2, clr_bg2, clr_bg2, clr_bg2);
                
                RenderSystem.depthMask(true);
                RenderSystem.shadeModel(GL11.GL_FLAT);
                RenderSystem.enableAlphaTest();
                RenderSystem.disableBlend();
                RenderSystem.popMatrix();
            });
        }

        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableAlphaTest();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);

        ClientHandler.drawGradientRound(x - 0.5f, y - 0.5f, w + 1f, h + 1f, round, clr3, clr3, new Color(25, 25, 25, 150).getRGB(), new Color(25, 25, 25, 150).getRGB());
        ClientHandler.drawGradientRound(x, y, w, h, round, clr, clr, clr, clr);

        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    public static void drawSexyRectFromPanel(float x, float y, float w, float h, float round, boolean colored) {
        int clr_bg = colored ? new Color(17, 17, 17).getRGB() : new Color(15, 15, 15, 100).getRGB();
        int clr_bg2 = new Color(Nebulae.getHandler().styler.clr17, Nebulae.getHandler().styler.clr17, Nebulae.getHandler().styler.clr17).getRGB();

        int clr_0 = interpolateColor(clr_bg, Nebulae.getHandler().themeManager.getColor(0), 0.35f);
        int clr_180 = interpolateColor(clr_bg, Nebulae.getHandler().themeManager.getColor(255), 0.35f);

        int clr0 = ColorUtils.setAlpha(clr_0, 255);
        int clr1 = ColorUtils.setAlpha(clr_180, 255);
        int clr3 = colored ? interpolateColor(clr0, clr1, Nebulae.getHandler().interpolateState) : clr_bg;
        int clr2_1 = colored ? interpolateColor(clr_bg, clr0, 0.2f) : clr_bg;
        int clr2_2 = colored ? interpolateColor(clr_bg, clr1, 0.2f) : clr_bg;

        int clr = ColorUtils.setAlpha(clr_bg2, colored ? 150 : 150);

        if (!colored && w > 0 && h > 0) {
            IWrapper.blurQueue.add(() -> {
                RenderSystem.pushMatrix();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.disableAlphaTest();
                RenderSystem.shadeModel(GL11.GL_SMOOTH);
                RenderSystem.depthMask(false);
                
                ClientHandler.drawGradientRound(x, y, w, h, round, clr_bg2, clr_bg2, clr_bg2, clr_bg2);
                
                RenderSystem.depthMask(true);
                RenderSystem.shadeModel(GL11.GL_FLAT);
                RenderSystem.enableAlphaTest();
                RenderSystem.disableBlend();
                RenderSystem.popMatrix();
            });
        }

        // Основной контент
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableAlphaTest();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);

        ClientHandler.drawGradientRound(x - 0.5f, y - 0.5f, w + 1f, h + 1f, round, clr3, clr3, clr2_2, clr2_1);
        ClientHandler.drawGradientRound(x, y, w, h, round, clr, clr, clr, clr);

        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    @UtilityClass
    public static class FrameBuffer {
        public Framebuffer createFrameBuffer(Framebuffer framebuffer) {
            return createFrameBuffer(framebuffer, false);
        }

        public Framebuffer createFrameBuffer(Framebuffer framebuffer, boolean depth) {
            if (needsNewFramebuffer(framebuffer)) {
                if (framebuffer != null) {
                    framebuffer.deleteFramebuffer();
                }
                int frameBufferWidth = mc.getMainWindow().getFramebufferWidth();
                int frameBufferHeight = mc.getMainWindow().getFramebufferHeight();
                return new Framebuffer(frameBufferWidth, frameBufferHeight, depth);
            }
            return framebuffer;
        }

        public boolean needsNewFramebuffer(Framebuffer framebuffer) {
            return framebuffer == null || framebuffer.framebufferWidth != mc.getMainWindow().getFramebufferWidth() || framebuffer.framebufferHeight != mc.getMainWindow().getFramebufferHeight();
        }
    }
}
