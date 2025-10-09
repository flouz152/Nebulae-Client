package beame.components.modules.render;

import beame.Nebulae;
import beame.util.color.ColorUtils;
import beame.util.fonts.Fonts;
import beame.util.render.ClientHandler;
import beame.util.render.ProjectionUtil;
import events.Event;
import events.impl.render.EventRender;
import events.impl.render.Render2DEvent;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.EnumSetting;

import static org.lwjgl.opengl.GL11.*;

public class Predictions extends Module {
// leaked by itskekoff; discord.gg/sk3d L7A2bXNE
    private static final Tessellator tessellator = Tessellator.getInstance();
    private static final BufferBuilder buffer = tessellator.getBuffer();

    private final EnumSetting projectiles = new EnumSetting("Снаряды",
            new BooleanSetting("Эндер-перл", true),
            new BooleanSetting("Стрела", true),
            new BooleanSetting("Трезубец", true));

    private final BooleanSetting showOwner = new BooleanSetting("Показывать владельца", true);

    public Predictions() {
        super("Predictions", Category.Visuals, true, "Траектория падения снарядов");
        addSettings(projectiles, showOwner);
    }

    @Override
    public void event(Event event) {
        if (event instanceof EventRender) {
            renderTrajectories3D((EventRender) event);
        } else if (event instanceof Render2DEvent) {
            renderTrajectories2D((Render2DEvent) event);
        }
    }

    private void renderTrajectories3D(EventRender e) {
        if (!e.isRender3D()) return;
        glPushMatrix();

        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);

        glEnable(GL_BLEND);
        glEnable(GL_LINE_SMOOTH);
        Vector3d renderOffset = mc.getRenderManager().info.getProjectedView();
        glTranslated(-renderOffset.x, -renderOffset.y, -renderOffset.z);
        glLineWidth(3);

