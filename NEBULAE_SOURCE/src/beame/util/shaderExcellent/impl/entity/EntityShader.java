package beame.util.shaderExcellent.impl.entity;

import beame.Nebulae;
import beame.util.IMinecraft;
import beame.util.render.CustomFramebuffer2;
import beame.util.render.RenderUtil;
import beame.util.shaderExcellent.ShaderManager;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import com.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EntityShader implements IMinecraft {
// leaked by itskekoff; discord.gg/sk3d 7h6PyMPo
    public ShaderManager kawaseDown = ShaderManager.kawaseDownBloom;
    public ShaderManager kawaseUp = ShaderManager.kawaseUpBloom;
    public Framebuffer framebuffer = new Framebuffer(1, 1, false, Minecraft.IS_RUNNING_ON_MAC);
    private int currentIterations;
    private final List<Framebuffer> framebufferList = new CopyOnWriteArrayList<>();

    private void initFramebuffers(float iterations) {
        for (Framebuffer framebuffer : framebufferList) {
            framebuffer.deleteFramebuffer();
        }
        framebufferList.clear();
        framebufferList.add(framebuffer = CustomFramebuffer2.createFrameBuffer(null, false));
        for (int i = 0; i <= iterations; i++) {
            Framebuffer currentBuffer = new Framebuffer((int) (mc.getMainWindow().getFramebufferWidth() / Math.pow(2, i)), (int) (mc.getMainWindow().getFramebufferHeight() / Math.pow(2, i)), true, Minecraft.IS_RUNNING_ON_MAC);
            currentBuffer.setFramebufferFilter(GL11.GL_LINEAR);
            GlStateManager.bindTexture(currentBuffer.framebufferTexture);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_MIRRORED_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL14.GL_MIRRORED_REPEAT);
            framebufferList.add(currentBuffer);
        }
    }

    public void render(MatrixStack matrix, int framebufferTexture, int iterations, float offset, float divider) {
        if (currentIterations != iterations || (framebuffer.framebufferWidth != mc.getMainWindow().getFramebufferWidth() || framebuffer.framebufferHeight != mc.getMainWindow().getFramebufferHeight())) {
            initFramebuffers(iterations);
            currentIterations = iterations;
        }
        RenderSystem.pushMatrix();
        renderFBO(matrix, framebufferList.get(1), framebufferTexture, kawaseDown, offset);
        for (int i = 1; i < iterations; i++) {
            renderFBO(matrix, framebufferList.get(i + 1), framebufferList.get(i).framebufferTexture, kawaseDown, offset);
        }
        for (int i = iterations; i > 1; i--) {
            renderFBO(matrix, framebufferList.get(i - 1), framebufferList.get(i).framebufferTexture, kawaseUp, offset);
        }
        Framebuffer lastBuffer = framebufferList.get(0);
        lastBuffer.framebufferClear();
        lastBuffer.bindFramebuffer(true);
        kawaseUp.load();
        kawaseUp.setUniformf("divider", divider);
        kawaseUp.setUniformf("offset", offset, offset);
        kawaseUp.setUniformi("check", 1);

        switch (Nebulae.getHandler().getModuleList().shaderEsp.mode().get()) {
            case "Внешний": {
                kawaseUp.setUniformi("inTexture", 0);
                kawaseUp.setUniformi("textureToCheck", 16);
                break;
            }
            case "Внутренний": {
                kawaseUp.setUniformi("inTexture", 16);
                kawaseUp.setUniformi("textureToCheck", 0);
                break;
            }
            case "Внешний и внутренний": {
                kawaseUp.setUniformi("inTexture", 0);
                kawaseUp.setUniformi("textureToCheck", 0);
                break;
            }
        }

        kawaseUp.setUniformf("halfpixel", 1.0f / lastBuffer.framebufferWidth, 1.0f / lastBuffer.framebufferHeight);
        kawaseUp.setUniformf("resolution", lastBuffer.framebufferWidth, lastBuffer.framebufferHeight);
        RenderSystem.activeTexture(GL13.GL_TEXTURE16);
        RenderUtil.bindTexture(framebufferTexture);
        RenderSystem.activeTexture(GL13.GL_TEXTURE0);
        RenderUtil.bindTexture(framebufferList.get(1).framebufferTexture);
        RenderUtil.start();
        RenderSystem.disableBlend();
        CustomFramebuffer2.drawQuads(matrix);
        RenderUtil.stop();
        kawaseUp.setUniformf("divider", 12F);
        kawaseUp.unload();

        mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.bindTexture(framebufferList.get(0).framebufferTexture);
        RenderUtil.start();
        CustomFramebuffer2.drawQuads(matrix);
        RenderUtil.stop();
        RenderSystem.popMatrix();
    }

    private void renderFBO(MatrixStack matrix, Framebuffer framebuffer, int framebufferTexture, ShaderManager shader, float offset) {
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        shader.load();
        shader.setUniformf("offset", offset, offset);
        shader.setUniformi("inTexture", 0);
        shader.setUniformi("check", 0);
        shader.setUniformf("halfpixel", 1.0f / framebuffer.framebufferWidth, 1.0f / framebuffer.framebufferHeight);
        shader.setUniformf("resolution", framebuffer.framebufferWidth, framebuffer.framebufferHeight);
        RenderUtil.bindTexture(framebufferTexture);
        RenderUtil.start();
        RenderSystem.disableBlend();
        CustomFramebuffer2.drawQuads(matrix);
        RenderUtil.stop();
        shader.unload();
    }
}
