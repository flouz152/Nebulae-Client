package beame.labyaddon.module.render;

import beame.labyaddon.config.NebulaeAddonConfig;
import beame.labyaddon.core.AddonModule;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

/**
 * Lightweight TargetESP implementation used by the standalone add-on.
 * <p>
 * The effect is intentionally less flashy than in the full client but retains
 * the rotating ghosts / circle visuals players expect.
 */
public class TargetESPModule extends AddonModule {

    private static final String[] TYPES = new String[]{"Призраки", "Круг", "Квадрат", "Новый квадрат"};

    private String type = TYPES[0];
    private boolean redOnHurt = true;
    private float ghostsSpeed = 33.0F;
    private float ghostsLength = 24.0F;
    private float ghostsWidth = 0.4F;
    private float ghostsAngle = 0.18F;
    private float circleSpeed = 2000.0F;

    private LivingEntity currentTarget;

    public TargetESPModule() {
        super("TargetESP");
    }

    @Override
    public void onTick() {
        Entity pointed = Minecraft.getInstance().pointedEntity;
        LivingEntity candidate = null;
        if (pointed instanceof LivingEntity && pointed != mc.player) {
            candidate = (LivingEntity) pointed;
        }

        if (candidate == null && mc.player != null && mc.world != null) {
            double bestDistance = Double.MAX_VALUE;
            for (Entity entity : mc.world.getAllEntities()) {
                if (entity instanceof LivingEntity && entity != mc.player && entity.isAlive()) {
                    double dist = entity.getDistanceSq(mc.player);
                    if (dist < bestDistance && dist < 144) { // 12 blocks
                        bestDistance = dist;
                        candidate = (LivingEntity) entity;
                    }
                }
            }
        }

        this.currentTarget = candidate;
    }

    @Override
    public void onRender3D(MatrixStack stack, float partialTicks) {
        LivingEntity target = this.currentTarget;
        if (target == null || !target.isAlive()) {
            return;
        }

        ActiveRenderInfo camera = mc.gameRenderer.getActiveRenderInfo();
        Vector3d cameraPos = camera.getProjectedView();

        double x = MathHelper.lerp(partialTicks, target.lastTickPosX, target.getPosX()) - cameraPos.x;
        double y = MathHelper.lerp(partialTicks, target.lastTickPosY, target.getPosY()) - cameraPos.y;
        double z = MathHelper.lerp(partialTicks, target.lastTickPosZ, target.getPosZ()) - cameraPos.z;

        stack.push();
        stack.translate(x, y, z);

        int color = redOnHurt && target.hurtTime > 0 ? 0xFFDC5050 : 0xFF5AA8FF;
        if ("Квадрат".equals(type) || "Новый квадрат".equals(type)) {
            drawGroundSquare(stack, target, color, "Новый квадрат".equals(type));
        } else if ("Круг".equals(type)) {
            drawCircle(stack, target, color, partialTicks);
        } else {
            drawGhosts(stack, target, color, partialTicks);
        }

        stack.pop();
    }

