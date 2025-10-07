package beame.util.render;

import beame.Essence;
import beame.util.Stencil;
import beame.util.shader.ShaderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;

public class BlurUtility {
// leaked by itskekoff; discord.gg/sk3d EZF7kc0H
    public static BlurUtility blur = new BlurUtility();

    public final CustomFramebuffer BLURRED;
    public final CustomFramebuffer ADDITIONAL;
    CustomFramebuffer blurTarget = new CustomFramebuffer(false).setLinear();

    public BlurUtility() {
        BLURRED = new CustomFramebuffer(false).setLinear();
        ADDITIONAL = new CustomFramebuffer(false).setLinear();
    }

    public void render(Runnable run) {
        if(!Essence.getHandler().getModuleList().hud.disableBlur.get()) {
            float enabledAnimation = Essence.getHandler().getModuleList().hud.enabledAnimation;
            if (enabledAnimation > 0.1f) {
                Stencil.initStencilToWrite();
                run.run();
                Stencil.readStencilBuffer(1);
                BLURRED.draw();
                updateBlur(0.5f * enabledAnimation, 3);
                Stencil.uninitStencilBuffer();
            }
        }
    }

    public void updateBlur(float offset, int steps) {
        if(!Essence.getHandler().getModuleList().hud.disableBlur.get()) {
            Minecraft mc = Minecraft.getInstance();
            Framebuffer mcFramebuffer = mc.getFramebuffer();
            ADDITIONAL.setup();
            mcFramebuffer.bindFramebufferTexture();
            ShaderUtil.kawaseDown.attach();
            ShaderUtil.kawaseDown.setUniform("offset", offset);
            ShaderUtil.kawaseDown.setUniformf("resolution", 1f / mc.getMainWindow().getWidth(),
                    1f / mc.getMainWindow().getHeight());
            CustomFramebuffer.drawTexture();
            CustomFramebuffer[] buffers = {this.ADDITIONAL, this.BLURRED};
            for (int i = 1; i < steps; ++i) {
                int step = i % 2;
                buffers[step].setup();
                buffers[(step + 1) % 2].draw();
            }
            ShaderUtil.kawaseUp.attach();
            ShaderUtil.kawaseUp.setUniform("offset", offset);
            ShaderUtil.kawaseUp.setUniformf("resolution", 1f / mc.getMainWindow().getWidth(),
                    1f / mc.getMainWindow().getHeight());
            for (int i = 0; i < steps; ++i) {
                int step = i % 2;
                buffers[(step + 1) % 2].setup();
                buffers[step].draw();
            }
            ShaderUtil.kawaseUp.detach();
            mcFramebuffer.bindFramebuffer(false);
        }
    }
}