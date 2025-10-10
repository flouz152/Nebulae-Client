package beame.labyaddon.module.render;

import beame.labyaddon.config.NebulaeAddonConfig;
import beame.labyaddon.core.AddonModule;
import beame.labyaddon.util.IMinecraft;
import beame.labyaddon.util.animation.Animation;
import beame.labyaddon.util.animation.AnimationMath;
import beame.labyaddon.util.animation.Direction;
import beame.labyaddon.util.animation.impl.DecelerateAnimation;
import beame.labyaddon.util.color.ColorUtils;
import beame.labyaddon.util.math.MathUtil;
import beame.labyaddon.util.render.ClientHandler;
import beame.labyaddon.util.render.W2S;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import org.joml.Vector2d;
import org.lwjgl.opengl.GL11;

/**
 * Port of the original TargetESP module with the rendering logic used by the main client.
 */
public class TargetESPModule extends AddonModule implements IMinecraft {

    private static final ResourceLocation QUAD_TEXTURE = new ResourceLocation("night/image/target/Quad.png");
    private static final ResourceLocation QUAD2_TEXTURE = new ResourceLocation("night/image/target/Quad2.png");
    private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation("night/image/glow.png");

    private static final String[] TYPES = {"Призраки", "Круг", "Квадрат", "Новый квадрат"};

    private final Animation alpha = new DecelerateAnimation(600, 255.0D);
    private float alphaState = 0.0F;
    private long startTime = System.currentTimeMillis();

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
        LivingEntity candidate = null;
        Entity pointed = Minecraft.getInstance().pointedEntity;
        if (pointed instanceof LivingEntity && pointed != mc.player) {
            candidate = (LivingEntity) pointed;
        }

        if (candidate == null && mc.player != null && mc.world != null) {
            double bestDistance = Double.MAX_VALUE;
            for (Entity entity : mc.world.getAllEntities()) {
                if (entity instanceof LivingEntity living && living != mc.player && living.isAlive()) {
                    double dist = living.getDistanceSq(mc.player);
                    if (dist < bestDistance && dist < 144.0D) {
                        bestDistance = dist;
                        candidate = living;
                    }
                }
            }
        }

