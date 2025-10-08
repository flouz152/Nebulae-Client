package beame.components.modules.render;

import beame.Essence;
import beame.module.Category;
import beame.module.Module;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.RadioSetting;
import beame.setting.SettingList.SliderSetting;
import beame.util.color.ColorUtils;
import beame.util.render.W2S;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import events.Event;
import events.impl.player.EventAttack;
import events.impl.player.EventUpdate;
import events.impl.player.WorldChangeEvent;
import events.impl.render.Render2DEvent;
import events.impl.render.Render3DLastEvent;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import org.joml.Vector2d;
import org.lwjgl.opengl.GL11;

public class TargetESP extends Module {

    private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation("night/image/glow.png");
    private static final ResourceLocation QUAD_TEXTURE = new ResourceLocation("night/image/target/Quad.png");
    private static final ResourceLocation QUAD_NEW_TEXTURE = new ResourceLocation("night/image/target/Quad2.png");

    private final RadioSetting mode = new RadioSetting("Тип", "Призраки", "Призраки", "Круг", "Квадрат", "Новый квадрат");
    private final BooleanSetting redOnHurt = new BooleanSetting("Краснеть при ударе", true);
    private final SliderSetting ghostsSpeed = new SliderSetting("Скорость призраков", 33.0f, 5.0f, 100.0f, 1.0f)
            .setVisible(() -> mode.is("Призраки"));
    private final SliderSetting ghostsLength = new SliderSetting("Длина призраков", 24.0f, 5.0f, 64.0f, 1.0f)
            .setVisible(() -> mode.is("Призраки"));
    private final SliderSetting ghostsWidth = new SliderSetting("Ширина призраков", 0.4f, 0.1f, 1.0f, 0.01f)
            .setVisible(() -> mode.is("Призраки"));
    private final SliderSetting ghostsAngle = new SliderSetting("Угол вращения призраков", 0.18f, 0.01f, 1.0f, 0.01f)
            .setVisible(() -> mode.is("Призраки"));
    private final SliderSetting circleSpeed = new SliderSetting("Скорость круга", 2000.0f, 10.0f, 10000.0f, 1.0f)
            .setVisible(() -> mode.is("Круг"));

    private final TargetState tracker = new TargetState();
    private long animationOrigin = System.currentTimeMillis();

    public TargetESP() {
        super("TargetESP", Category.Visuals, true, "Включает эффект подсвечивания таргета");
        addSettings(mode, redOnHurt, ghostsSpeed, ghostsLength, ghostsWidth, ghostsAngle, circleSpeed);
    }

    @Override
    public void event(Event event) {
        if (event instanceof EventAttack attack) {
            handleAttack(attack);
            return;
        }

        if (event instanceof WorldChangeEvent) {
            tracker.reset();
            return;
        }

        if (event instanceof EventUpdate) {
            tickTracker();
            return;
        }

        LivingEntity target = tracker.getTarget();
        float alpha = tracker.getVisibility();
        if (target == null || alpha <= 0.01F) {
            return;
        }

        if (event instanceof Render3DLastEvent render3D) {
            if (mode.is("Призраки")) {
                renderGhosts(render3D.getMatrix(), render3D, target, alpha);
            } else if (mode.is("Круг")) {
                renderCircle(render3D.getMatrix(), render3D, target, alpha);
            }
        } else if (event instanceof Render2DEvent render2D) {
            if (mode.is("Квадрат") || mode.is("Новый квадрат")) {
                ResourceLocation texture = mode.is("Квадрат") ? QUAD_TEXTURE : QUAD_NEW_TEXTURE;
                renderOverlay(render2D.getMatrix(), render2D.getPartialTicks(), target, texture, alpha);
            }
        }
    }

    private void handleAttack(EventAttack attack) {
        if (!(attack.getTarget() instanceof LivingEntity living)) {
            return;
        }

        if (living == mc.player) {
            return;
        }

        tracker.setTarget(living);
        animationOrigin = System.currentTimeMillis();
    }

