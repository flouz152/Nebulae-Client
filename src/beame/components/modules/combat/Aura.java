package beame.components.modules.combat;

import beame.Essence;
import beame.module.Category;
import beame.module.Module;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.EnumSetting;
import beame.setting.SettingList.RadioSetting;
import beame.setting.SettingList.SliderSetting;
import beame.util.math.TimerUtil;
import beame.util.other.MoveUtil;
import events.Event;
import events.impl.player.EventInput;
import events.impl.player.EventMotion;
import events.impl.player.EventUpdate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.TridentItem;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import beame.components.modules.combat.AuraHandlers.other.RotationUtil;

import java.util.concurrent.ThreadLocalRandom;

public class Aura extends Module {
    private final SliderSetting range = new SliderSetting("Дистанция", 3.3f, 2.0f, 6.0f, 0.05f);
    private final SliderSetting fov = new SliderSetting("ФОВ", 180.0f, 10.0f, 360.0f, 1.0f);
    private final SliderSetting minCps = new SliderSetting("Мин CPS", 8.0f, 1.0f, 20.0f, 0.1f);
    private final SliderSetting maxCps = new SliderSetting("Макс CPS", 12.0f, 1.0f, 20.0f, 0.1f);
    private final SliderSetting rotationSpeed = new SliderSetting("Скорость поворота", 55.0f, 5.0f, 180.0f, 1.0f);

    private final EnumSetting targets = new EnumSetting("Цели",
            new BooleanSetting("Игроки", true),
            new BooleanSetting("Мобы", true),
            new BooleanSetting("Животные", false),
            new BooleanSetting("Жители", false),
            new BooleanSetting("Невидимых", true),
            new BooleanSetting("Друзья", false)
    );

    private final RadioSetting priority = new RadioSetting("Приоритет", "Ближайший",
            "Ближайший",
            "Минимум HP",
            "Максимум HP");

    private final BooleanSetting movementCorrection = new BooleanSetting("Коррекция движения", true);
    private final BooleanSetting stopOnUse = new BooleanSetting("Не бить при использовании", true);
    private final BooleanSetting stopOnInventory = new BooleanSetting("Не бить в инвентаре", true);
    private final BooleanSetting onlyWeapon = new BooleanSetting("Только с оружием", true);
    private final BooleanSetting allowWalls = new BooleanSetting("Бить через стены", false);
    private final BooleanSetting keepSprint = new BooleanSetting("Сохранять спринт", true);
    private final BooleanSetting onlyCritical = new BooleanSetting("Только криты", false);
    private final BooleanSetting smartCritical = new BooleanSetting("Умные криты", true).setVisible(onlyCritical::get);

    private final TimerUtil attackTimer = new TimerUtil();
    private long nextAttackDelay = 200L;

    private LivingEntity currentTarget;
    private Vector2f currentRotations = new Vector2f(0.0F, 0.0F);
    private boolean rotationsInitialized;
    private Vector3d aimPoint;
    private int aimPointTicks;

    public Aura() {
        super("AttackAura", Category.Combat, true, "Автоматически бьет энтити в установленном радиусе");
        addSettings(range, fov, minCps, maxCps, rotationSpeed, targets, priority,
                movementCorrection, stopOnUse, stopOnInventory, onlyWeapon, allowWalls,
                keepSprint, onlyCritical, smartCritical);
    }

    @Override
    protected void onEnable() {
        attackTimer.reset();
        nextAttackDelay = computeAttackDelay();
        currentTarget = null;
        resetRotations();
    }

    @Override
    protected void onDisable() {
        currentTarget = null;
        resetRotations();
    }

    @Override
    public void event(Event event) {
        if (mc.player == null || mc.world == null) {
            return;
        }

        if (event instanceof EventInput inputEvent) {
            if (movementCorrection.get() && currentTarget != null) {
                MoveUtil.fixMovement(inputEvent, currentRotations.x);
            }
            return;
        }

        if (event instanceof EventUpdate) {
            updateTarget();

            if (currentTarget != null) {
                updateRotations();
                attemptAttack();
            } else {
                resetRotations();
            }
            return;
        }

        if (event instanceof EventMotion motionEvent) {
            if (currentTarget == null) {
                return;
            }

            motionEvent.setYaw(currentRotations.x);
            motionEvent.setPitch(currentRotations.y);
            mc.player.rotationYawHead = currentRotations.x;
            mc.player.renderYawOffset = RotationUtil.calculateCorrectYawOffset(currentRotations.x);
            mc.player.rotationPitchHead = currentRotations.y;
        }
    }

