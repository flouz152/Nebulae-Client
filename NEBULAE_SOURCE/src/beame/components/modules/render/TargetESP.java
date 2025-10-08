package beame.components.modules.render;

import beame.Nebulae;
import beame.components.modules.combat.Aura;
import beame.util.animation.Animation;
import beame.util.animation.AnimationMath;
import beame.util.animation.Direction;
import beame.util.animation.impl.DecelerateAnimation;
import beame.util.color.ColorUtils;
import beame.util.math.MathUtil;
import beame.util.render.ClientHandler;
import beame.util.render.W2S;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import events.Event;
import events.impl.render.Render2DEvent;
import events.impl.render.Render3DLastEvent;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.joml.Vector2d;
import org.lwjgl.opengl.GL11;
import beame.setting.SettingList.RadioSetting;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.SliderSetting;

import static beame.util.color.ColorUtils.interpolateColor;

public class TargetESP extends Module {
// leaked by itskekoff; discord.gg/sk3d vgRsuBoe
    public TargetESP() {
        super("TargetESP", Category.Visuals, true, "Включает эффект подсвечивания таргета");
        addSettings(type, redOnHurt, ghostsSpeed, ghostsLength, ghostsWidth, speedcircle);
    }

    public final RadioSetting type = new RadioSetting("Тип", "Призраки", "Призраки", "Круг", "Квадрат","Новый квадрат");
    public final BooleanSetting redOnHurt = new BooleanSetting("Краснеть при ударе", true);
    public final SliderSetting ghostsSpeed = new SliderSetting("Скорость призраков", 33.0f, 5.0f, 100.0f, 1.0f).setVisible(() -> type.is("Призраки"));
    public final SliderSetting ghostsLength = new SliderSetting("Длина призраков", 24, 5, 64, 1).setVisible(() -> type.is("Призраки"));
    public final SliderSetting ghostsWidth = new SliderSetting("Ширина призраков", 0.4f, 0.1f, 1f, 0.01f).setVisible(() -> type.is("Призраки"));
    public final SliderSetting ghostsAngle = new SliderSetting("Угол вращения призраков", 0.18f, 0.01f, 1.0f, 0.01f).setVisible(() -> type.is("Призраки"));
    public final SliderSetting speedcircle = new SliderSetting("Скорость круга", 2000f, 10f, 10000f, 1f).setVisible(() -> type.is("Круг"));
    public static long startTime = System.currentTimeMillis();

    @Override
    public void event(Event event) {
        Aura aura = Nebulae.getHandler().getModuleList().aura;
        alphaState = AnimationMath.fast(alphaState, aura.getTarget() != null ? 1 : 0, 8);

        if(!aura.isState()) return;

        Animation var2;
        Direction var3;
        label15: {
            var2 = this.alpha;
            if (aura.isState()) {
                if (aura.getTarget() != null) {
                    var3 = Direction.FORWARDS;
                    break label15;
                }
            }
            var3 = Direction.BACKWARDS;
        }

        var2.setDirection(var3);

        if(event instanceof Render2DEvent) {
            Render2DEvent render2D = (Render2DEvent) event;
            if(type.is("Квадрат") || type.is("Новый квадрат")) {
                draw2DSoulsMarker(render2D.getMatrix(), render2D, aura.getTarget());
            }
        }
        if(event instanceof Render3DLastEvent) {
            Render3DLastEvent render3D = (Render3DLastEvent) event;
            if(type.is("Призраки") || type.is("Круг")) {
                draw3DSoulsMarker(render3D.getMatrix(), render3D, aura.getTarget());
            }
        }
    }

    private final Animation alpha = new DecelerateAnimation(600, 255.0D);
    private float alphaState = 0;

