package beame.components.modules.render;

import beame.Essence;
import beame.util.animation.AnimationMath;
import beame.util.color.ColorUtils;
import beame.util.math.MathUtil;
import beame.util.math.TimerUtil;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import events.Event;
import events.impl.player.EventJump;
import events.impl.render.EventRender;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.RadioSetting;
import beame.setting.SettingList.EnumSetting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Visuality extends Module {
// leaked by itskekoff; discord.gg/sk3d KwG8JysU
    public Visuality() {
        super("Visuality", Category.Visuals, true, "Визуальная косметика для игрока");
        addSettings(elements, hatType);
    }

    private final EnumSetting elements = new EnumSetting("Отображать",
            new BooleanSetting("Шляпа", true, 0),
            new BooleanSetting("Круг от прыжка", true, 0),
            new BooleanSetting("След", true, 0)
    );

    private final RadioSetting hatType = new RadioSetting("Стиль шляпы",
            "Китайская",
            "Китайская", "Нимб"
    ).setVisible(() -> elements.get(0).get());

    ArrayList<TrailParticle> particles = new ArrayList<>();
    private final Map<PlayerEntity, Vector3d> lastPos = new HashMap<>();

    public List<Point> points = new ArrayList<Point>();
    public List<Circle> circles = new ArrayList<Circle>();

    @Override
    public void event(Event event) {
        if (event instanceof EventJump) {
            addCircle();
        }
        if (event instanceof EventRender) {
            if (elements.get(0).get()) {
                if (mc.gameSettings.getPointOfView() != PointOfView.FIRST_PERSON) {
                    this.renderHat((EventRender) event);
                }
            }

            if (elements.get(1).get()) {
                if (((EventRender) event).isRender3D()) {
                    this.updateCircles();
                    this.renderCircles();
                }
            }

            if (elements.get(2).get()) {
                if (mc.gameSettings.getPointOfView() != PointOfView.FIRST_PERSON) {
                    long currentTime = System.currentTimeMillis();
                    this.points.removeIf(p -> (float) (currentTime - p.time) > (8 * 100.0f));
                    Vector3d playerPos = this.interpolatePlayerPosition(((EventRender) event).partialTicks);

                    this.points.removeIf(p -> p.pos.distanceTo(playerPos) > 1.5);

                    this.points.add(new Point(playerPos));
                    this.render3DPoints();
                    RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                }
            }
        }
    }

    private void renderHat(EventRender event) {
        float[] colors;
        int i;

        boolean isNumbus = hatType.is("Нимб");

        EntityRendererManager rm = mc.getRenderManager();
        assert mc.player != null;
        double xHat = mc.player.lastTickPosX + (mc.player.getPosX() - mc.player.lastTickPosX) * event.partialTicks - rm.info.getProjectedView().getX();
        double yHat = mc.player.lastTickPosY + (mc.player.getPosY() - mc.player.lastTickPosY) * event.partialTicks - rm.info.getProjectedView().getY() + mc.player.getHeight() + 0.1;
        double zHat = mc.player.lastTickPosZ + (mc.player.getPosZ() - mc.player.lastTickPosZ) * event.partialTicks - rm.info.getProjectedView().getZ();
        float size = isNumbus ? 0.25f : 0.5f;
        RenderSystem.pushMatrix();
        GL11.glDepthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.shadeModel(7425);
        RenderSystem.disableCull();
        RenderSystem.lineWidth(isNumbus ? 7.0f : 2.0f);
        RenderSystem.color4f(-1.0f, -1.0f, -1.0f, -1.0f);
        buffer.begin(8, DefaultVertexFormats.POSITION_COLOR);
        float f = size = isNumbus ? 0.25f : 0.5f;
        if (!isNumbus) {
            for (i = 0; i <= 360; ++i) {
                colors = ColorUtils.rgb(Essence.getHandler().themeManager.getColor(0));
                buffer.pos(xHat, yHat + 0.25, zHat).color(colors[0], colors[1], colors[2], 0.3f).endVertex();
                buffer.pos(xHat + Math.cos(Math.toRadians(i)) * (double) size, yHat, zHat + Math.sin(Math.toRadians(i)) * (double) size).color(colors[0], colors[1], colors[2], 0.3f).endVertex();
            }
        }
        buffer.finishDrawing();
        WorldVertexBufferUploader.draw(buffer);
        RenderSystem.color4f(-1.0f, -1.0f, -1.0f, -1.0f);
        buffer.begin(2, DefaultVertexFormats.POSITION_COLOR);
        for (i = 0; i <= 360; ++i) {
            colors = ColorUtils.rgb(Essence.getHandler().themeManager.getColor(0));
            buffer.pos(xHat + Math.cos(Math.toRadians(i)) * (double) size, yHat, zHat + Math.sin(Math.toRadians(i)) * (double) size).color(colors[0], colors[1], colors[2], 1.0f).endVertex();
        }
        buffer.finishDrawing();
        WorldVertexBufferUploader.draw(buffer);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        RenderSystem.enableAlphaTest();
        GL11.glDepthMask(true);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4354);
        RenderSystem.shadeModel(7424);
        RenderSystem.popMatrix();
    }

    private Vector3d interpolatePlayerPosition(float partialTicks) {
        return new Vector3d(MathUtil.interpolate(mc.player.getPosX(), mc.player.prevPosX, partialTicks), MathUtil.interpolate(mc.player.getPosY(), mc.player.prevPosY, partialTicks), MathUtil.interpolate(mc.player.getPosZ(), mc.player.prevPosZ, partialTicks));
    }

    private void render3DPoints() {
        this.startRendering();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(8, DefaultVertexFormats.POSITION_COLOR);
        int index = 0;
        for (Point p : this.points) {
            int c = Essence.getHandler().themeManager.getColor(0);
            float red = (float) (c >> 16 & 0xFF) / 255.0f;
            float green = (float) (c >> 8 & 0xFF) / 255.0f;
            float blue = (float) (c & 0xFF) / 255.0f;
            float alpha = (float) index / (float) this.points.size() * 0.7f;
            Vector3d pos = p.pos.subtract(mc.getRenderManager().info.getProjectedView());
            buffer.pos(pos.x, pos.y + (double) mc.player.getHeight(), pos.z).color(red, green, blue, alpha).endVertex();
            buffer.pos(pos.x, pos.y, pos.z).color(red, green, blue, alpha).endVertex();
            ++index;
        }
        tessellator.draw();
        RenderSystem.lineWidth(2.0f);
        this.renderLineStrip(this.points, true);
        this.renderLineStrip(this.points, false);
        this.stopRendering();
    }

    private void renderLineStrip(List<Point> points, boolean withHeight) {
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
        Vector3d projectedView = mc.getRenderManager().info.getProjectedView();
        int index = 0;
        for (Point p : points) {
            int c = Essence.getHandler().themeManager.getColor(0);
            float red = (float) (c >> 16 & 0xFF) / 255.0f;
            float green = (float) (c >> 8 & 0xFF) / 255.0f;
            float blue = (float) (c & 0xFF) / 255.0f;
            float alpha = (float) index / (float) points.size() * 1.5f;
            alpha = Math.min(alpha, 1.0f);
            Vector3d pos = p.pos.subtract(projectedView);
            if (withHeight) {
                buffer.pos(pos.x, pos.y + (double) mc.player.getHeight(), pos.z).color(red, green, blue, alpha).endVertex();
            } else {
                buffer.pos(pos.x, pos.y, pos.z).color(red, green, blue, alpha).endVertex();
            }
            ++index;
        }
        tessellator.draw();
    }

    private void startRendering() {
        RenderSystem.pushMatrix();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.disableAlphaTest();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 0.5f);
    }

    private void stopRendering() {
        RenderSystem.enableAlphaTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
    }

    private void addCircle() {
        this.circles.add(new Circle((float) mc.player.getPosX(), (float) mc.player.getPosY(), (float) mc.player.getPosZ()));
    }

    private void updateCircles() {
        for (Circle circle3 : this.circles) {
            circle3.factor = AnimationMath.fast(circle3.factor, 1.5f + 0.1f, 1.0f);
            circle3.alpha = AnimationMath.fast(circle3.alpha, 0.0f, 1.5f);
        }
        if (!this.circles.isEmpty()) {
            this.circles.removeIf(circle2 -> circle2.alpha <= 0.005f);
        }
    }

    private void renderCircles() {
        this.setupRenderSettings();
        for (Circle circle2 : this.circles) {
            this.drawJumpCircle(circle2, circle2.factor);
        }
        this.restoreRenderSettings();
    }

    private void setupRenderSettings() {
        RenderSystem.pushMatrix();
        RenderSystem.disableLighting();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.shadeModel(7425);
        RenderSystem.disableCull();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(770, 1, 0, 1);
        GlStateManager.translated(-mc.getRenderManager().info.getProjectedView().getX(), -mc.getRenderManager().info.getProjectedView().getY(), -mc.getRenderManager().info.getProjectedView().getZ());
    }

    private void restoreRenderSettings() {
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.enableAlphaTest();
        RenderSystem.depthMask(true);
        RenderSystem.popMatrix();
    }

    private void drawJumpCircle(Circle circle2, float radius) {
        double x = circle2.spawnX;
        double y = (double) circle2.spawnY + 0.1;
        double z = circle2.spawnZ;
        GlStateManager.translated(x, y, z);
        GlStateManager.rotatef(circle2.factor * 70.0f, 0.0f, -1.0f, 0.0f);
        mc.getTextureManager().bindTexture(new ResourceLocation("night/image/jump.png"));
        buffer.begin(8, DefaultVertexFormats.POSITION_COLOR_TEX);
        int i = 0;
        while ((float) i <= 360.0f) {
            float[] colors = ColorUtils.rgb(Essence.getHandler().themeManager.getColor(0));
            double sin2 = MathHelper.sin((float) Math.toRadians((float) i + 0.1f)) * radius;
            double cos2 = MathHelper.cos((float) Math.toRadians((float) i + 0.1f)) * radius;
            buffer.pos(0.0, 0.0, 0.0).color(colors[0], colors[1], colors[2], MathHelper.clamp(circle2.alpha, 0.0f, 1.0f)).tex(0.5f, 0.5f).endVertex();
            buffer.pos(sin2, 0.0, cos2).color(colors[0], colors[1], colors[2], MathHelper.clamp(circle2.alpha, 0.0f, 1.0f)).tex((float) (sin2 / (double) (2.0f * radius) + 0.5), (float) (cos2 / (double) (2.0f * radius) + 0.5)).endVertex();
            ++i;
        }
        tessellator.draw();
        GlStateManager.rotatef(-circle2.factor * 70.0f, 0.0f, -1.0f, 0.0f);
        GlStateManager.translated(-x, -y, -z);
    }

    static class TrailParticle {
        public Vector3d pos;
        public long time;
        public TimerUtil timer;
        public int color;

        public TrailParticle(Vector3d pos, long time, int color) {
            this.time = time;
            this.color = color;
            this.timer = new TimerUtil();
            this.pos = pos;
        }

        public float getX() {
            return (float) pos.x;
        }

        public float getY() {
            return (float) pos.y;
        }

        public float getZ() {
            return (float) pos.z;
        }
    }

    static class Point {
        public Vector3d pos;
        public long time;

        public Point(Vector3d pos) {
            this.pos = pos;
            this.time = System.currentTimeMillis();
        }
    }

    static class Circle {
        public final float spawnX;
        public final float spawnY;
        public final float spawnZ;
        public float factor = 0.0f;
        public float alpha = 5.0f;

        public Circle(float spawnX, float spawnY, float spawnZ) {
            this.spawnX = spawnX;
            this.spawnY = spawnY;
            this.spawnZ = spawnZ;
        }
    }
}