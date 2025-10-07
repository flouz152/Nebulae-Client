package beame.components.modules.render;

import beame.Nebulae;
import beame.util.color.ColorUtils;
import beame.util.render.CustomFramebuffer2;
import beame.util.render.ProjectionUtil;
import beame.util.shaderExcellent.ShaderManager;
import beame.util.shaderExcellent.impl.entity.EntityShader;
import beame.util.shaderExcellent.impl.outline.EntityOutlineShader;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import events.Event;
import events.impl.render.Render2DEvent;
import events.impl.render.Render3DPosedEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import org.joml.Vector2f;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.RadioSetting;
import beame.setting.SettingList.SliderSetting;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShaderESP extends Module {
// leaked by itskekoff; discord.gg/sk3d RvyJJ62y

    private final CustomFramebuffer2 buffer = new CustomFramebuffer2(true);
    private final EntityShader bloom = new EntityShader();
    private final RadioSetting mode = new RadioSetting("Режим", "Внешний", "Внутренний", "Внешний и внутренний");
    private final SliderSetting iterations = new SliderSetting("Сила", 3, 1, 5, 1);
    private final SliderSetting divider = new SliderSetting("Сила размытия", 8, 1, 8, 0.1F);
    private final BooleanSetting layers = new BooleanSetting("Слои", true);
    private final BooleanSetting chams = new BooleanSetting("Чамсы", false);
    private final BooleanSetting outline = new BooleanSetting("Обводка", true).setVisible(() -> !chams.get());
    public final BooleanSetting redOnHurt = new BooleanSetting("Краснеть при ударе", false);
    public final BooleanSetting ignore = new BooleanSetting("Игнорировать голых", false);


    public ShaderESP() {
        super("ShaderESP", Category.Visuals, true, "Включает показ обводка игрока");
        addSettings(mode,  iterations, divider, layers, chams, outline,redOnHurt, ignore);
    }

    public void patch(PlayerEntity entity, Runnable runnable) {
        Vector3d interpolated = entity.getPositionVec().subtract(entity.getPositionVec(mc.getRenderPartialTicks()));

        AxisAlignedBB aabb = entity.getBoundingBox().offset(interpolated.inverse().add(interpolated.scale(mc.getRenderPartialTicks())));
        Vector2f center = ProjectionUtil.project2D(aabb.getCenter());

        if (center.x == Float.MAX_VALUE && center.y == Float.MAX_VALUE) {
            return;
        }

        float minX = center.x, minY = center.y, maxX = center.x, maxY = center.y;

        for (Vector3d corner : aabb.getCorners()) {
            Vector2f vec = ProjectionUtil.project2D(corner);
            if (vec.x == Float.MAX_VALUE && vec.y == Float.MAX_VALUE) continue;


            minX = Math.min(minX, vec.x);
            minY = Math.min(minY, vec.y);
            maxX = Math.max(maxX, vec.x);
            maxY = Math.max(maxY, vec.y);
        }

        float posX = minX, posY = minY, width = maxX - minX, height = maxY - minY;

        boolean isFriend = Nebulae.getHandler().friends.isFriend(entity.getGameProfile().getName());
        int baseColor = isFriend ? ColorUtils.green : Nebulae.getHandler().getThemeManager().getColor(1);
        float hurtPC = (float) Math.sin(entity.hurtTime * (18F * Math.PI / 180F));

        int color = baseColor;

        if (redOnHurt.get() && entity.hurtTime > 0) {
            color = ColorUtils.overCol(baseColor, ColorUtils.red, hurtPC);
        }

        if (entity instanceof PlayerEntity) {
            if (ignore().get() && isNaked(entity)) {
                return;
            }
        }

        ShaderManager gradient = ShaderManager.entityChamsShader;
        if (gradient == null) return;

        gradient.load();
        gradient.setUniformi("tex", 0);
        gradient.setUniformf("location", posX, posY);
        gradient.setUniformf("rectSize", width, height);
        gradient.setUniformf("color", ColorUtils.getRGBAf(color));

        runnable.run();

        gradient.unload();
    }


    @Override
    public void event(Event event) {
        if (event instanceof Render3DPosedEvent render3DPosedEvent) {
            MatrixStack stack = render3DPosedEvent.getMatrix();
            buffer.setup();

            mc.world.getPlayers().stream()
                    .filter(this::isValid)
                    .forEach(player -> {
                        patch(player, () -> {
                            EntityRendererManager rendererManager = mc.getRenderManager();
                            stack.push();
                            stack.translate(
                                    -rendererManager.renderPosX(),
                                    -rendererManager.renderPosY(),
                                    -rendererManager.renderPosZ()
                            );
                            RenderSystem.depthMask(true);
                            rendererManager.setRenderShadow(false);
                            rendererManager.setRenderName(false);

                            IRenderTypeBuffer.Impl bufferSource = mc.getRenderTypeBuffers().getBufferSource();
                            Vector3d pos = player.getPositionVec(render3DPosedEvent.getPartialTicks());
                            EntityRenderer<? super PlayerEntity> renderer = rendererManager.getRenderer(player);
                            boolean nameVisible = renderer.isRenderName();

                            if (nameVisible) renderer.setRenderName(false);
                            if (!layers.get()) renderer.setRenderLayers(false);

                            rendererManager.renderClearEntityStatic(
                                    player,
                                    pos.getX(), pos.getY(), pos.getZ(),
                                    player.rotationYaw,
                                    render3DPosedEvent.getPartialTicks(),
                                    stack,
                                    bufferSource,
                                    rendererManager.getPackedLight(player, render3DPosedEvent.getPartialTicks())
                            );

                            if (!layers.get()) renderer.setRenderLayers(true);
                            if (nameVisible) renderer.setRenderName(true);

                            bufferSource.finish();

                            rendererManager.setRenderName(true);
                            rendererManager.setRenderShadow(true);
                            RenderSystem.depthMask(false);
                            RenderSystem.enableDepthTest();
                            stack.pop();
                        });
                    });

            buffer.stop();
        }

        if (event instanceof Render2DEvent eventRender) {
            if (outline.get()) {
                EntityOutlineShader.draw(1, buffer.framebufferTexture);
            }
            bloom.render(eventRender.getMatrix(),
                    buffer.framebufferTexture,
                    iterations.get().intValue(),
                    1F,
                    4 + divider.get()
            );
            if (chams.get()) buffer.draw();
            buffer.framebufferClear();
            mc.getFramebuffer().bindFramebuffer(true);
        }
    }


    public boolean isNaked(LivingEntity entity) {
        return entity.getItemStackFromSlot(EquipmentSlotType.HEAD).isEmpty()
                && entity.getItemStackFromSlot(EquipmentSlotType.CHEST).isEmpty()
                && entity.getItemStackFromSlot(EquipmentSlotType.LEGS).isEmpty()
                && entity.getItemStackFromSlot(EquipmentSlotType.FEET).isEmpty();
    }


    private boolean isValid(final Entity entity) {
        if (!entity.isAlive() || entity.isGlowing()) {
            return false;
        }
        if (mc.renderViewEntity != null && entity == mc.renderViewEntity && mc.gameSettings.getPointOfView().firstPerson()) {
            return false;
        }
        return isInView(entity) && entity instanceof PlayerEntity;
    }

    public boolean isInView(Entity entity) {
        if (mc.getRenderViewEntity() == null || mc.worldRenderer.getClippinghelper() == null) {
            return false;
        }
        return mc.worldRenderer.getClippinghelper().isBoundingBoxInFrustum(entity.getBoundingBox()) || entity.ignoreFrustumCheck;
    }
}