    public void draw2DSoulsMarker(MatrixStack stack, Render2DEvent e, Entity currentTarget) {
        if(currentTarget == null) return;
        if(currentTarget == mc.player) return;

        int clr = Nebulae.getHandler().themeManager.getColor(0);
        int color;
        if (redOnHurt.get()) {
            if (currentTarget instanceof LivingEntity && ((LivingEntity)currentTarget).hurtTime > 0) {
                color = interpolateColor(ColorUtils.rgba(220, 80, 80, 255), ColorUtils.rgba(220, 80, 80, 255),
                        Math.min((((LivingEntity)currentTarget).hurtTime/2f), 1));
            } else {
                color = Nebulae.getHandler().themeManager.getColor(0);
            }
        } else {
            color = interpolateColor(Nebulae.getHandler().themeManager.getColor(1), Nebulae.getHandler().themeManager.getColor(0),
                    Math.min((((LivingEntity)currentTarget).hurtTime/2f), 1));
        }

        Entity target = currentTarget;
        double sin = Math.sin((double)System.currentTimeMillis() / 1000.0D);
        float size;
        if (mc.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON) {
            size = 90.0F;
        } else {
            size = 60.0F;
        }

        Vector3d interpolated = MathUtil.interpolate(target.getPositionVec(), new Vector3d(target.lastTickPosX, target.lastTickPosY, target.lastTickPosZ), e.getPartialTicks());
        Vector2d ss = W2S.project(interpolated.x, interpolated.y + currentTarget.getHeight() / 2f, interpolated.z);
        if(ss == null){
            return;
        }

        Vector2f pos = new Vector2f((float)ss.x, (float)ss.y);
        GlStateManager.pushMatrix();
        GlStateManager.translatef(pos.x, pos.y, 0);
        GlStateManager.rotatef((float) sin * 120, 0, 0, 1);
        GlStateManager.translatef(-pos.x, -pos.y, 0);
        if (pos != null) {
            String texturePath = type.is("Квадрат") ? "night/image/target/Quad.png" : "night/image/target/Quad2.png";
            ClientHandler.drawImage(stack, new ResourceLocation(texturePath), pos.x - size / 2f, pos.y - size / 2f, size, size, ColorUtils.setAlpha(color, (int)alpha.getOutput()), 0);
        }
        GlStateManager.popMatrix();
    }

