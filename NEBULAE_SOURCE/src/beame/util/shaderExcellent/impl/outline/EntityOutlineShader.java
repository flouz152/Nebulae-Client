package beame.util.shaderExcellent.impl.outline;

import beame.util.IMinecraft;
import beame.util.render.CustomFramebuffer2;
import beame.util.render.RenderUtil;
import beame.util.shaderExcellent.ShaderManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.optifine.render.Blender;
import org.lwjgl.opengl.GL30;

public class EntityOutlineShader implements IMinecraft {
// leaked by itskekoff; discord.gg/sk3d D4MfyxnL

    private static final ShaderManager outline = ShaderManager.entityOutlineShader;
    private static final CustomFramebuffer2 outFrameBuffer = new CustomFramebuffer2(false);


    public static void draw(int radius, int texture) {
        outFrameBuffer.setup();
        outFrameBuffer.bindFramebuffer(true);

        outline.load();
        outline.setUniformf("size", (float) radius);
        outline.setUniformi("textureIn", 0);
        outline.setUniformi("textureToCheck", 20);
        outline.setUniformf("texelSize", 1.0F / (float) (mc.getMainWindow().getScaledWidth() * mc.getMainWindow().getGuiScaleFactor()), 1.0F / (float) (mc.getMainWindow().getScaledHeight() * mc.getMainWindow().getGuiScaleFactor()));

        outline.setUniformf("direction", 1.0F, 0.0F);
        int blend = 8;
        RenderSystem.enableBlend();
        Blender.setupBlend(blend, 1f);

        RenderUtil.defaultAlphaFunc();

        RenderUtil.bindTexture(texture);
        CustomFramebuffer2.drawQuads();

        mc.getFramebuffer().bindFramebuffer(false);
        Blender.setupBlend(blend, 1f);

        outline.setUniformf("direction", 0.0F, 1.0F);

        outFrameBuffer.bindFramebufferTexture();
        GL30.glActiveTexture(GL30.GL_TEXTURE20);
        RenderUtil.bindTexture(texture);
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        CustomFramebuffer2.drawQuads();

        outline.unload();
        RenderSystem.bindTexture(0);
        RenderSystem.disableBlend();
    }

}