    private void tickTracker() {
        if (mc.player == null || mc.world == null) {
            tracker.reset();
            return;
        }

        LivingEntity target = tracker.getTarget();
        if (target != null) {
            boolean invalidWorld = target.getEntityWorld() != mc.world;
            boolean invalidDistance = mc.player.getDistance(target) > 5.0F;
            if (!target.isAlive() || target.removed || invalidWorld || invalidDistance) {
                tracker.clearTarget();
            }
        }

        tracker.updateFade();
    }

    private void renderGhosts(MatrixStack stack, Render3DLastEvent event, LivingEntity target, float alpha) {
        ActiveRenderInfo camera = event.getActiveRenderInfo();
        if (camera == null) {
            return;
        }

        Vector3d projected = camera.getProjectedView();
        double x = MathHelper.lerp(event.getPartialTicks(), target.lastTickPosX, target.getPosX()) - projected.getX();
        double y = MathHelper.lerp(event.getPartialTicks(), target.lastTickPosY, target.getPosY()) - projected.getY();
        double z = MathHelper.lerp(event.getPartialTicks(), target.lastTickPosZ, target.getPosZ()) - projected.getZ();

        stack.push();
        RenderSystem.pushMatrix();
        RenderSystem.disableLighting();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        RenderSystem.disableCull();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO, GL11.GL_ONE);

        stack.translate(x + 0.2D, y + target.getHeight() / 4.0F + 0.75D, z);
        mc.getTextureManager().bindTexture(GLOW_TEXTURE);

        float speed = Math.max(1.0F, ghostsSpeed.get());
        int length = Math.max(1, ghostsLength.get().intValue());
        float size = ghostsWidth.get();
        float angleStep = ghostsAngle.get();
        double radius = 0.7D;
        double spacing = 12.0D;

        int baseColor = resolveBaseColor(target);
        long elapsed = System.currentTimeMillis() - animationOrigin;