    public void draw3DSoulsMarker(MatrixStack stack, Render3DLastEvent e, Entity currentTarget) {
        if(currentTarget == null) return;
        if(currentTarget == mc.player) return;

        int clr = Nebulae.getHandler().themeManager.getColor(0);
        int color;
        if (redOnHurt.get()) {
            if (currentTarget instanceof LivingEntity && ((LivingEntity)currentTarget).hurtTime > 0) {
                color = interpolateColor(ColorUtils.rgba(220, 80, 80, 255), ColorUtils.rgba(220, 80, 80, 255),
                        Math.min((((LivingEntity)currentTarget).hurtTime/2f), 1));
            } else {
                color = Nebulae.getHandler().themeManager.getColor(0);
            }
        } else {
            color = interpolateColor(Nebulae.getHandler().themeManager.getColor(1), Nebulae.getHandler().themeManager.getColor(0),
                    Math.min((((LivingEntity)currentTarget).hurtTime/2f), 1));
        }

        Entity target = currentTarget;
        double distance;

        if(type.is("Призраки")) {
            stack.push();
            RenderSystem.pushMatrix();
            RenderSystem.disableLighting();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.shadeModel(7425);
            RenderSystem.disableCull();
            RenderSystem.disableAlphaTest();
            RenderSystem.blendFuncSeparate(770, 1, 0, 1);

            ActiveRenderInfo camera = mc.getRenderManager().info;
            stack.translate(-mc.getRenderManager().info.getProjectedView().getX(), -mc.getRenderManager().info.getProjectedView().getY(), -mc.getRenderManager().info.getProjectedView().getZ());

            Vector3d interpolated = MathUtil.interpolate(target.getPositionVec(), new Vector3d(target.lastTickPosX, target.lastTickPosY, target.lastTickPosZ), e.getPartialTicks());
            interpolated.y += 0.75D;

            stack.translate(interpolated.x + 0.20000000298023224D, interpolated.y + (double)(target.getHeight() / 4.0F), interpolated.z);
            mc.getTextureManager().bindTexture(new ResourceLocation("night/image/glow.png"));

            double radius = 0.699999988079071D;
            float speed = ghostsSpeed.get();
            float size = ghostsWidth.get();
            distance = 12.0D;
            int length = ghostsLength.get().intValue();
            float angleStep = ghostsAngle.get();

            int i;
            Quaternion r;
            double angle;
            double s;
            double c;
            byte alpha;
            for(i = 0; i < length; ++i) {
                r = camera.getRotation().copy();
                buffer.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
                angle = angleStep * ((double)(System.currentTimeMillis() - startTime) - (double)i * distance) / (double)speed;
                s = Math.sin((double)((float)angle)) * radius;
                c = Math.cos((double)((float)angle)) * radius;
                stack.translate(s, c, -c);
                stack.translate((double)(-size / 2.0F), (double)(-size / 2.0F), 0.0D);
                stack.rotate(r);
                stack.translate((double)(size / 2.0F), (double)(size / 2.0F), 0.0D);
                buffer.pos(stack.getLast().getMatrix(), 0.0F, -size, 0.0F).color(ColorUtils.setAlpha(color, (int)(alphaState*(i*10)))).tex(0.0F, 0.0F).endVertex();
                buffer.pos(stack.getLast().getMatrix(), -size, -size, 0.0F).color(ColorUtils.setAlpha(color, (int)(alphaState*(i*10)))).tex(0.0F, 1.0F).endVertex();
                buffer.pos(stack.getLast().getMatrix(), -size, 0.0F, 0.0F).color(ColorUtils.setAlpha(color, (int)(alphaState*(i*10)))).tex(1.0F, 1.0F).endVertex();
                buffer.pos(stack.getLast().getMatrix(), 0.0F, 0.0F, 0.0F).color(ColorUtils.setAlpha(color, (int)(alphaState*(i*10)))).tex(1.0F, 0.0F).endVertex();
                tessellator.draw();
                stack.translate((double)(-size / 2.0F), (double)(-size / 2.0F), 0.0D);
                r.conjugate();
                stack.rotate(r);
                stack.translate((double)(size / 2.0F), (double)(size / 2.0F), 0.0D);
                stack.translate(-s, -c, c);
            }

            for(i = 0; i < length; ++i) {
                r = camera.getRotation().copy();
                buffer.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
                angle = angleStep * ((double)(System.currentTimeMillis() - startTime) - (double)i * distance) / (double)speed;
                s = Math.sin((double)((float)angle)) * radius;
                c = Math.cos((double)((float)angle)) * radius;
                stack.translate(-s, s, -c);
                stack.translate((double)(-size / 2.0F), (double)(-size / 2.0F), 0.0D);
                stack.rotate(r);
                stack.translate((double)(size / 2.0F), (double)(size / 2.0F), 0.0D);
                buffer.pos(stack.getLast().getMatrix(), 0.0F, -size, 0.0F).color(ColorUtils.setAlpha(color, (int)(alphaState*(i*10)))).tex(0.0F, 0.0F).endVertex();
                buffer.pos(stack.getLast().getMatrix(), -size, -size, 0.0F).color(ColorUtils.setAlpha(color, (int)(alphaState*(i*10)))).tex(0.0F, 1.0F).endVertex();
                buffer.pos(stack.getLast().getMatrix(), -size, 0.0F, 0.0F).color(ColorUtils.setAlpha(color, (int)(alphaState*(i*10)))).tex(1.0F, 1.0F).endVertex();
                buffer.pos(stack.getLast().getMatrix(), 0.0F, 0.0F, 0.0F).color(ColorUtils.setAlpha(color, (int)(alphaState*(i*10)))).tex(1.0F, 0.0F).endVertex();
                tessellator.draw();
                stack.translate((double)(-size / 2.0F), (double)(-size / 2.0F), 0.0D);
                r.conjugate();
                stack.rotate(r);
                stack.translate((double)(size / 2.0F), (double)(size / 2.0F), 0.0D);
                stack.translate(s, -s, c);
            }

            for(i = 0; i < length; ++i) {
                r = camera.getRotation().copy();
                buffer.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
                angle = angleStep * ((double)(System.currentTimeMillis() - startTime) - (double)i * distance) / (double)speed;
                s = Math.sin((double)((float)angle)) * radius;
                c = Math.cos((double)((float)angle)) * radius;
                stack.translate(c, -s, -s);
                stack.translate((double)(-size / 2.0F), (double)(-size / 2.0F), 0.0D);
                stack.rotate(r);
                stack.translate((double)(size / 2.0F), (double)(size / 2.0F), 0.0D);
                buffer.pos(stack.getLast().getMatrix(), 0.0F, -size, 0.0F).color(ColorUtils.setAlpha(color, (int)(alphaState*(i*10)))).tex(0.0F, 0.0F).endVertex();
                buffer.pos(stack.getLast().getMatrix(), -size, -size, 0.0F).color(ColorUtils.setAlpha(color, (int)(alphaState*(i*10)))).tex(0.0F, 1.0F).endVertex();
                buffer.pos(stack.getLast().getMatrix(), -size, 0.0F, 0.0F).color(ColorUtils.setAlpha(color, (int)(alphaState*(i*10)))).tex(1.0F, 1.0F).endVertex();
                buffer.pos(stack.getLast().getMatrix(), 0.0F, 0.0F, 0.0F).color(ColorUtils.setAlpha(color, (int)(alphaState*(i*10)))).tex(1.0F, 0.0F).endVertex();
                tessellator.draw();
                stack.translate((double)(-size / 2.0F), (double)(-size / 2.0F), 0.0D);
                r.conjugate();
                stack.rotate(r);
                stack.translate((double)(size / 2.0F), (double)(size / 2.0F), 0.0D);
                stack.translate(-c, s, s);
            }

            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.enableAlphaTest();
            RenderSystem.depthMask(true);
            RenderSystem.popMatrix();
            stack.pop();
        } else if(type.is("Круг")) {
            int i;
            EntityRendererManager rm = mc.getRenderManager();

            stack.push();

            double x = target.lastTickPosX + (target.getPosX() - target.lastTickPosX) * (double) e.getPartialTicks() - rm.info.getProjectedView().getX();
            double y = target.lastTickPosY + (target.getPosY() - target.lastTickPosY) * (double) e.getPartialTicks() - rm.info.getProjectedView().getY();
            double z = target.lastTickPosZ + (target.getPosZ() - target.lastTickPosZ) * (double) e.getPartialTicks() - rm.info.getProjectedView().getZ();

            stack.translate(x, y, z);

            float height2 = target.getHeight();
            double duration = speedcircle.get();
            double elapsed = (double) System.currentTimeMillis() % duration;
            boolean side = elapsed > duration / 2.0;
            double progress = elapsed / (duration / 2.0);
            progress = side ? (progress -= 1.0) : 1.0 - progress;
            progress = progress < 0.5 ? 2.0 * progress * progress : 1.0 - Math.pow(-2.0 * progress + 2.0, 2.0) / 2.0;
            double eased = (double) (height2 / 2.0f) * (progress > 0.5 ? 1.0 - progress : progress) * (double) (side ? -1 : 1);

            GL11.glDepthMask(false);
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.disableAlphaTest();
            RenderSystem.shadeModel(7425);
            RenderSystem.disableCull();
            RenderSystem.lineWidth(1.5f);
            RenderSystem.color4f(-1.0f, -1.0f, -1.0f, -1.0f);

            buffer.begin(8, DefaultVertexFormats.POSITION_COLOR);
            float[] colors = null;
            for (i = 0; i <= 360; ++i) {
                colors = ColorUtils.rgba(color);
                buffer.pos(stack.getLast().getMatrix(),
                                (float)(Math.cos(Math.toRadians(i)) * (double) target.getWidth() * 0.8),
                                (float)((double) height2 * progress),
                                (float)(Math.sin(Math.toRadians(i)) * (double) target.getWidth() * 0.8))
                        .color(colors[0], colors[1], colors[2], 0.5f).endVertex();

                buffer.pos(stack.getLast().getMatrix(),
                                (float)(Math.cos(Math.toRadians(i)) * (double) target.getWidth() * 0.8),
                                (float)((double) height2 * progress + eased),
                                (float)(Math.sin(Math.toRadians(i)) * (double) target.getWidth() * 0.8))
                        .color(colors[0], colors[1], colors[2], 0.0f).endVertex();
            }
            buffer.finishDrawing();
            WorldVertexBufferUploader.draw(buffer);

            RenderSystem.color4f(-1.0f, -1.0f, -1.0f, -1.0f);
            buffer.begin(2, DefaultVertexFormats.POSITION_COLOR);
            for (i = 0; i <= 360; ++i) {
                buffer.pos(stack.getLast().getMatrix(),
                                (float)(Math.cos(Math.toRadians(i)) * (double) target.getWidth() * 0.8),
                                (float)((double) height2 * progress),
                                (float)(Math.sin(Math.toRadians(i)) * (double) target.getWidth() * 0.8))
                        .color(colors[0], colors[1], colors[2], 0.5f).endVertex();
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

            stack.pop();
        }
    }

    @Override
    protected void onDisable() {
        alphaState = 0;
        super.onDisable();
    }
}
