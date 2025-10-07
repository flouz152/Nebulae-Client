package beame.components.modules.combat;

import beame.Essence;
import beame.components.modules.combat.AuraHandlers.component.core.combat.Rotation;
import beame.components.modules.combat.AuraHandlers.component.core.combat.RotationComponent;
import beame.util.math.MathUtil;
import events.Event;
import events.impl.render.EventRender;
import events.impl.player.EventUpdate;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.EnumSetting;
import beame.setting.SettingList.SliderSetting;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ProjectileHelper extends Module {
// leaked by itskekoff; discord.gg/sk3d eKMM74Dz
    private Vector3d hitPos = null;
    private Vector3d renderPos = null;
    private Vector3d lastHitPos = null;
    private List<Vector3d> arrowHitPositions = new ArrayList<>();
    private Entity target = null;
    private Entity lastTarget = null;
    private boolean willHit = false;
    private boolean wasCharged = false;
    private boolean waitingForCharge = false;
    private Direction hitSide = null;
    private long targetAcquiredTime = 0;
    private boolean wasDrawn = false;
    private LivingEntity tridentTarget;
    private LivingEntity bowTarget;
    private final RotationComponent rotationComponent = new RotationComponent();

    private static final Color MISS_COLOR = new Color(0, 255, 0, 150);
    private static final Color HIT_COLOR = new Color(255, 0, 0, 150);
    private static final double STEP = 0.1;
    private static final double HITBOX_SIZE = 0.15;
    private static final float INTERPOLATION_SPEED = 0.1f;
    private static final long SHOOT_DELAY = 1;
    private static final float ARROW_SPREAD_ANGLE = 9.0f;
    private static final double CROSSHAIR_SIZE = 0.25;

    private final EnumSetting gun = new EnumSetting("Учитывать",
            new BooleanSetting("Арбалет", true),
            new BooleanSetting("Трезубец", true),
            new BooleanSetting("Зелья", true),
            new BooleanSetting("Лук", true));
    private final BooleanSetting autoShoot = new BooleanSetting("Авто-выстрел", true).setVisible(() -> gun.get("Арбалет").get() || gun.get("Трезубец").get());
    private final BooleanSetting drawPath = new BooleanSetting("Прицел", true).setVisible(() -> gun.get("Арбалет").get() || gun.get("Трезубец").get() || gun.get("Зелья").get());
    private final BooleanSetting ignoreFriends = new BooleanSetting("Игнорировать друзей", true);


    private final BooleanSetting tridentAim = new BooleanSetting("Аим на трезубец", false).setVisible(() -> gun.get("Трезубец").get());
    private final BooleanSetting bowAim = new BooleanSetting("Аим на лук", false).setVisible(() -> gun.get("Лук").get());
    private final float attackRange = 50f;
    private final BooleanSetting wallCheck = new BooleanSetting("Игнорировать игроков за стеной", true).setVisible(() -> tridentAim.get() && gun.get("Трезубец").get());
    private final BooleanSetting ignoreNaked = new BooleanSetting("Игнорировать голых", true).setVisible(() -> tridentAim.get() && gun.get("Трезубец").get());
    private final BooleanSetting onlyAuraTarget = new BooleanSetting("Только за таргетом", false).setVisible(() -> tridentAim.get() && gun.get("Трезубец").get());
    private final SliderSetting targetMemoryTime = new SliderSetting("Время памяти таргета (сек)", 5, 1, 20, 1).setVisible(() -> tridentAim.get() && onlyAuraTarget.get() && gun.get("Трезубец").get());
    public final SliderSetting predict = new SliderSetting("Сила предикта", 1.03f, 1f, 3, 0.01f).setVisible(() -> gun.get("Трезубец").get());
    private List<Vector3d> trajectoryPoints = new ArrayList<>();

    public ProjectileHelper() {
        super("ProjectileHelper", Category.Combat, true, "Помощник в наводке метательных инструментов");
        addSettings(gun, autoShoot, drawPath, ignoreFriends, tridentAim, bowAim, wallCheck, ignoreNaked, predict);
    }

    private boolean isFriend(Entity entity) {
        if (entity instanceof PlayerEntity && ignoreFriends.get()) {
            String playerName = ((PlayerEntity) entity).getGameProfile().getName();
            return beame.Essence.getHandler().friends.isFriend(playerName);
        }
        return false;
    }

    @Override
    public void event(Event event) {
        if (event instanceof EventUpdate) {
            if (mc.player == null) return;

            boolean isCrossbow = mc.player.getHeldItemMainhand().getItem() == Items.CROSSBOW &&
                    gun.get("Арбалет").get();
            boolean isTrident = mc.player.getHeldItemMainhand().getItem() == Items.TRIDENT &&
                    gun.get("Трезубец").get();
            boolean isBow = mc.player.getHeldItemMainhand().getItem() == Items.BOW &&
                    gun.get("Лук").get();
            boolean isPotion = (mc.player.getHeldItemMainhand().getItem() == Items.SPLASH_POTION ||
                    mc.player.getHeldItemMainhand().getItem() == Items.LINGERING_POTION) &&
                    gun.get("Зелья").get();

            if (bowAim.get() && (!isBow || !mc.player.isHandActive())) {
                if (wasDrawn) {
                    RotationComponent.getInstance().stopRotation();
                    bowTarget = null;
                    wasDrawn = false;
                }
            }
            if (tridentAim.get() && (!isTrident || !mc.player.isHandActive())) {
                if (wasDrawn) {
                    RotationComponent.getInstance().stopRotation();
                    tridentTarget = null;
                    wasDrawn = false;
                }
            }

            if (!isCrossbow && !isTrident && !isBow && !isPotion) {
                wasCharged = false;
                waitingForCharge = false;
                hitPos = null;
                target = null;
                willHit = false;
                return;
            }
            if (tridentAim.get() && isTrident) {
                handleTridentAim(event);
                calculateTrajectory();

                if (autoShoot.get()) {
                    boolean isCharging = mc.player.isHandActive();
                    boolean isFullyCharged = isCharging && mc.player.getItemInUseMaxCount() >= 10;
                    handleTridentLogic(isCharging, isFullyCharged);
                }
            } else if (bowAim.get() && isBow && mc.player.isHandActive()) {
                handleBowAim(event);
                calculateTrajectory();
            } else {
                boolean isCharging = mc.player.isHandActive();
                boolean isFullyCharged = isCrossbow ?
                        CrossbowItem.isCharged(mc.player.getHeldItemMainhand()) :
                        (isCharging && mc.player.getItemInUseMaxCount() >= 10);

                calculateTrajectory();

                if (autoShoot.get()) {
                    if (isCrossbow) {
                        handleCrossbowLogic(isFullyCharged, isCharging);
                    } else if (isTrident) {
                        handleTridentLogic(isCharging, isFullyCharged);
                    }
                }
            }
        }
        else if (event instanceof EventRender) {
           {
                if (!drawPath.get() || hitPos == null || mc.player == null) return;

                boolean validItem = (mc.player.getHeldItemMainhand().getItem() == Items.CROSSBOW &&
                        gun.get("Арбалет").get()) ||
                        (mc.player.getHeldItemMainhand().getItem() == Items.TRIDENT &&
                                gun.get("Трезубец").get()) ||
                        ((mc.player.getHeldItemMainhand().getItem() == Items.SPLASH_POTION ||
                                mc.player.getHeldItemMainhand().getItem() == Items.LINGERING_POTION) &&
                                gun.get("Зелья").get());

                if (!validItem) return;

                try {
                    renderTrajectory();
                } catch (Exception ex) {

                }
            }
        }
    }
    private void handleTridentAim(Event event) {
        boolean isDrawn = isTridentDrawn();

        if (isDrawn) {
            if (onlyAuraTarget.get()) {
                updateTargetFromAura();
            } else {
                updateTridentTarget();
            }

            if (tridentTarget != null && mc.player.getDistance(tridentTarget) <= attackRange) {
                aimAtTridentTarget(tridentTarget);
            }
        } else if (wasDrawn) {
            RotationComponent.getInstance().stopRotation();
            tridentTarget = null;
        }

        wasDrawn = isDrawn;
    }

    private boolean isTridentDrawn() {
        return mc.player.isHandActive() && mc.player.getHeldItem(mc.player.getActiveHand()).getItem() == Items.TRIDENT;
    }

    private void updateTargetFromAura() {
        Aura aura = (Aura) Essence.getHandler().getModuleList().aura;
        LivingEntity currentTarget = null;
        if (aura != null && aura.isState()) {
            currentTarget = aura.getTarget();
            if (currentTarget != null) {
                tridentTarget = currentTarget;
            }
        }
        if (tridentTarget != null && tridentTarget.isAlive() && isValidTridentTarget(tridentTarget) &&
                mc.player.getDistance(tridentTarget) <= attackRange) {
        } else if (currentTarget != null && isValidTridentTarget(currentTarget) &&
                mc.player.getDistance(currentTarget) <= attackRange) {
            tridentTarget = currentTarget;
        } else {
            tridentTarget = null;
        }
    }

    private void updateTridentTarget() {
        List<LivingEntity> potentialTargets = new ArrayList<>();

        for (var entity : mc.world.getAllEntities()) {
            if (entity instanceof LivingEntity living && isValidTridentTarget(living)) {
                potentialTargets.add(living);
            }
        }

        if (potentialTargets.isEmpty()) {
            tridentTarget = null;
            return;
        }

        potentialTargets.sort(Comparator.comparingDouble(mc.player::getDistance));
        tridentTarget = potentialTargets.get(0);
    }

    private boolean isValidTridentTarget(LivingEntity entity) {
        if (entity == mc.player || !entity.isAlive()) return false;
        if (!(entity instanceof PlayerEntity)) return false;
        if(entity instanceof PlayerEntity) if (!entity.botEntity) return false;
        if (mc.player.getDistance(entity) > attackRange) return false;
        if (wallCheck.get() && !canSeeEntity(entity)) return false;
        if (ignoreFriends.get() && entity instanceof PlayerEntity) {
            if (Essence.getHandler().friends.isFriend(((PlayerEntity) entity).getGameProfile().getName())) {
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

    private void aimAtTridentTarget(LivingEntity target) {
        Vector3d playerPos = mc.player.getEyePosition(1.0F);

        Vector3d targetPos = target.getPositionVec().add(0, target.getHeight() * 0.4, 0);

        double velocityX = target.getPosX() - target.prevPosX;
        double velocityY = target.getPosY() - target.prevPosY;
        double velocityZ = target.getPosZ() - target.prevPosZ;

        double gravity = 0.03;
        double initialVelocity = 2.5;

        double deltaX = targetPos.x - playerPos.x;
        double deltaY = targetPos.y - playerPos.y;
        double deltaZ = targetPos.z - playerPos.z;
        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        double flightTime = horizontalDistance / (initialVelocity * 0.8);

        Vector3d predictedPos = new Vector3d(
                targetPos.x + velocityX * flightTime * predict.get(),
                targetPos.y + velocityY * Math.min(flightTime * 1.5, 0.5),
                targetPos.z + velocityZ * flightTime * predict.get()
        );

        double predDeltaX = predictedPos.x - playerPos.x;
        double predDeltaY = predictedPos.y - playerPos.y;
        double predDeltaZ = predictedPos.z - playerPos.z;
        double predHorizontalDistance = Math.sqrt(predDeltaX * predDeltaX + predDeltaZ * predDeltaZ);

        float targetYaw = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(predDeltaZ, predDeltaX)) - 90.0F);

        double optimalPitch = -Math.toDegrees(Math.atan(
                (initialVelocity * initialVelocity - Math.sqrt(
                        Math.pow(initialVelocity, 4) - gravity * (gravity * predHorizontalDistance * predHorizontalDistance + 2 * predDeltaY * initialVelocity * initialVelocity)
                )) / (gravity * predHorizontalDistance)
        ));

        if (Double.isNaN(optimalPitch) || Double.isInfinite(optimalPitch)) {
            float defaultPitch = (float) -Math.toDegrees(Math.atan2(predDeltaY, predHorizontalDistance));
            optimalPitch = defaultPitch;
        }
        float randomtr = MathUtil.random(40, 360f);
        rotationComponent.update(
                new Rotation(targetYaw, (float) optimalPitch),
                360, randomtr, 1, 5
        );
    }

    private void handleCrossbowLogic(boolean isFullyCharged, boolean isCharging) {
        if (target != null && isFriend(target)) {
            return;
        }

        if (isFullyCharged && willHit && target != null) {
            if (target != lastTarget) {
                targetAcquiredTime = System.currentTimeMillis();
                lastTarget = target;
            }

            if (!wasCharged && System.currentTimeMillis() - targetAcquiredTime >= SHOOT_DELAY) {
                wasCharged = true;
                mc.playerController.processRightClick(mc.player, mc.world, net.minecraft.util.Hand.MAIN_HAND);
            }
        } else {
            wasCharged = false;
            lastTarget = null;
        }
    }

    private void handleTridentLogic(boolean isCharging, boolean isFullyCharged) {
        if (target != null && isFriend(target)) {
            return;
        }

        if (isCharging && !waitingForCharge) {
            waitingForCharge = true;
        } else if (isFullyCharged && willHit && target != null) {
            if (target != lastTarget) {
                targetAcquiredTime = System.currentTimeMillis();
                lastTarget = target;
            }

            if (System.currentTimeMillis() - targetAcquiredTime >= SHOOT_DELAY) {
                mc.playerController.onStoppedUsingItem(mc.player);
                waitingForCharge = false;
                wasCharged = false;
                lastTarget = null;
            }
        } else if (!willHit || target == null) {
            lastTarget = null;
        }
    }

    private void handleBowAim(Event event) {
        boolean isDrawn = isBowDrawn();

        if (isDrawn) {
            updateBowTarget();
            if (bowTarget != null && mc.player.getDistance(bowTarget) <= attackRange) {
                aimAtBowTarget(bowTarget);
            }
        } else if (wasDrawn) {
            RotationComponent.getInstance().stopRotation();
            bowTarget = null;
        }
        wasDrawn = isDrawn;
    }

    private boolean isBowDrawn() {
        return mc.player.isHandActive() && mc.player.getHeldItem(mc.player.getActiveHand()).getItem() == Items.BOW;
    }

    private void updateBowTarget() {
        List<LivingEntity> potentialTargets = new ArrayList<>();
        for (var entity : mc.world.getAllEntities()) {
            if (entity instanceof LivingEntity living && isValidBowTarget(living)) {
                potentialTargets.add(living);
            }
        }
        if (potentialTargets.isEmpty()) {
            bowTarget = null;
            return;
        }
        potentialTargets.sort(Comparator.comparingDouble(mc.player::getDistance));
        bowTarget = potentialTargets.get(0);
    }

    private boolean isValidBowTarget(LivingEntity entity) {
        if (entity == mc.player || !entity.isAlive()) return false;
        if (!(entity instanceof PlayerEntity)) return false;
        if (mc.player.getDistance(entity) > attackRange) return false;
        if (wallCheck.get() && !canSeeEntity(entity)) return false;
        if (ignoreFriends.get() && entity instanceof PlayerEntity) {
            if (Essence.getHandler().friends.isFriend(((PlayerEntity) entity).getGameProfile().getName())) {
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

    private void aimAtBowTarget(LivingEntity target) {
        Vector3d playerPos = mc.player.getEyePosition(1.0F);
        Vector3d targetPos = target.getPositionVec().add(0, target.getHeight() * 0.4, 0);

        double velocityX = target.getPosX() - target.prevPosX;
        double velocityY = target.getPosY() - target.prevPosY;
        double velocityZ = target.getPosZ() - target.prevPosZ;

        double gravity = 0.05;
        double initialVelocity = 3.0;

        double deltaX = targetPos.x - playerPos.x;
        double deltaY = targetPos.y - playerPos.y;
        double deltaZ = targetPos.z - playerPos.z;
        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        double flightTime = horizontalDistance / (initialVelocity * 0.8);

        Vector3d predictedPos = new Vector3d(
                targetPos.x + velocityX * flightTime * predict.get(),
                targetPos.y + velocityY * Math.min(flightTime * 1.5, 0.5),
                targetPos.z + velocityZ * flightTime * predict.get()
        );

        double predDeltaX = predictedPos.x - playerPos.x;
        double predDeltaY = predictedPos.y - playerPos.y;
        double predDeltaZ = predictedPos.z - playerPos.z;
        double predHorizontalDistance = Math.sqrt(predDeltaX * predDeltaX + predDeltaZ * predDeltaZ);

        float targetYaw = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(predDeltaZ, predDeltaX)) - 90.0F);

        double optimalPitch = -Math.toDegrees(Math.atan(
                (initialVelocity * initialVelocity - Math.sqrt(
                        Math.pow(initialVelocity, 4) - gravity * (gravity * predHorizontalDistance * predHorizontalDistance + 2 * predDeltaY * initialVelocity * initialVelocity)
                )) / (gravity * predHorizontalDistance)
        ));

        if (Double.isNaN(optimalPitch) || Double.isInfinite(optimalPitch)) {
            float defaultPitch = (float) -Math.toDegrees(Math.atan2(predDeltaY, predHorizontalDistance));
            optimalPitch = defaultPitch;
        }
        float randomtr = MathUtil.random(40, 360f);
        rotationComponent.update(
                new Rotation(targetYaw, (float) optimalPitch),
                360, randomtr, 1, 5
        );
    }

    @Override
    public void onDisable() {
        super.onDisable();
        wasCharged = false;
        waitingForCharge = false;
        lastTarget = null;
        tridentTarget = null;
        wasDrawn = false;
        RotationComponent.getInstance().stopRotation();
    }

    private void calculateTrajectory() {
        Vector3d oldHitPos = hitPos;
        hitPos = null;
        willHit = false;
        target = null;
        hitSide = null;
        arrowHitPositions.clear();

        Vector3d pos = new Vector3d(mc.player.getPosX(), mc.player.getPosY() + mc.player.getEyeHeight(), mc.player.getPosZ());

        float pitch = mc.player.rotationPitch;
        float yaw = mc.player.rotationYaw;

        boolean isCrossbow = mc.player.getHeldItemMainhand().getItem() == Items.CROSSBOW;
        boolean isTrident = mc.player.getHeldItemMainhand().getItem() == Items.TRIDENT;
        boolean isBow = mc.player.getHeldItemMainhand().getItem() == Items.BOW;

        if (isCrossbow && CrossbowItem.isCharged(mc.player.getHeldItemMainhand())) {
            float yawRad = yaw * 0.017453292F;
            float pitchRad = pitch * 0.017453292F;


            calculateArrowTrajectory(pos, pitch, yaw, oldHitPos);


            calculateArrowTrajectory(pos, pitch, yaw - ARROW_SPREAD_ANGLE, oldHitPos);


            calculateArrowTrajectory(pos, pitch, yaw + ARROW_SPREAD_ANGLE, oldHitPos);
        } else if (isBow && mc.player.isHandActive()) {
            float yawRad = yaw * 0.017453292F;
            float pitchRad = pitch * 0.017453292F;

            calculateArrowTrajectory(pos, pitch, yaw, oldHitPos);
        }
        else {
            calculateSingleProjectileTrajectory(pos, pitch, yaw, oldHitPos);
        }
    }

    private void calculateArrowTrajectory(Vector3d pos, float pitch, float yaw, Vector3d oldHitPos) {
        double velocityMultiplier = 3.15;
        double gravity = 0.05;
        double drag = 0.99;
        double stepSize = 0.1;

        float yawRad = yaw * 0.017453292F;
        float pitchRad = pitch * 0.017453292F;


        double motionX = -MathHelper.sin(yawRad) * MathHelper.cos(pitchRad);
        double motionY = -MathHelper.sin(pitchRad);
        double motionZ = MathHelper.cos(yawRad) * MathHelper.cos(pitchRad);


        double length = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
        motionX = motionX / length * velocityMultiplier;
        motionY = motionY / length * velocityMultiplier;
        motionZ = motionZ / length * velocityMultiplier;

        Vector3d motion = new Vector3d(motionX, motionY, motionZ);
        Vector3d currentPos = pos;
        Vector3d lastPos = pos;

        int maxSteps = 1000;
        double currentDrag = 1.0;

        for (int i = 0; i < maxSteps; i++) {
            currentDrag *= drag;

            Vector3d nextPos = new Vector3d(
                    currentPos.x + motion.x * currentDrag * stepSize,
                    currentPos.y + motion.y * currentDrag * stepSize,
                    currentPos.z + motion.z * currentDrag * stepSize
            );

            AxisAlignedBB boundingBox = new AxisAlignedBB(
                    nextPos.x - HITBOX_SIZE, nextPos.y - HITBOX_SIZE, nextPos.z - HITBOX_SIZE,
                    nextPos.x + HITBOX_SIZE, nextPos.y + HITBOX_SIZE, nextPos.z + HITBOX_SIZE
            );

            for (Entity entity : mc.world.getAllEntities()) {
                if (entity instanceof PlayerEntity && entity != mc.player) {
                    if (entity.getBoundingBox().intersects(boundingBox)) {
                        willHit = true;
                        target = entity;
                        hitPos = nextPos;
                        arrowHitPositions.add(nextPos);
                        lastHitPos = oldHitPos;
                        return;
                    }
                }
            }

            BlockPos blockPos = new BlockPos(nextPos);
            if (!mc.world.isAirBlock(blockPos)) {
                BlockState state = mc.world.getBlockState(blockPos);

                boolean isPassable = state.getMaterial().isReplaceable() ||
                        state.getMaterial().isLiquid() ||
                        state.getBlock() instanceof net.minecraft.block.CarpetBlock ||
                        state.getBlock() instanceof net.minecraft.block.FlowerBlock ||
                        state.getBlock() instanceof net.minecraft.block.TallGrassBlock ||
                        state.getBlock() instanceof net.minecraft.block.GrassBlock ||
                        state.getBlock() instanceof net.minecraft.block.BushBlock;

                if (!isPassable) {
                    RayTraceResult rayTrace = mc.world.rayTraceBlocks(new RayTraceContext(
                            lastPos,
                            nextPos,
                            RayTraceContext.BlockMode.OUTLINE,
                            RayTraceContext.FluidMode.NONE,
                            mc.player
                    ));

                    if (rayTrace != null && rayTrace.getType() == RayTraceResult.Type.BLOCK) {
                        BlockRayTraceResult blockRayTrace = (BlockRayTraceResult) rayTrace;
                        BlockState hitState = mc.world.getBlockState(blockRayTrace.getPos());

                        if (!hitState.getMaterial().isReplaceable() &&
                                !hitState.getMaterial().isLiquid() &&
                                !(hitState.getBlock() instanceof net.minecraft.block.CarpetBlock) &&
                                !(hitState.getBlock() instanceof net.minecraft.block.FlowerBlock) &&
                                !(hitState.getBlock() instanceof net.minecraft.block.TallGrassBlock) &&
                                !(hitState.getBlock() instanceof net.minecraft.block.GrassBlock) &&
                                !(hitState.getBlock() instanceof net.minecraft.block.BushBlock)) {

                            arrowHitPositions.add(nextPos);
                            if (hitPos == null) {
                                hitPos = nextPos;
                                hitSide = blockRayTrace.getFace();
                            }
                            return;
                        }
                    }
                }
            }

            lastPos = currentPos;
            currentPos = nextPos;
            motion = new Vector3d(
                    motion.x,
                    motion.y - gravity * stepSize,
                    motion.z
            );

            if (currentPos.y < -64 || currentPos.distanceTo(pos) > 300) {
                arrowHitPositions.add(nextPos);
                if (hitPos == null) {
                    hitPos = nextPos;
                }
                break;
            }
        }
    }

    private void calculateSingleProjectileTrajectory(Vector3d pos, float pitch, float yaw, Vector3d oldHitPos) {
        double velocityMultiplier;
        double gravity;
        double drag;
        double stepSize;

        boolean isTrident = mc.player.getHeldItemMainhand().getItem() == Items.TRIDENT;
        boolean isPotion = mc.player.getHeldItemMainhand().getItem() == Items.SPLASH_POTION ||
                mc.player.getHeldItemMainhand().getItem() == Items.LINGERING_POTION;

        if (mc.player.getHeldItemMainhand().getItem() == Items.CROSSBOW) {
            velocityMultiplier = 3.15;
            gravity = 0.05;
            drag = 0.99;
            stepSize = 0.1;
        } else if (isTrident) {
            velocityMultiplier = 3.5;
            gravity = 0.03;
            drag = 0.995;
            stepSize = 0.05;
        } else {
            velocityMultiplier = 0.5;
            gravity = 0.05;
            drag = 0.99;
            stepSize = 0.1;
        }

        double motionX = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
        double motionY = -MathHelper.sin(pitch * 0.017453292F);
        double motionZ = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);

        double length = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
        motionX = motionX / length * velocityMultiplier;
        motionY = motionY / length * velocityMultiplier;
        motionZ = motionZ / length * velocityMultiplier;

        Vector3d motion = new Vector3d(motionX, motionY, motionZ);
        Vector3d currentPos = pos;
        Vector3d lastPos = pos;

        int maxSteps = 10000;
        double currentDrag = 1.0;

        trajectoryPoints.clear();
        trajectoryPoints.add(pos);

        boolean hasHitEntity = false;
        boolean hasHitGround = false;

        for (int i = 0; i < maxSteps; i++) {
            currentDrag *= drag;

            Vector3d nextPos = new Vector3d(
                    currentPos.x + motion.x * currentDrag * stepSize,
                    currentPos.y + motion.y * currentDrag * stepSize,
                    currentPos.z + motion.z * currentDrag * stepSize
            );

            if (i % 5 == 0) {
                trajectoryPoints.add(nextPos);
            }

            AxisAlignedBB boundingBox = new AxisAlignedBB(
                    nextPos.x - HITBOX_SIZE, nextPos.y - HITBOX_SIZE, nextPos.z - HITBOX_SIZE,
                    nextPos.x + HITBOX_SIZE, nextPos.y + HITBOX_SIZE, nextPos.z + HITBOX_SIZE
            );

            for (Entity entity : mc.world.getAllEntities()) {
                if (entity instanceof PlayerEntity && entity != mc.player) {
                    if (entity.getBoundingBox().intersects(boundingBox)) {
                        willHit = true;
                        target = entity;
                        hitPos = nextPos;
                        lastHitPos = oldHitPos;
                        hasHitEntity = true;
                        break;
                    }
                }
            }

            if (hasHitEntity) {
                break;
            }

            BlockPos blockPos = new BlockPos(nextPos);
            if (!mc.world.isAirBlock(blockPos)) {
                BlockState state = mc.world.getBlockState(blockPos);

                boolean isPassable = state.getMaterial().isReplaceable() ||
                        state.getMaterial().isLiquid() ||
                        state.getBlock() instanceof net.minecraft.block.CarpetBlock ||
                        state.getBlock() instanceof net.minecraft.block.FlowerBlock ||
                        state.getBlock() instanceof net.minecraft.block.TallGrassBlock ||
                        state.getBlock() instanceof net.minecraft.block.GrassBlock ||
                        state.getBlock() instanceof net.minecraft.block.BushBlock;

                if (!isPassable) {
                    RayTraceResult rayTrace = mc.world.rayTraceBlocks(new RayTraceContext(
                            lastPos,
                            nextPos,
                            RayTraceContext.BlockMode.OUTLINE,
                            RayTraceContext.FluidMode.NONE,
                            mc.player
                    ));

                    if (rayTrace != null && rayTrace.getType() == RayTraceResult.Type.BLOCK) {
                        BlockRayTraceResult blockRayTrace = (BlockRayTraceResult) rayTrace;
                        BlockState hitState = mc.world.getBlockState(blockRayTrace.getPos());

                        if (!hitState.getMaterial().isReplaceable() &&
                                !hitState.getMaterial().isLiquid() &&
                                !(hitState.getBlock() instanceof net.minecraft.block.CarpetBlock) &&
                                !(hitState.getBlock() instanceof net.minecraft.block.FlowerBlock) &&
                                !(hitState.getBlock() instanceof net.minecraft.block.TallGrassBlock) &&
                                !(hitState.getBlock() instanceof net.minecraft.block.GrassBlock) &&
                                !(hitState.getBlock() instanceof net.minecraft.block.BushBlock)) {

                            arrowHitPositions.add(nextPos);
                            if (hitPos == null) {
                                hitPos = nextPos;
                                hitSide = blockRayTrace.getFace();
                            }
                            return;
                        }
                    }
                }
            }


            if (nextPos.y <= 0) {
                hitPos = new Vector3d(nextPos.x, 0, nextPos.z);
                hasHitGround = true;
                break;
            }

            lastPos = currentPos;
            currentPos = nextPos;
            motion = new Vector3d(
                    motion.x,
                    motion.y - gravity * stepSize,
                    motion.z
            );

            double maxDistance = 50000;
            if (currentPos.distanceTo(pos) > maxDistance) {
                hitPos = nextPos;
                break;
            }
        }


        if (hitPos == null) {
            hitPos = currentPos;
        }

        lastHitPos = oldHitPos;
    }

    private void renderTrajectory() {
        if (hitPos == null) return;

        boolean isCrossbow = mc.player.getHeldItemMainhand().getItem() == Items.CROSSBOW &&
                CrossbowItem.isCharged(mc.player.getHeldItemMainhand());

        if (renderPos == null) {
            renderPos = hitPos;
        } else {
            double distance = lastHitPos != null ? hitPos.distanceTo(lastHitPos) : 0;
            float speed = (float) MathHelper.clamp(INTERPOLATION_SPEED * (distance > 1 ? 0.5 : 1), 0.05f, 0.2f);

            renderPos = new Vector3d(
                    lerp(renderPos.x, hitPos.x, speed),
                    lerp(renderPos.y, hitPos.y, speed),
                    lerp(renderPos.z, hitPos.z, speed)
            );
        }

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(2F);

        Color color = willHit ? HIT_COLOR : MISS_COLOR;
        GL11.glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);

        double renderPosX = mc.getRenderManager().info.getProjectedView().getX();
        double renderPosY = mc.getRenderManager().info.getProjectedView().getY();
        double renderPosZ = mc.getRenderManager().info.getProjectedView().getZ();


        double distanceToTarget = Math.sqrt(
                Math.pow(renderPos.x - renderPosX, 2) +
                        Math.pow(renderPos.y - renderPosY, 2) +
                        Math.pow(renderPos.z - renderPosZ, 2)
        );


        double maxRenderDistance = 500.0;


        Vector3d displayPos;
        if (distanceToTarget > maxRenderDistance) {

            double dx = renderPos.x - renderPosX;
            double dy = renderPos.y - renderPosY;
            double dz = renderPos.z - renderPosZ;
            double norm = Math.sqrt(dx*dx + dy*dy + dz*dz);


            displayPos = new Vector3d(
                    renderPosX + dx/norm * maxRenderDistance * 0.9,
                    renderPosY + dy/norm * maxRenderDistance * 0.9,
                    renderPosZ + dz/norm * maxRenderDistance * 0.9
            );
        } else {
            displayPos = renderPos;
        }

        float pitch = mc.player.rotationPitch * 0.017453292F;
        float yaw = mc.player.rotationYaw * 0.017453292F;

        double lookX = -MathHelper.sin(yaw) * MathHelper.cos(pitch);
        double lookY = -MathHelper.sin(pitch);
        double lookZ = MathHelper.cos(yaw) * MathHelper.cos(pitch);

        double upX = -MathHelper.sin(yaw) * MathHelper.sin(pitch);
        double upY = MathHelper.cos(pitch);
        double upZ = MathHelper.cos(yaw) * MathHelper.sin(pitch);

        double rightX = -MathHelper.cos(yaw);
        double rightY = 0;
        double rightZ = -MathHelper.sin(yaw);

        if (isCrossbow) {
            for (Vector3d arrowHit : arrowHitPositions) {
                renderCrosshair(arrowHit, renderPosX, renderPosY, renderPosZ, rightX, rightY, rightZ, upX, upY, upZ);
            }
        } else {
            renderCrosshair(displayPos, renderPosX, renderPosY, renderPosZ, rightX, rightY, rightZ, upX, upY, upZ);
        }

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    private void renderCrosshair(Vector3d pos, double renderPosX, double renderPosY, double renderPosZ,
                                 double rightX, double rightY, double rightZ,
                                 double upX, double upY, double upZ) {
        GL11.glBegin(GL11.GL_LINE_LOOP);
        for (int i = 0; i < 360; i += 10) {
            double angle = i * Math.PI / 180;
            double cos = Math.cos(angle) * CROSSHAIR_SIZE;
            double sin = Math.sin(angle) * CROSSHAIR_SIZE;

            double x = pos.x - renderPosX + rightX * cos + upX * sin;
            double y = pos.y - renderPosY + rightY * cos + upY * sin;
            double z = pos.z - renderPosZ + rightZ * cos + upZ * sin;

            GL11.glVertex3d(x, y, z);
        }
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(
                pos.x - renderPosX + upX * CROSSHAIR_SIZE,
                pos.y - renderPosY + upY * CROSSHAIR_SIZE,
                pos.z - renderPosZ + upZ * CROSSHAIR_SIZE
        );
        GL11.glVertex3d(
                pos.x - renderPosX - upX * CROSSHAIR_SIZE,
                pos.y - renderPosY - upY * CROSSHAIR_SIZE,
                pos.z - renderPosZ - upZ * CROSSHAIR_SIZE
        );
        GL11.glVertex3d(
                pos.x - renderPosX + rightX * CROSSHAIR_SIZE,
                pos.y - renderPosY + rightY * CROSSHAIR_SIZE,
                pos.z - renderPosZ + rightZ * CROSSHAIR_SIZE
        );
        GL11.glVertex3d(
                pos.x - renderPosX - rightX * CROSSHAIR_SIZE,
                pos.y - renderPosY - rightY * CROSSHAIR_SIZE,
                pos.z - renderPosZ - rightZ * CROSSHAIR_SIZE
        );
        GL11.glEnd();
    }

    private double lerp(double a, double b, float t) {
        return a + (b - a) * t;
    }
}