        for (Entity entity : mc.world.getAllEntities()) {
            if (isValidProjectile(entity) && isMoving(entity) && !shouldReturnToThrower(entity)) {
                buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
                renderEntityTrajectory(entity);
                tessellator.draw();
            }
        }

        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);

        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);

        glPopMatrix();
    }

    private boolean isMoving(Entity entity) {
        if (entity instanceof ArrowEntity || entity instanceof TridentEntity) {
            Vector3d motion = entity.getMotion();
            double speed = motion.x * motion.x + motion.y * motion.y + motion.z * motion.z;
            return speed > 0.001;
        }
        return true;
    }

    private boolean isReallyMoving(Entity entity) {
        if (entity instanceof EnderPearlEntity) {
            return true;
        }

        Vector3d motion = entity.getMotion();
        double speed = Math.abs(motion.x) + Math.abs(motion.y) + Math.abs(motion.z);

        return speed > 0.01;
    }

    private void renderEntityTrajectory(Entity entity) {
        int color = Nebulae.getHandler().themeManager.getColor(0);
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;

        Vector3d motion = entity.getMotion();
        Vector3d pos = entity.getPositionVec();
        Vector3d prevPos;

        for (int i = 0; i < 150; i++) {
            prevPos = pos;
            pos = pos.add(motion);
            motion = getNextMotion(entity, motion);

            buffer.pos((float) prevPos.x, (float) prevPos.y, (float) prevPos.z).color(red, green, blue, 1.0f).endVertex();

            RayTraceContext rayTraceContext = new RayTraceContext(
                    prevPos,
                    pos,
                    RayTraceContext.BlockMode.COLLIDER,
                    RayTraceContext.FluidMode.NONE,
                    entity
            );

            BlockRayTraceResult blockHitResult = mc.world.rayTraceBlocks(rayTraceContext);

            boolean isLast = blockHitResult.getType() == RayTraceResult.Type.BLOCK;

            if (isLast) {
                pos = blockHitResult.getHitVec();
            }

            buffer.pos((float) pos.x, (float) pos.y, (float) pos.z).color(red, green, blue, 1.0f).endVertex();

            if (isLast || pos.y < 0) break;
        }
    }

    private void renderTrajectories2D(Render2DEvent e) {
        for (Entity entity : mc.world.getAllEntities()) {
            if (isValidProjectile(entity) && isMoving(entity) && !shouldReturnToThrower(entity)) {
                if (hasChangedPosition(entity)) {
                    renderProjectileInfo(entity);
                }
            }
        }
    }

    public boolean shouldReturnToThrower(Entity entity) {
        if (!(entity instanceof TridentEntity)) return false;
        TridentEntity trident = (TridentEntity) entity;
        return trident.func_234616_v_() != null && trident.dealtDamage;
    }

    private void renderProjectileInfo(Entity entity) {
        Vector3d entityPosition = entity.getPositionVec();
        Vector3d entityMotion = entity.getMotion();
        Vector3d lastPosition = entityPosition;
        float ticks = 0;

        for (int i = 0; i <= 150; i++) {
            lastPosition = entityPosition;
            entityPosition = entityPosition.add(entityMotion);
            entityMotion = getNextMotion(entity, entityMotion);

            if (shouldEntityHit(entityPosition.add(0, 1, 0), lastPosition.add(0, 1, 0)) || entityPosition.y <= 0) {
                break;
            }

            ticks++;
        }

        double timeInSeconds = (ticks * 50) / 1000.0;

        Vector2f p = ProjectionUtil.project2D(entityPosition);
        if (p != null && p.x != Float.MAX_VALUE) {
            p.y -= 30;
            GL11.glPushMatrix();
            GL11.glTranslatef(p.x, p.y + 3, 0);

            double seconds = Math.round(timeInSeconds * 10) / 10.0;

            String ownerText = "";
            if (showOwner.get()) {
                Entity owner = null;

                if (entity instanceof ProjectileEntity) {
                    owner = ((ProjectileEntity) entity).getShooter();
                } else if (entity instanceof EnderPearlEntity) {
                    owner = ((EnderPearlEntity) entity).getShooter();
                }

                if (owner != null) {
                    ownerText = " | " + owner.getName().getString();
                }
            }

            String projectileType = "";
            if (entity instanceof EnderPearlEntity) {
                projectileType = "Перл";
            } else if (entity instanceof ArrowEntity) {
                projectileType = "Стрела";
            } else if (entity instanceof TridentEntity) {
                projectileType = "Трезубец";
            }

            String displayText = projectileType + " " + seconds + " сек" + ownerText;

            float wid = Fonts.SF_BOLD.get(14).getWidth(displayText) + 10;
            ClientHandler.drawRound(-wid / 2f, -4.5f, wid, 12, 3, ColorUtils.rgba(0, 0, 0, 150));
            Fonts.SF_BOLD.get(14).drawCenteredString(displayText, 0, 0, -1);

            GL11.glPopMatrix();
        }
    }

    public static Vector3d updatePearlMotion(Entity entity, Vector3d originalPearlMotion, Vector3d pearlPosition) {
        Vector3d pearlMotion = originalPearlMotion;

        if ((entity.isInWater() || mc.world.getBlockState(new BlockPos(pearlPosition)).getBlock() == Blocks.WATER) && !(entity instanceof TridentEntity)) {
            float scale = entity instanceof EnderPearlEntity ? 0.8f : 0.6f;
            pearlMotion = pearlMotion.scale(scale);
        } else {
            pearlMotion = pearlMotion.scale(0.99f);
        }

        if (!entity.hasNoGravity())
            pearlMotion = pearlMotion.subtract(0, entity instanceof EnderPearlEntity ? 0.03 : 0.05, 0);

        return pearlMotion;
    }

    public static boolean shouldEntityHit(Vector3d pearlPosition, Vector3d lastPosition) {
        final RayTraceContext rayTraceContext = new RayTraceContext(
                lastPosition,
                pearlPosition,
                RayTraceContext.BlockMode.COLLIDER,
                RayTraceContext.FluidMode.NONE,
                mc.player
        );
        final BlockRayTraceResult blockHitResult = mc.world.rayTraceBlocks(rayTraceContext);

        return blockHitResult.getType() == RayTraceResult.Type.BLOCK;
    }

    private Vector3d getNextMotion(Entity entity, Vector3d motion) {
        if (entity.isInWater()) {
            float scale = entity instanceof TridentEntity ? 0.99f : entity instanceof EnderPearlEntity ? 0.8f : 0.6f;
            motion = motion.scale(scale);
        } else {
            motion = motion.scale(0.99f);
        }

        if (!entity.hasNoGravity()) {
            float gravity = entity instanceof EnderPearlEntity ? 0.03f : 0.05f;
            motion = motion.subtract(0, gravity, 0);
        }

        return motion;
    }

    private boolean isValidProjectile(Entity entity) {
        return (entity instanceof EnderPearlEntity && projectiles.get("Эндер-перл").get()) ||
                (entity instanceof ArrowEntity && projectiles.get("Стрела").get()) ||
                (entity instanceof TridentEntity && projectiles.get("Трезубец").get());
    }

    private boolean hasChangedPosition(Entity entity) {
        return entity.prevPosY != entity.getPosY() || entity.prevPosX != entity.getPosX() || entity.prevPosZ != entity.getPosZ();
    }
}
