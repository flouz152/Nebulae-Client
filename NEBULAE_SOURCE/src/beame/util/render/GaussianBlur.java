package beame.util.render;

import beame.Nebulae;
import beame.util.IMinecraft;
import beame.util.Stencil;
import beame.util.shader.ShaderUtil;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL13;

import java.nio.FloatBuffer;

public class GaussianBlur {
// leaked by itskekoff; discord.gg/sk3d HQA34pEU
    private static final ShaderUtil gaussianBlur = new ShaderUtil("blur");
    private static Framebuffer framebuffer = new Framebuffer(1, 1, false, false);

    private static void setupUniforms(float dir1, float dir2, float radius) {
        gaussianBlur.setUniformi("textureIn", 0);
        gaussianBlur.setUniformf("texelSize", 1.0f / (float) IMinecraft.mc.getMainWindow().getWidth(), 1.0f / (float)IMinecraft.mc.getMainWindow().getHeight());
        gaussianBlur.setUniformf("direction", dir1, dir2);
        gaussianBlur.setUniformf("radius", radius);
        FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(256);
        int i = 0;
        while ((float)i <= radius) {
            weightBuffer.put(GaussianBlur.calculateGaussianValue(i, radius / 2.0f));
            ++i;
        }
        weightBuffer.rewind();
        RenderSystem.glUniform1(gaussianBlur.getUniform("weights"), weightBuffer);
    }

    public static void startBlur() {
        if(Nebulae.getHandler().getModuleList().hud.disableBlur.get())
            return;

        Stencil.initStencilToWrite();
    }

    public static void endBlur(float radius, float compression) {
        if(Nebulae.getHandler().getModuleList().hud.disableBlur.get())
            return;

        Stencil.readStencilBuffer(1);
        framebuffer = ShaderUtil.createFrameBuffer(framebuffer);
        if(framebuffer == null) return;

        framebuffer.framebufferClear(false);
        framebuffer.bindFramebuffer(false);
        gaussianBlur.attach();
        GaussianBlur.setupUniforms(compression, 0.0f, radius);
        GlStateManager.bindTexture(IMinecraft.mc.getFramebuffer().framebufferTexture);
        ShaderUtil.drawQuads();
        framebuffer.unbindFramebuffer();
        gaussianBlur.detach();
        IMinecraft.mc.getFramebuffer().bindFramebuffer(false);
        gaussianBlur.attach();
        gaussianBlur.setUniformf("direction", 0.0f, compression);
        GlStateManager.bindTexture(GaussianBlur.framebuffer.framebufferTexture);
        ShaderUtil.drawQuads();
        gaussianBlur.detach();
        Stencil.uninitStencilBuffer();
        GlStateManager.bindTexture(0);
    }

    public static void blur(float radius, float compression) {
        if(Nebulae.getHandler().getModuleList().hud.disableBlur.get())
            return;

        framebuffer = ShaderUtil.createFrameBuffer(framebuffer);
        if(framebuffer == null) return;

        framebuffer.framebufferClear(false);
        framebuffer.bindFramebuffer(false);
        gaussianBlur.attach();
        GaussianBlur.setupUniforms(compression, 0.0f, radius);
        GlStateManager.bindTexture(IMinecraft.mc.getFramebuffer().framebufferTexture);
        ShaderUtil.drawQuads();
        framebuffer.unbindFramebuffer();
        gaussianBlur.detach();
        IMinecraft.mc.getFramebuffer().bindFramebuffer(false);
        gaussianBlur.attach();
        GaussianBlur.setupUniforms(0.0f, compression, radius);
        GlStateManager.bindTexture(GaussianBlur.framebuffer.framebufferTexture);
        ShaderUtil.drawQuads();
        gaussianBlur.detach();
        GlStateManager.bindTexture(0);
    }

    public static float calculateGaussianValue(float x, float sigma) {
        double output = 1.0 / Math.sqrt(Math.PI * 2 * (double)(sigma * sigma));
        return (float)(output * Math.exp((double)(-(x * x)) / (2.0 * (double)(sigma * sigma))));
    }
}
