package beame.components.modules.combat;

import beame.Nebulae;
import beame.components.modules.combat.AuraHandlers.component.core.combat.RotationComponent;
import events.Event;
import events.impl.render.Render2DEvent;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.SliderSetting;
import beame.components.modules.combat.AuraHandlers.component.core.combat.Rotation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TridentAim extends Module {
// leaked by itskekoff; discord.gg/sk3d SBheBwu0
    private final RotationComponent rotationComponent = new RotationComponent();
    private boolean wasDrawn = false;

    public TridentAim() {
        super("TridentAim", Category.Combat);
        addSettings(attackRange, wallCheck, ignoreFriends, ignoreNaked);
    }

    @Override
    public void event(Event event) {
        if(event instanceof Render2DEvent) {
            if (mc.player == null || mc.world == null) return;

            boolean isDrawn = isTridentDrawn();

            if (isDrawn) {
                updateTarget();

                if (target != null && mc.player.getDistance(target) <= attackRange.get()) {
                    aimAtTarget(target);
                }
            } else if (wasDrawn) {
                RotationComponent.getInstance().stopRotation();
                target = null;
            }

            wasDrawn = isDrawn;
        }
    }

    private final SliderSetting attackRange = new SliderSetting("Дистанция", 20f, 10f, 50f, 2f);
    private final BooleanSetting wallCheck = new BooleanSetting("Игнорировать игроков за стеной", true);
    private final SliderSetting smoothness = new SliderSetting("Плавность", 0.5f, 0.1f, 1.0f, 0.1f);

    private final BooleanSetting ignoreNaked = new BooleanSetting("Игнорировать голых", true);
    private final BooleanSetting ignoreFriends = new BooleanSetting("Игнорировать друзей", true);

    private LivingEntity target;

    private boolean isTridentDrawn() {
        return mc.player.isHandActive() && mc.player.getHeldItem(mc.player.getActiveHand()).getItem() == Items.TRIDENT;
    }

    private void updateTarget() {
        List<LivingEntity> potentialTargets = new ArrayList<>();

        for (var entity : mc.world.getAllEntities()) {
            if (entity instanceof LivingEntity living && isValidTarget(living)) {
                potentialTargets.add(living);
            }
        }

        if (potentialTargets.isEmpty()) {
            target = null;
            return;
        }

        potentialTargets.sort(Comparator.comparingDouble(mc.player::getDistance));
        target = potentialTargets.get(0);
    }

    private boolean isValidTarget(LivingEntity entity) {
        if (entity == mc.player || !entity.isAlive()) return false;
        if (!(entity instanceof PlayerEntity)) return false;
        if(entity instanceof PlayerEntity) if (!entity.botEntity) return false;
        if (mc.player.getDistance(entity) > attackRange.get()) return false;
        if (wallCheck.get() && !canSeeEntity(entity)) return false;
        if (ignoreFriends.get() && entity instanceof PlayerEntity) {
            if (Nebulae.getHandler().friends.isFriend(((PlayerEntity) entity).getGameProfile().getName())) {
                return false;
            }
        }
        if (ignoreNaked.get() && entity instanceof PlayerEntity) {
            boolean hasArmor = false;
            for (ItemStack armor : entity.getArmorInventoryList()) {
                if (!armor.isEmpty()) {
                    hasArmor = true;
                    break;
                }
            }
            if (!hasArmor) return false;
        }

        return true;
    }

    private boolean canSeeEntity(LivingEntity entity) {
        Vector3d eyePos = mc.player.getEyePosition(1.0F);
        Vector3d targetCenter = entity.getPositionVec().add(0, entity.getHeight() * 0.5, 0);

        RayTraceContext context = new RayTraceContext(
                eyePos,
                targetCenter,
                RayTraceContext.BlockMode.COLLIDER,
                RayTraceContext.FluidMode.NONE,
                mc.player
        );

        BlockRayTraceResult result = mc.world.rayTraceBlocks(context);
        return result.getType() == RayTraceResult.Type.MISS;
    }

    private void aimAtTarget(LivingEntity target) {
        Vector3d playerPos = mc.player.getEyePosition(1.0F);
        Vector3d targetPos = target.getPositionVec().add(0, target.getHeight() * 0.4, 0);
        double deltaX = targetPos.x - playerPos.x;
        double deltaY = targetPos.y - playerPos.y;
        double deltaZ = targetPos.z - playerPos.z;

        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float targetYaw = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0F);
        float targetPitch = (float) -Math.toDegrees(Math.atan2(deltaY, horizontalDistance));

        double gravity = 0.05;
        double initialVelocity = 2.5;
        double optimalPitch = -Math.toDegrees(Math.atan(
                (initialVelocity * initialVelocity - Math.sqrt(
                        Math.pow(initialVelocity, 4) - gravity * (gravity * horizontalDistance * horizontalDistance + 2 * deltaY * initialVelocity * initialVelocity)
                )) / (gravity * horizontalDistance)
        ));

        rotationComponent.update(
                new Rotation(targetYaw, (float) optimalPitch),
                360, 360, 100, 5
        );
    }

    @Override
    public void onEnable() {
        super.onEnable();
        target = null;
        wasDrawn = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        target = null;
        wasDrawn = false;
        RotationComponent.getInstance().stopRotation();
    }
}
