package beame.feature.gps;

import beame.Nebulae;
import beame.util.fonts.Fonts;
import beame.util.render.ClientHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.math.MathHelper;

import static beame.util.IMinecraft.mc;

public class GPS {
// leaked by itskekoff; discord.gg/sk3d qLqMiVxd
    private float globalX = 0;
    private float globalZ = 0;
    private boolean enabled = false;

    public void init(float x, float z) {
        this.enabled = true;
        this.globalX = x;
        this.globalZ = z;
    }

    public void disable() {
        this.enabled = false;
    }

    public void renderGPS(MatrixStack stack) {
        if(!enabled)
            return;

        double x = globalX - mc.getRenderManager().info.getProjectedView().getX();
        double z = globalZ - mc.getRenderManager().info.getProjectedView().getZ();
        double cos2 = MathHelper.cos((float)(mc.player.rotationYaw * (Math.PI / 180)));
        double sin2 = MathHelper.sin((float)(mc.player.rotationYaw * (Math.PI / 180)));
        double rotY = -(z * cos2 - x * sin2);
        double rotX = -(x * cos2 + z * sin2);
        double dst = Math.sqrt(Math.pow(globalX - mc.player.getPosX(), 2.0) + Math.pow(globalZ - mc.player.getPosZ(), 2.0));
        float angle = (float)(Math.atan2(rotY, rotX) * 180.0 / Math.PI);
        double x2 = 90.0f * MathHelper.cos((float)Math.toRadians(angle)) + (float)mc.getMainWindow().scaledWidth() / 2.0f;
        double y2 = 90.0f * MathHelper.sin((float)Math.toRadians(angle)) + (float)mc.getMainWindow().scaledHeight() / 2.0f;

        GlStateManager.pushMatrix();
        GlStateManager.disableBlend();
        GlStateManager.translated(x2, y2, 0.0);
        GlStateManager.rotatef(angle + 90.0f, 0.0f, 0.0f, 1.0f);
        ClientHandler.drawArrow(stack, 0.0f, 0.0f, 17.0f, Nebulae.getHandler().themeManager.getColor(0));
        GlStateManager.enableBlend();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.disableBlend();
        GlStateManager.translated(x2, y2, 0.0);
        GlStateManager.rotatef(0.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.enableBlend();
        GlStateManager.popMatrix();

        Fonts.SUISSEINTL.get(14).drawCenteredString(stack, (int)dst + "m", mc.getMainWindow().getScaledWidth() / 2, mc.getMainWindow().getScaledHeight() / 2 + 10, -1);
    }
}
