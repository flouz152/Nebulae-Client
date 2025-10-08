package beame.util.render;


import beame.util.IMinecraft;
import beame.util.shader.ShaderUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;

import java.util.ArrayList;
import java.util.List;

public class KawaseBlur implements IMinecraft {
// leaked by itskekoff; discord.gg/sk3d tMbCgPpA

    private static Framebuffer framebufferTexture = new Framebuffer(mw.getFramebufferWidth(), mw.getFramebufferHeight(), false, Minecraft.IS_RUNNING_ON_MAC);
    private static Framebuffer framebuffer = new Framebuffer(mw.getFramebufferWidth(), mw.getFramebufferHeight(), false, Minecraft.IS_RUNNING_ON_MAC);
    private static final List<Framebuffer> framebufferList = new ArrayList<>();
    private static int currentIterations;

    private static void initFramebuffers(float iterations) {
        for (Framebuffer framebuffer : framebufferList) {
            framebuffer.deleteFramebuffer();
        }

        framebufferList.clear();
        framebufferList.add(framebuffer = ClientHandler.FrameBuffer.createFrameBuffer(null));

        for (int i = 1; i <= iterations; i++) {
            Framebuffer currentBuffer = new Framebuffer((int) (mw.getFramebufferWidth() / Math.pow(2, i)), (int) (mw.getFramebufferHeight() / Math.pow(2, i)), false, Minecraft.IS_RUNNING_ON_MAC);
            currentBuffer.setFramebufferFilter(GL11.GL_LINEAR);

            RenderSystem.bindTexture(currentBuffer.framebufferTexture);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_MIRRORED_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL14.GL_MIRRORED_REPEAT);
            RenderSystem.bindTexture(0);

            framebufferList.add(currentBuffer);
        }
    }

    private static void draw(int framebufferTexture, int iterations, float offset) {
        if (currentIterations != iterations || framebuffer.framebufferWidth != mw.getFramebufferWidth() || framebuffer.framebufferHeight != mw.getFramebufferHeight()) {
            initFramebuffers(iterations);
            currentIterations = iterations;
        }

        renderFBO(framebufferList.get(1), mc.getFramebuffer().framebufferTexture, ShaderUtil.KAWASE_DOWN, offset);

        for (int i = 1; i < iterations; i++) {
            renderFBO(framebufferList.get(i + 1), framebufferList.get(i).framebufferTexture, ShaderUtil.KAWASE_DOWN, offset);
        }

        for (int i = iterations; i > 1; i--) {
            renderFBO(framebufferList.get(i - 1), framebufferList.get(i).framebufferTexture, ShaderUtil.KAWASE_UP, offset);
        }

        Framebuffer lastBuffer = framebufferList.get(0);
        lastBuffer.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);
        lastBuffer.bindFramebuffer(false);

        ShaderUtil.KAWASE_UP.attach();
        ShaderUtil.KAWASE_UP.setUniformf("offset", offset, offset);
        ShaderUtil.KAWASE_UP.setUniform("inTexture", 0);
        ShaderUtil.KAWASE_UP.setUniform("check", 1);
        ShaderUtil.KAWASE_UP.setUniform("textureToCheck", 16);
        ShaderUtil.KAWASE_UP.setUniformf("halfPixel", 1f / lastBuffer.framebufferWidth, 1f / lastBuffer.framebufferHeight);
        ShaderUtil.KAWASE_UP.setUniformf("resolution", lastBuffer.framebufferWidth, lastBuffer.framebufferHeight);
        GL13.glActiveTexture(GL13.GL_TEXTURE16);
        RenderSystem.bindTexture(framebufferTexture);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        RenderSystem.bindTexture(framebufferList.get(1).framebufferTexture);
        ShaderUtil.drawQuads();
        ShaderUtil.KAWASE_UP.detach();

        mc.getFramebuffer().bindFramebuffer(true);
        RenderSystem.bindTexture(framebufferList.get(0).framebufferTexture);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(770, 771);
        ShaderUtil.drawQuads();
        RenderSystem.disableBlend();
        RenderSystem.bindTexture(0);
    }

    private static void renderFBO(Framebuffer framebuffer, int framebufferTexture, ShaderUtil shader, float offset) {
        framebuffer.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);
        framebuffer.bindFramebuffer(false);

        shader.attach();
        RenderSystem.bindTexture(framebufferTexture);
        shader.setUniformf("offset", offset, offset);
        shader.setUniform("inTexture", 0);
        shader.setUniform("check", 0);
        shader.setUniformf("halfPixel", 1f / framebuffer.framebufferWidth, 1f / framebuffer.framebufferHeight);
        shader.setUniformf("resolution", framebuffer.framebufferWidth, framebuffer.framebufferHeight);
        ShaderUtil.drawQuads();
        shader.detach();
    }

    public static void applyBlur(Runnable runnable, int iterations, float offset) {
        framebufferTexture = ClientHandler.FrameBuffer.createFrameBuffer(framebufferTexture);
        framebufferTexture.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);
        framebufferTexture.bindFramebuffer(false);
        runnable.run();
        framebufferTexture.unbindFramebuffer();
        draw(framebufferTexture.framebufferTexture, iterations, offset);
    }
}