        for (int axis = 0; axis < 3; axis++) {
            for (int i = 0; i < length; i++) {
                Quaternion rotation = camera.getRotation().copy();
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);

                double angle = angleStep * ((elapsed) - i * spacing) / speed;
                double sin = Math.sin(angle) * radius;
                double cos = Math.cos(angle) * radius;

                applyAxisTranslation(stack, axis, sin, cos);
                stack.translate(-size / 2.0F, -size / 2.0F, 0.0D);
                stack.rotate(rotation);
                stack.translate(size / 2.0F, size / 2.0F, 0.0D);

                Matrix4f matrix = stack.getLast().getMatrix();
                float falloff = 1.0F - (float) i / (float) length;
                int tinted = ColorUtils.setAlpha(baseColor, (int) (alpha * 255.0F * falloff));
                int r = ColorUtils.getRed(tinted);
                int g = ColorUtils.getGreen(tinted);
                int b = ColorUtils.getBlue(tinted);
                int a = ColorUtils.getAlpha(tinted);

                buffer.pos(matrix, 0.0F, -size, 0.0F).color(r, g, b, a).tex(0.0F, 0.0F).endVertex();
                buffer.pos(matrix, -size, -size, 0.0F).color(r, g, b, a).tex(0.0F, 1.0F).endVertex();
                buffer.pos(matrix, -size, 0.0F, 0.0F).color(r, g, b, a).tex(1.0F, 1.0F).endVertex();
                buffer.pos(matrix, 0.0F, 0.0F, 0.0F).color(r, g, b, a).tex(1.0F, 0.0F).endVertex();
                tessellator.draw();

                stack.translate(-size / 2.0F, -size / 2.0F, 0.0D);
                rotation.conjugate();
                stack.rotate(rotation);
                stack.translate(size / 2.0F, size / 2.0F, 0.0D);
                revertAxisTranslation(stack, axis, sin, cos);
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

    private void renderCircle(MatrixStack stack, Render3DLastEvent event, LivingEntity target, float alpha) {
        ActiveRenderInfo camera = event.getActiveRenderInfo();
        if (camera == null) {
            return;
        }

        Vector3d projected = camera.getProjectedView();
        double x = MathHelper.lerp(event.getPartialTicks(), target.lastTickPosX, target.getPosX()) - projected.getX();
        double y = MathHelper.lerp(event.getPartialTicks(), target.lastTickPosY, target.getPosY()) - projected.getY();
        double z = MathHelper.lerp(event.getPartialTicks(), target.lastTickPosZ, target.getPosZ()) - projected.getZ();

        stack.push();
        stack.translate(x, y, z);

        double duration = Math.max(10.0D, circleSpeed.get());
        double elapsed = (System.currentTimeMillis() - animationOrigin) % duration;
        boolean reverse = elapsed > duration / 2.0D;
        double progress = elapsed / (duration / 2.0D);
        progress = reverse ? progress - 1.0D : 1.0D - progress;
        progress = progress < 0.5D ? 2.0D * progress * progress : 1.0D - Math.pow(-2.0D * progress + 2.0D, 2.0D) / 2.0D;
        double eased = target.getHeight() / 2.0D * (progress > 0.5D ? 1.0D - progress : progress) * (reverse ? -1.0D : 1.0D);

        int baseColor = resolveBaseColor(target);
        int solid = ColorUtils.setAlpha(baseColor, (int) (alpha * 200.0F));
        int transparent = ColorUtils.setAlpha(baseColor, 0);

        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        RenderSystem.disableCull();
        RenderSystem.lineWidth(1.5F);

        Matrix4f matrix = stack.getLast().getMatrix();

        buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= 360; i++) {
            double radians = Math.toRadians(i);
            float px = (float) (Math.cos(radians) * target.getWidth() * 0.8D);
            float pz = (float) (Math.sin(radians) * target.getWidth() * 0.8D);

            int sr = ColorUtils.getRed(solid);
            int sg = ColorUtils.getGreen(solid);
            int sb = ColorUtils.getBlue(solid);
            int sa = ColorUtils.getAlpha(solid);

            int tr = ColorUtils.getRed(transparent);
            int tg = ColorUtils.getGreen(transparent);
            int tb = ColorUtils.getBlue(transparent);
            int ta = ColorUtils.getAlpha(transparent);

            buffer.pos(matrix, px, (float) (target.getHeight() * progress), pz).color(sr, sg, sb, sa).endVertex();
            buffer.pos(matrix, px, (float) (target.getHeight() * progress + eased), pz).color(tr, tg, tb, ta).endVertex();
        }
        tessellator.draw();

        buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= 360; i++) {
            double radians = Math.toRadians(i);
            float px = (float) (Math.cos(radians) * target.getWidth() * 0.8D);
            float pz = (float) (Math.sin(radians) * target.getWidth() * 0.8D);
            int sr = ColorUtils.getRed(solid);
            int sg = ColorUtils.getGreen(solid);
            int sb = ColorUtils.getBlue(solid);
            int sa = ColorUtils.getAlpha(solid);
            buffer.pos(matrix, px, (float) (target.getHeight() * progress), pz).color(sr, sg, sb, sa).endVertex();
        }
        tessellator.draw();

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        RenderSystem.enableAlphaTest();
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        RenderSystem.shadeModel(GL11.GL_FLAT);