    private void updateTarget() {
        LivingEntity previousTarget = currentTarget;
        LivingEntity bestTarget = null;
        double bestScore = Double.MAX_VALUE;
        AxisAlignedBB searchBox = mc.player.getBoundingBox().grow(range.get() + 2.0, 3.0, range.get() + 2.0);

        for (LivingEntity entity : mc.world.getEntitiesWithinAABB(LivingEntity.class, searchBox, e -> e != mc.player)) {
            if (!isTargetValid(entity)) {
                continue;
            }

            double distance = mc.player.getDistance(entity);
            if (distance > range.get() + 0.25f) {
                continue;
            }

            if (!allowWalls.get() && !mc.player.canEntityBeSeen(entity) && distance > 2.5f) {
                continue;
            }

            Vector3d targetCenter = entity.getBoundingBox().getCenter();
            Vector2f desiredRotation = RotationUtil.calculate(mc.player.getEyePosition(1.0F), targetCenter);
            float yawDiff = Math.abs(MathHelper.wrapDegrees(desiredRotation.x - mc.player.rotationYaw));
            double score = getPriorityScore(entity, distance, yawDiff);

            if (score < bestScore) {
                bestScore = score;
                bestTarget = entity;
            }
        }

        currentTarget = bestTarget;
        if (currentTarget != previousTarget) {
            aimPoint = null;
            rotationsInitialized = false;
            aimPointTicks = 0;
        }
    }

    private void updateRotations() {
        if (currentTarget == null) {
            return;
        }

        if (!rotationsInitialized) {
            currentRotations = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
            rotationsInitialized = true;
        }

        if (aimPoint == null || aimPointTicks++ > 8) {
            aimPoint = getAimPoint(currentTarget);
            aimPointTicks = 0;
        }

        Vector2f desired = RotationUtil.calculate(mc.player.getEyePosition(1.0F), aimPoint);
        currentRotations = smoothRotations(currentRotations, desired, rotationSpeed.get());
    }