        this.currentTarget = candidate;
        alphaState = AnimationMath.fast(alphaState, candidate != null ? 1.0F : 0.0F, 8.0F);
        alpha.setDirection(candidate != null ? Direction.FORWARDS : Direction.BACKWARDS);
    }

    @Override
    public void onRender2D(MatrixStack stack, float partialTicks) {
        if (!isEnabled() || currentTarget == null) {
            return;
        }

        if ("Квадрат".equals(type) || "Новый квадрат".equals(type)) {
            draw2DMarker(stack, currentTarget, partialTicks);
        }
    }

    @Override
    public void onRender3D(MatrixStack stack, float partialTicks) {
        if (!isEnabled() || currentTarget == null) {
            return;
        }

        if ("Призраки".equals(type) || "Круг".equals(type)) {
            draw3DMarker(stack, currentTarget, partialTicks);
        }
    }

    private void draw2DMarker(MatrixStack stack, Entity target, float partialTicks) {
        if (target == null || target == mc.player) {
            return;
        }

        int baseColor = getBaseColor(target);
        Vector3d previous = new Vector3d(target.lastTickPosX, target.lastTickPosY, target.lastTickPosZ);
        Vector3d interpolated = MathUtil.interpolate(target.getPositionVec(), previous, partialTicks);
        Vector2d screen = W2S.project(interpolated.x, interpolated.y + target.getHeight() / 2.0F, interpolated.z);
        if (screen == null) {
            return;
        }

        float size = mc.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON ? 90.0F : 60.0F;
        float rotation = (float) (Math.sin(System.currentTimeMillis() / 1000.0D) * 120.0D);
        ResourceLocation texture = "Квадрат".equals(type) ? QUAD_TEXTURE : QUAD2_TEXTURE;
        ClientHandler.drawImage(stack, texture, (float) screen.x - size / 2.0F, (float) screen.y - size / 2.0F, size, size,
                ColorUtils.setAlpha(baseColor, (int) alpha.getOutput()), rotation);
    }

    private void draw3DMarker(MatrixStack stack, Entity target, float partialTicks) {
        if (target == null || target == mc.player) {
            return;
        }

        int color = getBaseColor(target);
        if ("Призраки".equals(type)) {
            renderGhosts(stack, target, partialTicks, color);
        } else {
            renderCircle(stack, target, partialTicks, color);
        }
    }

    private void renderGhosts(MatrixStack stack, Entity target, float partialTicks, int color) {
        stack.push();
        RenderSystem.pushMatrix();
        RenderSystem.disableLighting();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        RenderSystem.disableCull();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE, 0, 1);

        ActiveRenderInfo camera = mc.getRenderManager().info;
        stack.translate(-camera.getProjectedView().getX(), -camera.getProjectedView().getY(), -camera.getProjectedView().getZ());

        Vector3d previous = new Vector3d(target.lastTickPosX, target.lastTickPosY, target.lastTickPosZ);
        Vector3d interpolated = MathUtil.interpolate(target.getPositionVec(), previous, partialTicks);
        interpolated = interpolated.add(0.2D, target.getHeight() / 4.0F + 0.75D, 0.0D);
        stack.translate(interpolated.x, interpolated.y, interpolated.z);

        mc.getTextureManager().bindTexture(GLOW_TEXTURE);

        double radius = 0.7D;
        float speed = ghostsSpeed;
        float width = ghostsWidth;
        double distance = 12.0D;
        int length = Math.round(ghostsLength);
        float angleStep = ghostsAngle;
        float size = width * 60.0F;

        int[] base = ColorUtils.rgba(color);
        for (int axis = 0; axis < 3; axis++) {
            for (int i = 0; i < length; i++) {
                Quaternion rotation = camera.getRotation().copy();
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
                double angle = angleStep * ((System.currentTimeMillis() - startTime) - i * distance) / speed;
                double sin = Math.sin(angle) * radius;
                double cos = Math.cos(angle) * radius;
                switch (axis) {
                    case 0 -> stack.translate(sin, cos, -cos);
                    case 1 -> stack.translate(-sin, sin, -cos);
                    case 2 -> stack.translate(cos, -sin, -sin);
                }
                stack.translate(-size / 2.0F, -size / 2.0F, 0.0D);
                stack.rotate(rotation);
                stack.translate(size / 2.0F, size / 2.0F, 0.0D);

                int alphaValue = Math.max(0, Math.min(255, (int) (alphaState * (i * 10.0F))));
                buffer.pos(stack.getLast().getMatrix(), 0.0F, -size, 0.0F)
                        .color(base[0], base[1], base[2], alphaValue).tex(0.0F, 0.0F).endVertex();
                buffer.pos(stack.getLast().getMatrix(), -size, -size, 0.0F)
                        .color(base[0], base[1], base[2], alphaValue).tex(0.0F, 1.0F).endVertex();
                buffer.pos(stack.getLast().getMatrix(), -size, 0.0F, 0.0F)
                        .color(base[0], base[1], base[2], alphaValue).tex(1.0F, 1.0F).endVertex();
                buffer.pos(stack.getLast().getMatrix(), 0.0F, 0.0F, 0.0F)
                        .color(base[0], base[1], base[2], alphaValue).tex(1.0F, 0.0F).endVertex();
                WorldVertexBufferUploader.draw(buffer);

                stack.translate(-size / 2.0F, -size / 2.0F, 0.0D);
                rotation.conjugate();
                stack.rotate(rotation);
                stack.translate(size / 2.0F, size / 2.0F, 0.0D);
                switch (axis) {
                    case 0 -> stack.translate(-sin, -cos, cos);
                    case 1 -> stack.translate(sin, -sin, cos);
                    case 2 -> stack.translate(-cos, sin, sin);
                }
            }
        }

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.enableAlphaTest();
        RenderSystem.depthMask(true);
        RenderSystem.popMatrix();
        stack.pop();
    }

    private void renderCircle(MatrixStack stack, Entity target, float partialTicks, int color) {
        EntityRendererManager rm = mc.getRenderManager();
        stack.push();

        double x = target.lastTickPosX + (target.getPosX() - target.lastTickPosX) * partialTicks - rm.info.getProjectedView().getX();
        double y = target.lastTickPosY + (target.getPosY() - target.lastTickPosY) * partialTicks - rm.info.getProjectedView().getY();
        double z = target.lastTickPosZ + (target.getPosZ() - target.lastTickPosZ) * partialTicks - rm.info.getProjectedView().getZ();

        stack.translate(x, y, z);

        float height = target.getHeight();
        double duration = Math.max(10.0D, circleSpeed);
        double elapsed = System.currentTimeMillis() % duration;
        boolean side = elapsed > duration / 2.0D;
        double progress = elapsed / (duration / 2.0D);
        progress = side ? (progress - 1.0D) : 1.0D - progress;
        progress = progress < 0.5D ? 2.0D * progress * progress : 1.0D - Math.pow(-2.0D * progress + 2.0D, 2.0D) / 2.0D;
        double eased = (height / 2.0F) * (progress > 0.5D ? 1.0D - progress : progress) * (side ? -1.0D : 1.0D);

        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        RenderSystem.disableCull();
        RenderSystem.lineWidth(1.5F);

        int[] base = ColorUtils.rgba(color);
        int lineAlpha = Math.max(0, Math.min(255, (int) (base[3] * 0.5F)));

        buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= 360; i++) {
            double angle = Math.toRadians(i);
            double radius = target.getWidth() * 0.8D;
            float xPos = (float) (Math.cos(angle) * radius);
            float zPos = (float) (Math.sin(angle) * radius);
            buffer.pos(stack.getLast().getMatrix(), xPos, (float) (height * progress), zPos)
                    .color(base[0], base[1], base[2], lineAlpha).endVertex();
            buffer.pos(stack.getLast().getMatrix(), xPos, (float) (height * progress + eased), zPos)
                    .color(base[0], base[1], base[2], 0).endVertex();
        }
        WorldVertexBufferUploader.draw(buffer);

        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= 360; i++) {
            double angle = Math.toRadians(i);
            double radius = target.getWidth() * 0.8D;
            float xPos = (float) (Math.cos(angle) * radius);
            float zPos = (float) (Math.sin(angle) * radius);
            buffer.pos(stack.getLast().getMatrix(), xPos, (float) (height * progress), zPos)
                    .color(base[0], base[1], base[2], lineAlpha).endVertex();
        }
        WorldVertexBufferUploader.draw(buffer);

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        RenderSystem.enableAlphaTest();
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_DONT_CARE);
        RenderSystem.shadeModel(GL11.GL_FLAT);

        stack.pop();
    }

    private int getBaseColor(Entity target) {
        int themePrimary = 0xFF5AA8FF;
        int themeSecondary = 0xFF2A6BE6;

        if (redOnHurt && target instanceof LivingEntity living && living.hurtTime > 0) {
            return ColorUtils.interpolateColor(ColorUtils.rgba(220, 80, 80, 255), ColorUtils.rgba(220, 80, 80, 255),
                    Math.min(living.hurtTime / 2.0D, 1.0D));
        }

        if (target instanceof LivingEntity living) {
            return ColorUtils.interpolateColor(themeSecondary, themePrimary, Math.min(living.hurtTime / 2.0D, 1.0D));
        }

        return themePrimary;
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        alphaState = 0.0F;
        alpha.setDirection(Direction.BACKWARDS);
    }

    // Settings bridge for GUI
    public boolean isRedOnHurt() {
        return redOnHurt;
    }

    public void setRedOnHurt(boolean redOnHurt) {
        this.redOnHurt = redOnHurt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        for (String available : TYPES) {
            if (available.equals(type)) {
                this.type = type;
                return;
            }
        }
    }

    public String[] getAvailableTypes() {
        return TYPES.clone();
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