        stack.pop();
    }

    private void renderOverlay(MatrixStack stack, float partialTicks, LivingEntity target, ResourceLocation texture, float alpha) {
        double x = MathHelper.lerp(partialTicks, target.lastTickPosX, target.getPosX());
        double y = MathHelper.lerp(partialTicks, target.lastTickPosY, target.getPosY()) + target.getHeight() / 2.0D;
        double z = MathHelper.lerp(partialTicks, target.lastTickPosZ, target.getPosZ());

        Vector2d screen = W2S.project(x, y, z);
        if (screen == null) {
            return;
        }

        float size = mc.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON ? 90.0F : 60.0F;
        float rotation = (float) (Math.sin((System.currentTimeMillis() - animationOrigin) / 1000.0D) * 120.0D);
        int color = ColorUtils.setAlpha(resolveBaseColor(target), (int) (alpha * 255.0F));
        int r = ColorUtils.getRed(color);
        int g = ColorUtils.getGreen(color);
        int b = ColorUtils.getBlue(color);
        int a = ColorUtils.getAlpha(color);

        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        mc.getTextureManager().bindTexture(texture);

        stack.push();
        stack.translate((float) screen.x, (float) screen.y, 0.0D);
        stack.rotate(Vector3f.ZP.rotationDegrees(rotation));
        stack.translate(-(float) screen.x, -(float) screen.y, 0.0D);

        Matrix4f matrix = stack.getLast().getMatrix();
        float left = (float) screen.x - size / 2.0F;
        float top = (float) screen.y - size / 2.0F;
        float right = (float) screen.x + size / 2.0F;
        float bottom = (float) screen.y + size / 2.0F;

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
        buffer.pos(matrix, left, top, 0.0F).color(r, g, b, a).tex(0.0F, 0.0F).endVertex();
        buffer.pos(matrix, left, bottom, 0.0F).color(r, g, b, a).tex(0.0F, 1.0F).endVertex();
        buffer.pos(matrix, right, bottom, 0.0F).color(r, g, b, a).tex(1.0F, 1.0F).endVertex();
        buffer.pos(matrix, right, top, 0.0F).color(r, g, b, a).tex(1.0F, 0.0F).endVertex();
        tessellator.draw();

        stack.pop();
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
    }

    private int resolveBaseColor(LivingEntity target) {
        int primary = Essence.getHandler().themeManager.getColor(0);
        int secondary = Essence.getHandler().themeManager.getColor(1);
        double cycle = (Math.sin((System.currentTimeMillis() - animationOrigin) / 600.0D) + 1.0D) / 2.0D;
        int dynamic = ColorUtils.interpolate(secondary, primary, cycle);

        if (redOnHurt.get() && target.hurtTime > 0) {
            return ColorUtils.rgba(220, 80, 80, 255);
        }

        float hurtProgress = MathHelper.clamp(target.hurtTime / 10.0F, 0.0F, 1.0F);
        int hurtTint = ColorUtils.rgba(150, 90, 255, 255);
        return ColorUtils.interpolate(dynamic, hurtTint, hurtProgress);
    }

    private void applyAxisTranslation(MatrixStack stack, int axis, double sin, double cos) {
        switch (axis) {
            case 0 -> stack.translate(sin, cos, -cos);
            case 1 -> stack.translate(-sin, sin, -cos);
            case 2 -> stack.translate(cos, -sin, -sin);
            default -> {
            }
        }
    }

    private void revertAxisTranslation(MatrixStack stack, int axis, double sin, double cos) {
        switch (axis) {
            case 0 -> stack.translate(-sin, -cos, cos);
            case 1 -> stack.translate(sin, -sin, cos);
            case 2 -> stack.translate(-cos, sin, sin);
            default -> {
            }
        }
    }

    @Override
    protected void onDisable() {
        tracker.reset();
        super.onDisable();
    }

    private static final class TargetState {
        private LivingEntity target;
        private float visibility;

        void setTarget(LivingEntity target) {
            this.target = target;
            this.visibility = 0.0F;
        }

        LivingEntity getTarget() {
            return target;
        }

        float getVisibility() {
            return visibility;
        }

        void clearTarget() {
            this.target = null;
        }

        void updateFade() {
            float goal = this.target != null ? 1.0F : 0.0F;
            this.visibility = MathHelper.lerp(0.2F, this.visibility, goal);
            if (this.visibility < 0.01F && this.target == null) {
                this.visibility = 0.0F;
            }
        }

        void reset() {
            this.target = null;
            this.visibility = 0.0F;
        }
    }
}