    private void attemptAttack() {
        if (currentTarget == null || !currentTarget.isAlive()) {
            return;
        }

        if (shouldPause()) {
            return;
        }

        double distance = mc.player.getDistance(currentTarget);
        if (distance > range.get() + 0.15f) {
            return;
        }

        if (!allowWalls.get() && !mc.player.canEntityBeSeen(currentTarget) && distance > 2.5f) {
            return;
        }

        if (!isWithinFov()) {
            return;
        }

        if (!canUseWeapon()) {
            return;
        }

        if (!canCritical()) {
            return;
        }

        if (attackTimer.getTimePassed() < nextAttackDelay) {
            return;
        }

        if (mc.player.getCooledAttackStrength(0.5F) < 1.0F) {
            return;
        }

        boolean shouldKeepSprint = keepSprint.get() && mc.player.isSprinting();
        if (shouldKeepSprint) {
            mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_SPRINTING));
        }

        mc.playerController.attackEntity(mc.player, currentTarget);
        mc.player.swingArm(Hand.MAIN_HAND);

        if (shouldKeepSprint) {
            mc.player.setSprinting(true);
        }

        attackTimer.reset();
        nextAttackDelay = computeAttackDelay();
        aimPoint = null;
    }

    private boolean shouldPause() {
        if (stopOnInventory.get() && mc.currentScreen != null) {
            return true;
        }

        if (stopOnUse.get() && mc.player.isHandActive()) {
            return true;
        }

        return false;
    }

    private boolean canUseWeapon() {
        if (!onlyWeapon.get()) {
            return true;
        }

        ItemStack stack = mc.player.getHeldItemMainhand();
        Item item = stack.getItem();
        return item instanceof SwordItem || item instanceof AxeItem || item instanceof TridentItem;
    }

    private boolean canCritical() {
        if (!onlyCritical.get()) {
            return true;
        }

        boolean inFluid = (mc.player.isInWater() && mc.player.areEyesInFluid(FluidTags.WATER))
                || (mc.player.isInLava() && mc.player.areEyesInFluid(FluidTags.LAVA))
                || mc.player.isSwimming();

        boolean smart = smartCritical.get();
        if (!smart) {
            return !mc.player.isOnGround() && !inFluid;
        }

        if (mc.player.isOnLadder() || mc.player.isPassenger() || mc.player.abilities.isFlying || mc.player.isElytraFlying()) {
            return false;
        }

        if (mc.player.isPotionActive(Effects.BLINDNESS) || mc.player.isPotionActive(Effects.LEVITATION)
                || mc.player.isPotionActive(Effects.SLOW_FALLING)) {
            return false;
        }

        if (inFluid) {
            return false;
        }

        return mc.player.fallDistance > 0.0F && !mc.player.isOnGround();
    }

    private boolean isWithinFov() {
        if (fov.get() >= 360.0f || currentTarget == null) {
            return true;
        }

        Vector3d eyePos = mc.player.getEyePosition(1.0F);
        Vector3d targetPoint = aimPoint != null ? aimPoint : currentTarget.getBoundingBox().getCenter();
        Vector2f desired = RotationUtil.calculate(eyePos, targetPoint);
        float yawDiff = Math.abs(MathHelper.wrapDegrees(desired.x - currentRotations.x));
        return yawDiff <= fov.get() / 2.0f;
    }

    private long computeAttackDelay() {
        float min = Math.max(0.1f, minCps.get());
        float max = Math.max(0.1f, maxCps.get());
        if (max < min) {
            float temp = min;
            min = max;
            max = temp;
        }

        double cps = max == min ? min : ThreadLocalRandom.current().nextDouble(min, max);
        double delay = 1000.0 / Math.max(0.1, cps);
        return (long) Math.max(50L, delay);
    }

    private Vector3d getAimPoint(LivingEntity entity) {
        AxisAlignedBB box = entity.getBoundingBox();
        double x = ThreadLocalRandom.current().nextDouble(box.minX, box.maxX);
        double minY = box.minY + entity.getHeight() * 0.2;
        double maxY = box.minY + Math.max(0.1, Math.min(entity.getHeight(), entity.getEyeHeight(entity.getPose())));
        double y = ThreadLocalRandom.current().nextDouble(minY, Math.max(minY + 0.1, maxY));
        double z = ThreadLocalRandom.current().nextDouble(box.minZ, box.maxZ);
        return new Vector3d(x, y, z);
    }

    private Vector2f smoothRotations(Vector2f current, Vector2f desired, float speed) {
        float yawDiff = MathHelper.wrapDegrees(desired.x - current.x);
        float pitchDiff = desired.y - current.y;
        float maxStep = Math.max(1.0f, speed);

        float newYaw = current.x + MathHelper.clamp(yawDiff, -maxStep, maxStep);
        float newPitch = current.y + MathHelper.clamp(pitchDiff, -maxStep, maxStep);
        newPitch = MathHelper.clamp(newPitch, -90.0f, 90.0f);
        return new Vector2f(newYaw, newPitch);
    }

    private double getPriorityScore(LivingEntity entity, double distance, float yawDiff) {
        double base;
        if (priority.is("Минимум HP")) {
            base = entity.getHealth();
        } else if (priority.is("Максимум HP")) {
            base = -entity.getHealth();
        } else {
            base = distance;
        }

        return base + (yawDiff / 360.0);
    }

    private boolean isTargetValid(LivingEntity entity) {
        if (entity == null || entity == mc.player || !entity.isAlive() || entity.removed) {
            return false;
        }

        if (entity instanceof ArmorStandEntity) {
            return false;
        }

        if (entity instanceof PlayerEntity player) {
            if (!targets.get("Игроки").get()) {
                return false;
            }

            if (!targets.get("Друзья").get() && Essence.getHandler().friends.isFriend(player.getGameProfile().getName())) {
                return false;
            }

            if (player.isCreative() || player.isSpectator()) {
                return false;
            }
        } else if (entity instanceof AnimalEntity) {
            if (!targets.get("Животные").get()) {
                return false;
            }
        } else if (entity instanceof VillagerEntity) {
            if (!targets.get("Жители").get()) {
                return false;
            }
        } else if (entity instanceof MonsterEntity || entity instanceof MobEntity) {
            if (!targets.get("Мобы").get()) {
                return false;
            }
        } else {
            if (!targets.get("Мобы").get()) {
                return false;
            }
        }

        if (entity.isInvisible() && !targets.get("Невидимых").get()) {
            return false;
        }

        return true;
    }

    private void resetRotations() {
        rotationsInitialized = false;
        aimPoint = null;
        aimPointTicks = 0;
        currentRotations = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
    }
}