    private void drawCircle(MatrixStack stack, LivingEntity target, int color, float partialTicks) {
        RenderSystem.pushMatrix();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        float alpha = 0.5F;
        float radius = target.getWidth();
        double progress = (System.currentTimeMillis() % (long) circleSpeed) / circleSpeed;
        float offset = (float) progress * 360.0F;

        Tessellator tessellator = Tessellator.getInstance();
        Matrix4f matrix = stack.getLast().getMatrix();
        tessellator.getBuffer().begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < 90; i++) {
            double angle = Math.toRadians(offset + (i * 4));
            float x = (float) (Math.cos(angle) * radius * 1.4F);
            float z = (float) (Math.sin(angle) * radius * 1.4F);
            float y = 0.05F + target.getHeight() * 0.01F;
            tessellator.getBuffer().pos(matrix, x, y, z).color(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, (int) (alpha * 255.0F)).endVertex();
        }
        tessellator.draw();

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
    }

    private void drawGhosts(MatrixStack stack, LivingEntity target, int color, float partialTicks) {
        RenderSystem.pushMatrix();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();

        Matrix4f matrix = stack.getLast().getMatrix();
        Tessellator tessellator = Tessellator.getInstance();

        float alpha = 0.25F;
        float height = target.getHeight();
        float radius = Math.max(target.getWidth(), 0.6F) + ghostsWidth;
        int length = Math.max(6, Math.round(ghostsLength / 2));
        double time = System.currentTimeMillis() / ghostsSpeed;

        tessellator.getBuffer().begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < length; i++) {
            double angle = (time + i) * ghostsAngle;
            float x = (float) (Math.cos(angle) * radius);
            float z = (float) (Math.sin(angle) * radius);
            float y = (height * 0.5F) + (float) Math.sin(angle * 0.7F) * height * 0.25F;
            tessellator.getBuffer().pos(matrix, x, y, z).color(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, (int) (alpha * 255.0F)).endVertex();
        }
        tessellator.draw();

        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
    }

    private void drawGroundSquare(MatrixStack stack, LivingEntity target, int color, boolean filled) {
        RenderSystem.pushMatrix();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        float radius = Math.max(target.getWidth(), 0.6F) * 1.5F;
        float y = 0.02F;
        Matrix4f matrix = stack.getLast().getMatrix();
        Tessellator tessellator = Tessellator.getInstance();

        if (filled) {
            tessellator.getBuffer().begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
        } else {
            tessellator.getBuffer().begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        }

        int alpha = filled ? 90 : 200;
        float[][] corners = new float[][]{
                {-radius, y, -radius},
                {radius, y, -radius},
                {radius, y, radius},
                {-radius, y, radius}
        };

        for (float[] corner : corners) {
            tessellator.getBuffer().pos(matrix, corner[0], corner[1], corner[2])
                    .color(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, alpha)
                    .endVertex();
        }

        if (!filled) {
            tessellator.getBuffer().pos(matrix, corners[0][0], corners[0][1], corners[0][2])
                    .color(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, alpha)
                    .endVertex();
        }

        tessellator.draw();

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
    }

    public boolean isRedOnHurt() {
        return redOnHurt;
    }

    public void setRedOnHurt(boolean redOnHurt) {
        this.redOnHurt = redOnHurt;
    }

    public String[] getAvailableTypes() {
        return TYPES;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (Arrays.asList(TYPES).contains(type)) {
            this.type = type;
        }
    }

    public float getGhostsSpeed() {
        return ghostsSpeed;
    }

    public void setGhostsSpeed(float ghostsSpeed) {
        this.ghostsSpeed = ghostsSpeed;
    }

    public float getGhostsLength() {
        return ghostsLength;
    }

    public void setGhostsLength(float ghostsLength) {
        this.ghostsLength = ghostsLength;
    }

    public float getGhostsWidth() {
        return ghostsWidth;
    }

    public void setGhostsWidth(float ghostsWidth) {
        this.ghostsWidth = ghostsWidth;
    }

    public float getGhostsAngle() {
        return ghostsAngle;
    }

    public void setGhostsAngle(float ghostsAngle) {
        this.ghostsAngle = ghostsAngle;
    }

    public float getCircleSpeed() {
        return circleSpeed;
    }

    public void setCircleSpeed(float circleSpeed) {
        this.circleSpeed = circleSpeed;
    }

    public void applyConfig(NebulaeAddonConfig config) {
        setEnabled(config.targetEspEnabled.get());
        setType(config.targetEspType.get());
        setRedOnHurt(config.redOnHurt.get());
        setGhostsSpeed(config.ghostsSpeed.get().floatValue());
        setGhostsLength(config.ghostsLength.get().floatValue());
        setGhostsWidth(config.ghostsWidth.get().floatValue());
        setGhostsAngle(config.ghostsAngle.get().floatValue());
        setCircleSpeed(config.speedCircle.get().floatValue());
    }

    public void exportConfig(NebulaeAddonConfig config) {
        config.targetEspEnabled.set(isEnabled());
        config.targetEspType.set(getType());
        config.redOnHurt.set(isRedOnHurt());
        config.ghostsSpeed.set((double) getGhostsSpeed());
        config.ghostsLength.set((double) getGhostsLength());
        config.ghostsWidth.set((double) getGhostsWidth());
        config.ghostsAngle.set((double) getGhostsAngle());
        config.speedCircle.set((double) getCircleSpeed());
    }
}
