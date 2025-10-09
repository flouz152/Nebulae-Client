package beame.components.modules.combat;

import beame.Nebulae;
import beame.components.modules.combat.AuraHandlers.FuntimeNewRotation;
import beame.components.modules.combat.AuraHandlers.component.core.combat.Rotation;
import beame.components.modules.combat.AuraHandlers.component.core.combat.RotationComponent;
import beame.components.modules.combat.AuraHandlers.other.AuraUtil;
import beame.components.modules.combat.AuraHandlers.other.Rotates;
import beame.util.math.*;
import beame.util.other.MoveUtil;
import beame.util.player.InventoryUtility;
import beame.util.player.PlayerUtil;
import events.Event;
import events.impl.player.EventInput;
import events.impl.player.EventMotion;
import events.impl.player.EventUpdate;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.NonFinal;
import beame.module.Category;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import beame.module.Module;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.EnumSetting;
import beame.setting.SettingList.RadioSetting;
import beame.setting.SettingList.SliderSetting;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.hypot;
import static net.minecraft.util.math.MathHelper.*;

@Getter
public class Aura extends Module {
// leaked by itskekoff; discord.gg/sk3d JBoAZy3m

    public final RadioSetting rotationType = new RadioSetting("Тип наведения", "ФанТайм", "РиллиВорлд", "ФанТайм", "СпукиТайм", "ХвХ", "LuckyDayz", "Тест");

    public final RadioSetting targetSort = new RadioSetting("Приоритет по",
            "По всему", "Здоровью",
            "Броне",
            "Дистанции",
            "Полю зрения",
            "По всему");

    public final RadioSetting correctionType = new RadioSetting("Мод коррекции", "Сфокусированный", "Свободный", "Сфокусированный");
    public final BooleanSetting attack36 = new BooleanSetting("Обход аттаки 3.6 блоков", true);
    public final SliderSetting range = new SliderSetting("Дистанция аттаки", 3.00f, 2.00f, 6.00f, 0.05f).setVisible(() -> !attack36.get() || !rotationType.is("FunTime") || !rotationType.is("FunTime New"));
    private final SliderSetting preRange = new SliderSetting("Доп дистанция", 1F, 0F, 3F, 0.1F);
    public final SliderSetting fov = new SliderSetting("Фов", 360, 10, 360, 1f);

    public final EnumSetting filter = new EnumSetting("Кого атаковать",
            new BooleanSetting("Игроков", true),
            new BooleanSetting("Мобов", false),
            new BooleanSetting("Животных", false),
            new BooleanSetting("Жителей", false),
            new BooleanSetting("Друзей", false),
            new BooleanSetting("Невидимых", true),
            new BooleanSetting("Голых", true));

    public final EnumSetting noAttackCheck = new EnumSetting("Не бить если",
            new BooleanSetting("Открыт инвентарь", false),
            new BooleanSetting("Используешь еду", true));

    private final BooleanSetting onlycrit = new BooleanSetting("Только криты", true, 0);
    private final BooleanSetting smartcrit = new BooleanSetting("Умные криты", true, 0).setVisible(() -> onlycrit.get());
    private final BooleanSetting breakshield = new BooleanSetting("Ломать щит", true, 0).setVisible(() -> rotationType.get("РиллиВорлд") || rotationType.get("ХвХ") || rotationType.get("СпукиТайм") || rotationType.get("LuckyDayz"));
    private final BooleanSetting otzhimshield = new BooleanSetting("Отжимать щит", true, 0).setVisible(() -> rotationType.get("РиллиВорлд") || rotationType.get("ХвХ") || rotationType.get("СпукиТайм") || rotationType.get("LuckyDayz"));
    private final BooleanSetting walls = new BooleanSetting("Бить через стены", true, 0);
    private final BooleanSetting wallsAdvanced = new BooleanSetting("Бить через стены 2", false, 0);
    private final BooleanSetting synctps = new BooleanSetting("Синхронизация с тиками", false, 0);
    private final BooleanSetting fastrotation = new BooleanSetting("Ускорять ротацию", false, 0).setVisible(() -> rotationType.get("РиллиВорлд") || rotationType.get("ХвХ"));
    private final BooleanSetting interpol = new BooleanSetting("Интерполяция", false, 0);

    private final SliderSetting luckyYawSpeed = new SliderSetting("LuckyDayz скорость yaw", 80f, 20f, 200f, 1f)
            .setVisible(() -> rotationType.get("LuckyDayz"));
    private final SliderSetting luckyPitchSpeed = new SliderSetting("LuckyDayz скорость pitch", 70f, 15f, 180f, 1f)
            .setVisible(() -> rotationType.get("LuckyDayz"));
    private final SliderSetting luckyPrediction = new SliderSetting("LuckyDayz предикт", 3.5f, 0f, 8f, 0.1f)
            .setVisible(() -> rotationType.get("LuckyDayz"));
    private final SliderSetting luckySmoothing = new SliderSetting("LuckyDayz сглаживание", 0.45f, 0.05f, 1f, 0.05f)
            .setVisible(() -> rotationType.get("LuckyDayz"));
    private final BooleanSetting luckyAimCenter = new BooleanSetting("LuckyDayz центр хитбокса", false, 0)
            .setVisible(() -> rotationType.get("LuckyDayz"));
    private final BooleanSetting luckyAdaptiveFocus = new BooleanSetting("LuckyDayz адаптив", true, 0)
            .setVisible(() -> rotationType.get("LuckyDayz"));

    private final TimerUtil timerUtil = new TimerUtil();
    public Vector2f rotateVector = new Vector2f(0, 0);
    private final FuntimeNewRotation funtimeRotation = new FuntimeNewRotation();
    public LivingEntity target, prevTarget;
    public Entity entity;
    int ticks = 0;
    private boolean isCalculatingRotation = false;
    @NonFinal
    @Getter int attacks;
    @NonFinal @Getter @Setter
    int fdCount;
    float speedX = 1.5F;
    float speedY = 0.0F;
    float lastYaw;
    float lastPitch;
    float targetPitch;
    float gcd;

    private Vector3d randomHitboxPoint;
    private Vector3d targetHitboxPoint;
    private int randomHitboxTicks;
    private int interpTicks;
    private double interpT;
    private Vector3d springVelocity;
    private final RadioSetting interpolationType = new RadioSetting("Тип интерполяции",
            "Линейная", "Квадратичная", "Кубическая", "Плавный шаг", "Пружина", "Шумовая", "Линейная").setVisible(() -> interpol.get());

    private float smoothFactor = 0.0f;
    private float lastYawOffset = 0.0f;
    private float lastPitchOffset = 0.0f;
    private double interpolationFactor = 1.0;
    private final SecureRandom neuralRandom = new SecureRandom();

    private Vector3d luckyDayzSmoothedAim;
    private float luckyDayzYawVelocity;
    private float luckyDayzPitchVelocity;
    private double luckyDayzLastDistance;
    private boolean luckyDayzSprintReset;

    public Aura() {
        super("AttackAura", Category.Combat, true, "Автоматически бьет энтити в установленном радиусе");
        addSettings(rotationType, targetSort, correctionType, interpolationType, attack36, range, preRange, fov, filter, noAttackCheck, onlycrit, smartcrit, breakshield,
                otzhimshield, walls, wallsAdvanced, synctps, fastrotation, interpol, luckyYawSpeed, luckyPitchSpeed, luckyPrediction, luckySmoothing, luckyAimCenter, luckyAdaptiveFocus);
    }

    @Override
    public void event(Event event) {
        if (isCalculatingRotation) return;

        if (event instanceof EventInput inputEvent) {
            if (correctionType.get("Свободный") && target != null && mc.player != null) {
                MoveUtil.fixMovement(inputEvent, rotateVector.x);
                return;
            }
        }

        if (event instanceof EventUpdate eventUpdate) {
            updateTarget();

            if (target != null) {
                processRotationLogic();

                try {
                    isCalculatingRotation = true;
                    if (rotationType.get("РиллиВорлд")) {
                        ReallyWorldSpeed();
                    } else if (rotationType.get("ФанТайм")) {
                        FunTimeSpeed();
                    } else if (rotationType.get("СпукиТайм")) {
                        FunTimeSpeed();
                    } else if (rotationType.get("ХолиВорлд")) {
                        HolyWorldSpeed();
                    } else if (rotationType.get("ХвХ")) {
                        HvHSpeed();
                    } else if (rotationType.get("LuckyDayz")) {
                        LuckyDayzSpeed();
                    } else if (rotationType.get("Тест")) {
                        handleTest();
                    }
                } finally {
                    isCalculatingRotation = false;
                }
            } else {
                timerUtil.setMs(0);
                resetSnapRotation();
            }
        }
        if (event instanceof EventMotion eventMotion) {
            if (target == null) return;
            float yaw = rotateVector.x;
            float pitch = rotateVector.y;

            eventMotion.setYaw(yaw);
            eventMotion.setPitch(pitch);
            mc.player.rotationYawHead = yaw;
            mc.player.renderYawOffset = yaw;
            mc.player.rotationPitchHead = pitch;
        }
        if (rotationType.get("РиллиВорлд")) {
            ReallyWorldSpeed();
/*        } else if (rotationType.get("")) {
            ();*/
        } else if (rotationType.get("LuckyDayz")) {
            LuckyDayzSpeed();
        }
    }


    private void processRotationLogic() {
        if (target == null || mc.player == null || mc.world == null) return;

        boolean inCurrentView = LookTarget(target);
        if (!inCurrentView) {
            if (!(rotationType.get("LuckyDayz") && isWithinRotationFov(rotateVector, target))) {
                return;
            }
        }
        if (AuraUtil.getStrictDistance(target) > range.get() + preRange.get()) return;

        float attackStrength = mc.player.getCooledAttackStrength(synctps.get() ? Nebulae.getHandler().getServerUtils().getAdjustTicks() : (rotationType.get("СпукиТайм") ? (MathUtil.random(0.97f,1)) : 0.5f));
        boolean isReady = attackStrength > (rotationType.get("СпукиТайм") ? MathUtil.random(.87f, .91f) : .87F);

        if (!isReady || !timerUtil.hasTimeElapsed()) return;

        boolean jumpPressed = mc.gameSettings.keyBindJump.isKeyDown();

        if (smartcrit.get()) {
            if (jumpPressed) {
                if (!mc.player.onGround && mc.player.fallDistance > 0.0F) {
                    handleAttack();
                    ticks = 1;
                    timerUtil.reset();
                }
            } else {
                handleAttack();
                ticks = 1;
                timerUtil.reset();
            }
        } else if (shouldFalling()) {
            handleAttack();
            ticks = 1;
            timerUtil.reset();
        }
    }

    private void GrimSpeed() {
        if (ticks > 0) {
            if (!LookTarget(target)) return;

            Vector3d targetVector = AuraUtil.calculateTargetVector(target);
            float yawToTarget = (float) wrapDegrees(Math.toDegrees(Math.atan2(targetVector.z, targetVector.x)) - 90);
            float pitchToTarget = (float) (-Math.toDegrees(Math.atan2(targetVector.y, hypot(targetVector.x, targetVector.z))));
            float yawDelta = (wrapDegrees(yawToTarget - rotateVector.x));
            float pitchDelta = (wrapDegrees(pitchToTarget - rotateVector.y));

            float roundedYaw = (int) yawDelta;
            float yaw = rotateVector.x + roundedYaw / 1.2f;
            float pitch = clamp(rotateVector.y + pitchDelta / 1f, -90, 90);

            if (!shouldFalling()) {
                yaw = rotateVector.x + (mc.player.rotationYaw - rotateVector.x) / 2.2f;
                pitch = clamp(rotateVector.y + (mc.player.rotationPitch - rotateVector.y) / 2.2f, -90, 90);
            }

            float gcd = SensUtil.getGCDValue();
            yaw -= (yaw - rotateVector.x) % gcd;
            pitch -= (pitch - rotateVector.y) % gcd;

            rotateVector = new Vector2f(yaw, pitch);
            assert mc.player != null;
            mc.player.rotationYawOffset = yaw;

            ticks--;
        } else {
            resetSnapRotation();
        }
    }

    private void updateTarget() {
        List<LivingEntity> targets = new ArrayList<>();

        if (target != null && isFilter(target)) {
            float totalRange = range.get() + preRange.get();
            if (mc.player.getDistance(target) <= totalRange) {
                return;
            }
        }

        for (Entity entity : mc.world.getAllEntities()) {
            if (entity instanceof LivingEntity living && isFilter(living)) {
                float totalRange = range.get() + preRange.get();
                if (mc.player.getDistance(living) <= totalRange) {
                    targets.add(living);
                }
            }
        }

        if (!targets.isEmpty()) {
            sortTargets(targets);
            LivingEntity selected = targets.get(0);
            if (selected != target) {
                prevTarget = target;
                target = selected;
                resetLuckyDayzTracking();
            } else {
                target = selected;
            }
        } else {
            if (target != null) {
                prevTarget = target;
                resetLuckyDayzTracking();
            }
            target = null;
        }
    }

    private void sortTargets(List<LivingEntity> targets) {
        Comparator<LivingEntity> comparator = (o1, o2) -> 0;

        if (targetSort.get("Здоровью")) {
            comparator = Comparator.comparingDouble(this::getEntityHealth).reversed();
        }
        if (targetSort.get("Броне")) {
            comparator = comparator.thenComparing(Comparator.comparingDouble(o -> {
                if (o instanceof PlayerEntity) {
                    return this.getEntityArmor((PlayerEntity) o);
                }
                return 0.0;
            }).reversed());
        }
        if (targetSort.get("Дистанции")) {
            comparator = comparator.thenComparingDouble(mc.player::getDistance);
        }
        if (targetSort.get("Полю зрения")) {
            comparator = comparator.thenComparingDouble(this::getViewFov).reversed();
        }

        if (targetSort.get("По всему")) {
            comparator = Comparator.comparingDouble((LivingEntity o) -> mc.player.getDistance(o))
                    .thenComparingDouble(this::getViewFov)
                    .thenComparingDouble(this::getEntityHealth)
                    .thenComparingDouble(o -> {
                        if (o instanceof PlayerEntity) {
                            return this.getEntityArmor((PlayerEntity) o);
                        }
                        return 0.0;
                    });
        }

        targets.sort(comparator);
    }

    private double getViewFov(LivingEntity entity) {
        Vector3d playerVec = mc.player.getLookVec();
        Vector3d targetVec = entity.getPositionVec().subtract(mc.player.getPositionVec()).normalize();
        double dotProduct = playerVec.dotProduct(targetVec);
        return Math.acos(dotProduct) * (180 / Math.PI);
    }

    private void updateRotation(float yawSpeed, float pitchSpeed) {
        if (target == null || mc.player == null) return;
        Vector3d targetVector;
        AxisAlignedBB box = target.getBoundingBox();

        if (rotationType.get("1")) {
            double offsetX = Math.cos(System.currentTimeMillis() / 180.0) * MathUtil.random(0.3, 0.5);
            double offsetY = Math.sin(System.currentTimeMillis() / 210.0) * MathUtil.random(0.4, 0.19);
            double offsetZ = Math.cos(System.currentTimeMillis() / 170.0) * MathUtil.random(0.3, 0.5);

/*
            Vector3d motion = new Vector3d(
                target.getPosX() - target.lastTickPosX,
                target.getPosY() - target.lastTickPosY,
                target.getPosZ() - target.lastTickPosZ
            );*/

      /*
            double speedMultiplier = Math.min(1.0, motion.length() * 2);
            offsetX *= speedMultiplier;
            offsetY *= speedMultiplier;
            offsetZ *= speedMultiplier;*/

            interpolationFactor = 1.0;
            if (interpol.get()) {
                if (interpolationType.get("Линейная")) {
                    interpolationFactor = MathUtil.lerp(0.1, 0.9, Math.sin(System.currentTimeMillis() / 1000.0) * 0.5 + 0.5);
                } else if (interpolationType.get("Квадратичная")) {
                    double t = (Math.sin(System.currentTimeMillis() / 1000.0) * 0.5 + 0.5);
                    interpolationFactor = t * t;
                } else if (interpolationType.get("Кубическая")) {
                    double t = (Math.sin(System.currentTimeMillis() / 1000.0) * 0.5 + 0.5);
                    interpolationFactor = t * t * t;
                } else if (interpolationType.get("Плавный шаг")) {
                    mc.gameSettings.keyBindJump.isPressed();
                    double t = (Math.sin(System.currentTimeMillis() / 1000.0) * 0.5 + 0.5);
                    interpolationFactor = t * t * (3 - 2 * t);
                } else if (interpolationType.get("Пружина")) {
                    interpolationFactor = 1 - Math.pow(Math.E, -System.currentTimeMillis() / 500.0) * Math.cos(System.currentTimeMillis() / 200.0);
                } else if (interpolationType.get("Шумовая")) {
                    interpolationFactor = MathUtil.random(0.1, 3) + MathUtil.random(-0.35, 0.35);
                }
            }

            offsetX *= interpolationFactor;
            offsetY *= interpolationFactor;
            offsetZ *= interpolationFactor;

            double clampedX = MathHelper.clamp(target.getPosX() + offsetX, box.minX, box.maxX);
            double clampedY = target.getPosY() + target.getEyeHeight() * 0.9 + offsetY;
            double clampedZ = MathHelper.clamp(target.getPosZ() + offsetZ, box.minZ, box.maxZ);

            targetVector = new Vector3d(clampedX, clampedY, clampedZ)
                    .subtract(mc.player.getEyePosition(1.0F));
        } else {
            double offsetX = SensUtil.getSens((float)(Math.cos(System.currentTimeMillis() / 120) *
                    MathUtil.random(SensUtil.getSens(0.2f), SensUtil.getSens(0.15f))));
            double offsetY = Math.min((SensUtil.getSens((float)(Math.cos((double)System.currentTimeMillis() / 150) *
                    MathUtil.random(SensUtil.getSens(0.3f),SensUtil.getSens(0.8f)))) + target.getHeight()),target.getHeight());
            double offsetZ = SensUtil.getSens((float)(Math.cos(System.currentTimeMillis() / 100) *
                    MathUtil.random(SensUtil.getSens(0.2f), SensUtil.getSens(0.15f))));

            double clampedX = MathHelper.clamp(target.getPosX() + offsetX, box.minX, box.maxX);
            double clampedY = target.getPosY() + offsetY;
            double clampedZ = MathHelper.clamp(target.getPosZ() + offsetZ, box.minZ, box.maxZ);

            targetVector = new Vector3d(clampedX, clampedY, clampedZ)
                    .subtract(mc.player.getEyePosition(1.0F));
        }

        float yawToTarget = (float) wrapDegrees(Math.toDegrees(Math.atan2(targetVector.z, targetVector.x)) - 90);
        float pitchToTarget = (float) (-Math.toDegrees(Math.atan2(targetVector.y, hypot(targetVector.x, targetVector.z))));

        if (rotationType.get("1")) {
            float microFactor = interpol.get() ? (float)interpolationFactor : 1.0f;
            float microYaw = (float) (Math.sin(System.currentTimeMillis() / 100.0) * 0.3 * microFactor);
            float microPitch = (float) (Math.cos(System.currentTimeMillis() / 120.0) * 0.2 * microFactor);

            yawToTarget += microYaw;
            pitchToTarget += microPitch;
        }

        float yawDelta = (wrapDegrees(yawToTarget - rotateVector.x));
        float pitchDelta = (wrapDegrees(pitchToTarget - rotateVector.y));

        float clampedYaw, clampedPitch;
        if (rotationType.get("1")) {
            float adaptiveSpeed = MathUtil.random(0.7f, 0.9f);
            if (interpol.get()) {
                adaptiveSpeed *= (float)interpolationFactor;
            }
            clampedYaw = Math.min(Math.abs(yawDelta), yawSpeed * adaptiveSpeed);
            clampedPitch = Math.min(Math.abs(pitchDelta), pitchSpeed * adaptiveSpeed);
        } else {
            clampedYaw = Math.min(Math.abs(yawDelta), yawSpeed);
            clampedPitch = Math.min(Math.abs(pitchDelta), pitchSpeed);
        }

        handleHvH(yawDelta, pitchDelta, clampedYaw, clampedPitch);
    }

    private double maxHeight() {
        return target.getHeight() * (AuraUtility.getStrictDistance((target)) / mc.player.getDistance(target));
    }

    public void handleReallyWorld() {
        if (!LookTarget(target)) return;


        Vector3d vec = target.getPositionVec()
                .add(0, MathHelper.clamp(mc.player.getEyePosition(mc.getRenderPartialTicks()).y - target.getPosY() - 0.15f, 0, maxHeight()), 0)
                .subtract(mc.player.getEyePosition(mc.getRenderPartialTicks()))
                .normalize();

        float rawYaw = (float) Math.toDegrees(Math.atan2(-vec.x, vec.z));
        float rawPitch = (float) MathHelper.clamp(-Math.toDegrees(Math.atan2(vec.y, Math.hypot(vec.x, vec.z))), -90, 90);

        float yawDelta = (int) MathHelper.wrapDegrees(rawYaw - mc.player.rotationYaw);
        float pitchDelta = rawPitch - mc.player.rotationPitch;

        float yawSpeed = 360 * new Random().nextFloat(.9F, 1.F);
        float pitchSpeed = 360 * new Random().nextFloat(.9F, 1.F);

        float clampedYaw = MathHelper.clamp(yawDelta, -yawSpeed, yawSpeed);
        float clampedPitch = MathHelper.clamp(pitchDelta, -pitchSpeed, pitchSpeed);

        float randomreturn = MathUtil.random(20, 50);
        float randomturn = MathUtil.random(140, 240);
        float randomtime = MathUtil.random(2, 4f);

        Nebulae.getHandler().auraHelper.rotationHandler.update(new Rotates(
                        mc.player.rotationYaw + clampedYaw,
                        mc.player.rotationPitch + (mc.objectMouseOver.getType() == RayTraceResult.Type.ENTITY ? 0 : clampedPitch)),
                randomturn,  360F, (int) randomtime, 1);
    }

    private void applyFunTimeRotation(boolean attack, float yawDelta, float pitchDelta, float clampedYaw, float clampedPitch) {
        if (target == null || mc.player == null) {
            return;
        }

        handleReallyWorld();
    }

    public void handleFunTime() {
        if (!LookTarget(target)) return;

        Vector3d targetVector = AuraUtil.calculateTargetVector(target);
        float yawToTarget = (float) wrapDegrees(Math.toDegrees(Math.atan2(targetVector.z, targetVector.x)) - 90);
        float pitchToTarget = (float) (-Math.toDegrees(Math.atan2(targetVector.y, hypot(targetVector.x, targetVector.z))));
        float yawDelta = (wrapDegrees(yawToTarget - rotateVector.x));
        float pitchDelta = (wrapDegrees(pitchToTarget - rotateVector.y));

        float roundedYaw = (int) yawDelta;
        float yaw = rotateVector.x + roundedYaw / 1.2f + ThreadLocalRandom.current().nextFloat(-3.0F, 3.0F);
        float pitch = clamp(rotateVector.y + pitchDelta / 1f, -180.0F, 180.0F) + ThreadLocalRandom.current().nextFloat(-3.0F, 3.0F);

        if (!shouldFalling()) {
            yaw = rotateVector.x + (mc.player.rotationYaw - rotateVector.x) / 1.2f + ThreadLocalRandom.current().nextFloat(-3.0F, 3.0F);
            pitch = clamp(rotateVector.y + (mc.player.rotationPitch - rotateVector.y) / 1.2f, -180.0F, 180.0F) + ThreadLocalRandom.current().nextFloat(-3.0F, 3.0F);
        }


        float gcd = SensUtil.getGCDValue();
        yaw -= (yaw - rotateVector.x) % gcd;
        pitch -= (pitch - rotateVector.y) % gcd;

        rotateVector = new Vector2f(yaw, pitch);

        mc.player.rotationYawOffset = yaw;
    }

    public void handleSpookyTime() {
        if (!LookTarget(target)) return;

        Vector3d targetVec = target.getPositionVec()
                .add(0, target.getEyeHeight() - 0.15f, 0)
                .subtract(mc.player.getEyePosition(mc.getRenderPartialTicks()));

        float rawYaw = (float) Math.toDegrees(Math.atan2(-targetVec.x, targetVec.z));
        float rawPitch = (float) MathHelper.clamp(-Math.toDegrees(Math.atan2(targetVec.y, Math.hypot(targetVec.x, targetVec.z))), -90, 90);

        float microAdjustment = (float) (neuralRandom.nextGaussian() * 0.3);
        float predictionOffset = (float) (Math.sin(System.currentTimeMillis() / 200.0) * 2.0);


        smoothFactor = MathHelper.lerp(0.15f, smoothFactor, 1.0f);


        float distanceFactor = Math.min(1.0f, (float) (mc.player.getDistance(target) / range.get()));
        Vector3d targetMotion = new Vector3d(
                target.getPosX() - target.lastTickPosX,
                target.getPosY() - target.lastTickPosY,
                target.getPosZ() - target.lastTickPosZ
        );

        float adaptiveYawOffset = (float) (Math.atan2(targetMotion.x, targetMotion.z) * 2.0);
        float adaptivePitchOffset = (float) (Math.atan2(targetMotion.y, Math.hypot(targetMotion.x, targetMotion.z)) * 2.0);

        lastYawOffset = MathHelper.lerp(0.2f, lastYawOffset, adaptiveYawOffset + microAdjustment);
        lastPitchOffset = MathHelper.lerp(0.2f, lastPitchOffset, adaptivePitchOffset + microAdjustment);

        float finalYaw = rawYaw + lastYawOffset * smoothFactor + predictionOffset * distanceFactor;
        float finalPitch = rawPitch + lastPitchOffset * smoothFactor;

        float gcd = SensUtil.getGCDValue();
        finalYaw -= (finalYaw - rotateVector.x) % gcd;
        finalPitch -= (finalPitch - rotateVector.y) % gcd;

        float maxRotationSpeed = 20.0f + (float) (neuralRandom.nextGaussian() * 5.0);
        float yawDelta = MathHelper.wrapDegrees(finalYaw - rotateVector.x);
        float pitchDelta = finalPitch - rotateVector.y;

        yawDelta = MathHelper.clamp(yawDelta, -maxRotationSpeed, maxRotationSpeed);
        pitchDelta = MathHelper.clamp(pitchDelta, -maxRotationSpeed, maxRotationSpeed);

        rotateVector = new Vector2f(
                rotateVector.x + yawDelta,
                MathHelper.clamp(rotateVector.y + pitchDelta, -90.0f, 90.0f)
        );

        if (correctionType.get("Свободная")) {
            mc.player.rotationYawOffset = rotateVector.x;
        }
    }

    private final SecureRandom secureRandom = new SecureRandom();
    public float wrapLerp(float step, float input, float target) {
        return input + step * MathHelper.wrapDegrees(target - input);
    }

    private float applyGaussianJitter(float rotation, float strength) {
        return rotation + (float) (secureRandom.nextGaussian() * strength);
    }

    public void handleHolyWorld() {
        if (!isState() || target == null || mc.player == null || mc.world == null || AuraUtil.getStrictDistance(target) > range.get() + preRange.get()) {
            rotateVector = new Vector2f(mc.player != null ? mc.player.rotationYaw : 0, mc.player != null ? mc.player.rotationPitch : 0);
            return;
        }

        Vector3d vec = target.getPositionVec()
                .subtract(mc.player.getEyePosition(mc.getRenderPartialTicks()));

        double dst = Math.sqrt(Math.pow(vec.x, 3) + Math.pow(vec.z, 3));
        float rawYaw = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90);
        float rawPitch = (float) (-Math.toDegrees(Math.atan2(vec.y, dst)));

        float yawDelta = MathHelper.wrapDegrees(rawYaw - rotateVector.x);
        float pitchDelta = rawPitch - rotateVector.y;

        float yawSpeed = Math.min(0.2f + Math.abs(yawDelta) / 180f * 0.3f, 0.5f);
        float pitchSpeed = Math.min(0.15f + Math.abs(pitchDelta) / 90f * 0.2f, 0.4f);

        float targetYaw = wrapLerp(yawSpeed, rotateVector.x, rawYaw);
        float targetPitch = wrapLerp(pitchSpeed, rotateVector.y, MathHelper.clamp(rawPitch, -90, 90));

        targetYaw = applyGaussianJitter(targetYaw, 0.1f);
        targetPitch = applyGaussianJitter(targetPitch, 0.1f);

        float clampedYaw = SensUtil.getSensitivity2(targetYaw);
        float clampedPitch = SensUtil.getSensitivity2(targetPitch);

        rotateVector = new Vector2f(clampedYaw, clampedPitch);

        Rotation rotation = new Rotation(clampedYaw, clampedPitch);
        RotationComponent.update(rotation, 360, 1, 5, 1);
    }

    public void handleFunTimeNew() {
        if (!LookTarget(target)) return;

        Vector3d targetVector = AuraUtil.calculateTargetVector(target);
        float yawToTarget = (float) wrapDegrees(Math.toDegrees(Math.atan2(targetVector.z, targetVector.x)) - 90);
        float pitchToTarget = (float) (-Math.toDegrees(Math.atan2(targetVector.y, hypot(targetVector.x, targetVector.z))));
        float yawDelta = (wrapDegrees(yawToTarget - rotateVector.x));
        float pitchDelta = (wrapDegrees(pitchToTarget - rotateVector.y));

        float roundedYaw = (int) yawDelta;
        float yaw = rotateVector.x + roundedYaw / 1.2f;
        float pitch = clamp(rotateVector.y + pitchDelta / 1.2f, -90, 90);

        float gcd = SensUtil.getGCDValue();
        yaw -= (yaw - rotateVector.x) % gcd;
        pitch -= (pitch - rotateVector.y) % gcd;

        if (ticks > 0) {
            funtimeRotation.f3(target, new Vector2f(yaw, pitch), false, range.get());
        } else {
            funtimeRotation.f3(target, new Vector2f(yaw, pitch), true, range.get());
            resetSnapRotation();
        }

        rotateVector = new Vector2f(yaw, pitch);
        mc.player.rotationYawOffset = yaw;
    }


    public void handleHvH(float yawDelta, float pitchDelta, float clampedYaw, float clampedPitch) {
        float adjustedYawDelta = Math.signum(yawDelta) * Math.min(Math.abs(yawDelta), clampedYaw);
        float adjustedPitchDelta = Math.signum(pitchDelta) * Math.min(Math.abs(pitchDelta), clampedPitch);

        Vector2f correctedRotation = SensUtil.applySensitivityFix(
                new Vector2f(rotateVector.x + adjustedYawDelta, rotateVector.y + adjustedPitchDelta),
                rotateVector);
        rotateVector.x = correctedRotation.x;
        rotateVector.y = correctedRotation.y;

        mc.player.rotationYawOffset = rotateVector.x;
    }


    private void handleAttack() {
        if (target == null) return;
        if (mc.player.ticksSinceLastSwing < 2) return;
        if (!LookTarget(target) || isResetRotationChecks(noAttackCheck.get("Открыт инвентарь").get(), noAttackCheck.get("Используешь еду").get()))
            return;


        Vector3d playerPos = mc.player.getEyePosition(1.0F);
        AxisAlignedBB targetBox = target.getBoundingBox();
        double randomX = MathUtil.random(targetBox.minX, targetBox.maxX);
        double randomY = MathUtil.random(1.5, targetBox.maxY);
        double randomZ = MathUtil.random(targetBox.minZ, targetBox.maxZ);
        Vector3d targetPos = new Vector3d(randomX, randomY, randomZ);
        Vector3d lookVec = targetPos.subtract(playerPos).normalize();

        entity = MouseUtil.getMouseOver(target, rotateVector.x, rotateVector.y, getDistance());
        if (entity != null && entity != target) {
            AxisAlignedBB entityBox = entity.getBoundingBox();
            double randomEntityX = MathUtil.random(entityBox.minX, entityBox.maxX);
            double randomEntityY = MathUtil.random(1.5, entityBox.maxY);
            double randomEntityZ = MathUtil.random(entityBox.minZ, entityBox.maxZ);
            Vector3d entityPos = new Vector3d(randomEntityX, randomEntityY, randomEntityZ);
            Vector3d toEntity = entityPos.subtract(playerPos);

            double distanceToTarget = playerPos.distanceTo(targetPos);
            double distanceToEntity = playerPos.distanceTo(entityPos);

            if (distanceToEntity < distanceToTarget) {
                double dot = toEntity.normalize().dotProduct(lookVec);
                if (dot > 0.985) {
                    entity = target;
                    attacks++;
                    float mb = MathUtil.random(0.01f, 0.08f);
                    if (mc.player.fallDistance > (rotationType.get("СпукиТайм ") ? MathUtil.randomValue(0.001f, 0.01f) : 0)) {
                        fdCount = 0;
                    } else {
                        fdCount++;
                    }

                }
            }
        }

        if (isResetRotationChecks(noAttackCheck.get("Открыт инвентарь").get(), noAttackCheck.get("Используешь еду").get())) {
            return;
        }

        if ((entity == null || entity != target) && !mc.player.isElytraFlying()) {
            return;
        }

        if (otzhimshield.get()) {
            mc.playerController.onStoppedUsingItem(mc.player);
        }

        timerUtil.setMs(500);

        boolean sprinting = mc.player.serverSprintState;

        if (sprinting) {
            if(rotationType.get("СпукиТайм") && !mc.player.isElytraFlying()) {
                final KeyBinding[] pressedKeys = {mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack,
                        mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindJump};
                for (KeyBinding keyBinding : pressedKeys) {
                    keyBinding.setPressed(false);
                }
            }
            if (!mc.player.isElytraFlying())
                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
        }

        boolean bypass = attack36.get() && (rotationType.is("Фантайм") || !rotationType.is("FunTime New"));
        if (bypass) {
            mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, BlockPos.ZERO, Direction.UP));
            mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.ABORT_DESTROY_BLOCK, BlockPos.ZERO, Direction.UP));
        }

        if (rotationType.get("FunTime New")) {
            funtimeRotation.f3(target, rotateVector, true, range.get());
        }

        mc.playerController.attackEntity(mc.player, target);
        mc.player.swingArm(Hand.MAIN_HAND);

        if (sprinting) {
            mc.player.setSprinting(true);
            mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_SPRINTING));
        }

        if (target instanceof PlayerEntity player && breakshield.get()) {
            boolean shieldBroken = false;

            if (player.isBlocking()) {
                int invSlot = InventoryUtility.getAxeInInventory(false);
                int hotBarSlot = InventoryUtility.getAxeInInventory(true);

                if (hotBarSlot == -1 && invSlot != -1) {
                    int bestSlot = InventoryUtility.findBestSlotInHotBar();
                    mc.playerController.windowClick(0, invSlot, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(0, bestSlot + 36, 0, ClickType.PICKUP, mc.player);
                    mc.player.connection.sendPacket(new CHeldItemChangePacket(bestSlot));
                    mc.playerController.attackEntity(mc.player, entity);
                    mc.player.swingArm(Hand.MAIN_HAND);
                    shieldBroken = true;

                    mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
                    mc.playerController.windowClick(0, bestSlot + 36, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(0, invSlot, 0, ClickType.PICKUP, mc.player);
                }

                if (hotBarSlot != -1) {
                    mc.player.connection.sendPacket(new CHeldItemChangePacket(hotBarSlot));
                    mc.playerController.attackEntity(mc.player, entity);
                    mc.player.swingArm(Hand.MAIN_HAND);
                    shieldBroken = true;
                    mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
                }
            }
        }
    }

    boolean isBlockAboveHead = PlayerUtil.isBlockAboveHead();
    public boolean fixSplash = false;


    private float lastFallDistance = 0f;
    private int noCritTicks = 0;

    public boolean shouldFalling() {
        float attackStrength = mc.player.getCooledAttackStrength(synctps.get() ? Nebulae.getHandler().getServerUtils().getAdjustTicks() : (rotationType.get("СпукиТайм") ? (MathUtil.random(0.97f,1)) : 0.5f));
        boolean isReady = attackStrength > (rotationType.get("СпукиТайм") ? MathUtil.random(.87f, .91f) : .87F);

        float mb = MathUtil.random(0.001f, 0.008f);
        boolean isTrueCrit = !mc.player.onGround
                && !mc.player.isOnLadder()
                && !mc.player.isPotionActive(Effects.BLINDNESS)
                && !mc.player.isPassenger()
                && (mc.player.fallDistance > (rotationType.get("СпукиТайм") ? (float)(Math.random() * mb) : 0)
                || mc.player.isInWater()
                || mc.player.isSwimming()
                || mc.player.areEyesInFluid(FluidTags.WATER)
                || mc.player.isInLava() && mc.player.areEyesInFluid(FluidTags.LAVA)
                || mc.player.isOnLadder()
                || mc.world.getBlockState(mc.player.getPosition()).getMaterial() == Material.WEB) || isBlockAboveHead;

        if (fixSplash) {
            return false;
        }

        if (onlycrit.get()) {
            if (!mc.player.onGround) {
                return isReady && isTrueCrit;
            } else {
                return false;
            }
        }

        return isReady;
    }


/*    private void applyFastFall() {
        if (!mc.player.onGround) {
            mc.player.setMotion(mc.player.getMotion().x, -.1, mc.player.getMotion().z);
        }
    }*/

    private boolean isFilter(LivingEntity entity) {
        if (entity == null || entity instanceof ClientPlayerEntity || !entity.isAlive() ||
                entity.isInvulnerable() || entity instanceof ArmorStandEntity) {
            return false;
        }

        float totalRange = range.get() + preRange.get();
        if (entity.ticksExisted < 3 || mc.player.getDistanceEyePos(entity) > totalRange) {
            return false;
        }

        if (!mc.player.canEntityBeSeen(entity)) {
            if (walls.get()) {
                // allow legacy wall hits
            } else if (wallsAdvanced.get()) {
                if (!canHitThroughExtendedWalls(entity)) {
                    return false;
                }
            } else {
                return false;
            }
        }

        if (entity instanceof PlayerEntity player) {
            if (AntiBot.isBot(player)) {
                return false;
            }

            if (!filter.get("Друзей").get() && Nebulae.getHandler().friends.isFriend(player.getGameProfile().getName())) {
                return false;
            }

            if (player.getGameProfile().getName().equalsIgnoreCase(mc.player.getGameProfile().getName())) {
                return false;
            }

            if (player.isInvisible() && !filter.get("Невидимых").get()) {
                return false;
            }

            boolean hasArmor = false;

            for (ItemStack stack : player.inventory.armorInventory) {
                if (!stack.isEmpty()) {
                    hasArmor = true;
                    break;
                }
            }

            if (hasArmor) {
                if (!filter.get("Игроков").get()) {
                    return false;
                }
            } else {
                if (!filter.get("Голых").get()) {
                    return false;
                }
            }

            return true;
        }

        if (entity instanceof MonsterEntity) {
            return filter.get("Мобов").get();
        }

        if (entity instanceof AnimalEntity) {
            return filter.get("Животных").get();
        }

        if (entity instanceof VillagerEntity) {
            return filter.get("Жителей").get();
        }

        return false;
    }

    private void resetLuckyDayzTracking() {
        luckyDayzSmoothedAim = null;
        luckyDayzYawVelocity = 0;
        luckyDayzPitchVelocity = 0;
        luckyDayzLastDistance = 0;
        luckyDayzSprintReset = false;
    }

    private boolean canHitThroughExtendedWalls(LivingEntity entity) {
        if (mc.player == null || mc.world == null) {
            return false;
        }

        Vector3d eyePos = mc.player.getEyePosition(1.0F);
        AxisAlignedBB targetBox = entity.getBoundingBox().grow(0.2);
        double stepX = Math.max(0.2, (targetBox.maxX - targetBox.minX) / 2.0);
        double stepY = Math.max(0.2, (targetBox.maxY - targetBox.minY) / 2.0);
        double stepZ = Math.max(0.2, (targetBox.maxZ - targetBox.minZ) / 2.0);

        for (double x = targetBox.minX; x <= targetBox.maxX; x += stepX) {
            for (double y = targetBox.minY; y <= targetBox.maxY; y += stepY) {
                for (double z = targetBox.minZ; z <= targetBox.maxZ; z += stepZ) {
                    Vector3d end = new Vector3d(x, y, z);
                    RayTraceContext context = new RayTraceContext(eyePos, end,
                            RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, mc.player);
                    RayTraceResult trace = mc.world.rayTraceBlocks(context);
                    if (trace == null || trace.getType() == RayTraceResult.Type.MISS) {
                        return true;
                    }
                    if (trace.getType() == RayTraceResult.Type.BLOCK && trace instanceof BlockRayTraceResult) {
                        BlockRayTraceResult blockTrace = (BlockRayTraceResult) trace;
                        double blockDist = blockTrace.getHitVec().squareDistanceTo(eyePos);
                        double targetDist = end.squareDistanceTo(eyePos);
                        if (targetDist - blockDist <= 4.5) {
                            BlockPos hitPos = blockTrace.getPos();
                            BlockState state = mc.world.getBlockState(hitPos);
                            if (!state.isNormalCube(mc.world, hitPos) || state.getMaterial().isReplaceable()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    private void resetSnapRotation() {
        if (mc.player != null) {
            mc.player.rotationYawOffset = Integer.MIN_VALUE;
        }
        rotateVector = new Vector2f(mc.player != null ? mc.player.rotationYaw : 0, mc.player != null ? mc.player.rotationPitch : 0);
        resetLuckyDayzTracking();
    }

    private boolean LookTarget(LivingEntity target) {
        if (target == null) {
            return false;
        }

        float allowedFov = fov.get();
        if (rotationType.get("LuckyDayz") && allowedFov >= 360f) {
            return true;
        }

        Vector3d referenceDirection;
        if (rotationType.get("LuckyDayz")) {
            referenceDirection = Vector3d.fromPitchYaw(new Vector2f(rotateVector.y, rotateVector.x)).normalize();
        } else {
            referenceDirection = mc.player.getLook(1.0F).normalize();
        }

        Vector3d toTarget = target.getPositionVec()
                .subtract(mc.player.getEyePosition(1.0F))
                .normalize();

        double dotProduct = referenceDirection.dotProduct(toTarget);
        double angle = Math.toDegrees(Math.acos(MathHelper.clamp(dotProduct, -1.0, 1.0)));
        return angle <= allowedFov;
    }

    private boolean isWithinRotationFov(Vector2f rotation, LivingEntity target) {
        if (target == null) {
            return false;
        }

        Vector3d lookDirection = Vector3d.fromPitchYaw(new Vector2f(rotation.y, rotation.x)).normalize();
        Vector3d toTarget = target.getPositionVec()
                .subtract(mc.player.getEyePosition(1.0F))
                .normalize();

        double dot = lookDirection.dotProduct(toTarget);
        double angle = Math.toDegrees(Math.acos(MathHelper.clamp(dot, -1.0, 1.0)));
        return angle <= fov.get();
    }

    private double getEntityArmor(PlayerEntity entityPlayer2) {
        double d2 = 0.0;
        for (int i2 = 0; i2 < 4; ++i2) {
            ItemStack is = entityPlayer2.inventory.armorInventory.get(i2);
            if (!(is.getItem() instanceof ArmorItem)) continue;
            d2 += getProtectionLvl(is);
        }
        return d2;
    }

    private double getProtectionLvl(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem i) {
            double damageReduceAmount = i.getDamageReduceAmount();
            if (stack.isEnchanted()) {
                damageReduceAmount += (double) EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, stack) * 0.25;
            }
            return damageReduceAmount;
        }
        return 0;
    }

    public static boolean isResetRotationChecks(boolean checkInventory, boolean checkFood) {
        boolean isInventoryOpen = checkInventory && mc.currentScreen instanceof InventoryScreen;
        boolean isUsingFood = checkFood && mc.player.isHandActive() && mc.player.getActiveItemStack().getItem().isFood();
        return isInventoryOpen || isUsingFood;
    }

    private double getEntityHealth(LivingEntity ent) {
        if (ent instanceof PlayerEntity player) {
            return (double) (player.getHealth() + player.getAbsorptionAmount()) * (getEntityArmor(player) / 20.0);
        }
        return ent.getHealth() + ent.getAbsorptionAmount();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.player != null) {
            resetSnapRotation();
            target = null;
            prevTarget = null;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        resetSnapRotation();
        timerUtil.setMs(0);
        target = null;
        prevTarget = null;
        rotateVector.y = mc.player.rotationPitch;
    }

    public float getDistance() {
        if (rotationType.is("Фантайм") || rotationType.is("FunTime New")) {
            return attack36.get() && MoveUtil.isMoving() ? 3.6f : range.get();
        }
        return range.get();
    }

    public LivingEntity getTarget() {
        return target;
    }

    private void ReallyWorldSpeed() {
        if (ticks > 0) {
            handleReallyWorld();
            ticks--;
        } else {
            resetSnapRotation();
        }
    }

    private void FunTimeSpeed() {
        if (ticks > 0) {
            handleFunTime();
            ticks--;
        } else {
            resetSnapRotation();
        }
    }

    private void FunTimeNewSpeed() {
        if (ticks > 0) {
            handleFunTimeNew();
            ticks--;
        } else {
            resetSnapRotation();
        }
    }

    private void LuckyDayzSpeed() {
        if (ticks > 0) {
            handleLuckyDayz();
            ticks--;
        } else {
            resetSnapRotation();
        }
    }

    /* private void SpookyTimeSpeed() {
         handleSpookyTime();
     }*/
    private void HolyWorldSpeed() {
        if (rotationType.get("ХолиВорлд")) {
            handleHolyWorld();
            return;
        }
    }

    private void HvHSpeed() {
        int yawSpeed = fastrotation.get() ? 25 : 100;
        int pitchSpeed = fastrotation.get() ? 25 : 100;
        updateRotation(yawSpeed, pitchSpeed);
    }

    private void handleLuckyDayz() {
        if (target == null || mc.player == null || mc.world == null) {
            return;
        }

        if (!luckyDayzSprintReset && mc.player.connection != null) {
            boolean wasSprinting = mc.player.serverSprintState || mc.player.isSprinting();
            mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
            if (wasSprinting) {
                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_SPRINTING));
                mc.player.setSprinting(true);
            }
            luckyDayzSprintReset = true;
        }

        double smoothing = MathHelper.clamp(luckySmoothing.get(), 0.05f, 1.0f);
        Vector3d basePosition = target.getPositionVec();
        Vector3d motion = new Vector3d(
                target.getPosX() - target.lastTickPosX,
                target.getPosY() - target.lastTickPosY,
                target.getPosZ() - target.lastTickPosZ
        );

        double predictTicks = luckyPrediction.get();
        double distance = mc.player.getDistance(target);

        if (luckyAdaptiveFocus.get()) {
            double distanceFactor = MathHelper.clamp(distance / Math.max(0.1f, range.get()), 0.35, 1.35);
            predictTicks *= distanceFactor;

            if (target.hurtTime > 3) {
                predictTicks *= 0.6;
            }
        }

        Vector3d predicted = basePosition.add(motion.scale(predictTicks));
        AxisAlignedBB box = target.getBoundingBox();
        double yAnchor = luckyAimCenter.get() ? (box.minY + box.maxY) * 0.5 : target.getPosY() + target.getEyeHeight() - 0.15f;

        Vector3d aimPoint = new Vector3d(
                MathHelper.clamp(predicted.x, box.minX, box.maxX),
                MathHelper.clamp(yAnchor, box.minY, box.maxY),
                MathHelper.clamp(predicted.z, box.minZ, box.maxZ)
        );

        double jitterScale = Math.max(0.0025, 0.02 * (1.0 - smoothing));
        aimPoint = aimPoint.add(
                MathUtil.random(-jitterScale, jitterScale),
                MathUtil.random(-jitterScale * 0.75, jitterScale * 0.75),
                MathUtil.random(-jitterScale, jitterScale)
        );

        if (luckyAdaptiveFocus.get()) {
            double verticalBoost = MathHelper.clamp(distance / 6.0, 0.2, 0.8);
            aimPoint = aimPoint.add(0, verticalBoost * (luckyAimCenter.get() ? 0.3 : 0.1), 0);
        }

        if (luckyDayzSmoothedAim == null) {
            luckyDayzSmoothedAim = aimPoint;
        } else {
            luckyDayzSmoothedAim = new Vector3d(
                    MathHelper.lerp(smoothing, luckyDayzSmoothedAim.x, aimPoint.x),
                    MathHelper.lerp(smoothing, luckyDayzSmoothedAim.y, aimPoint.y),
                    MathHelper.lerp(smoothing, luckyDayzSmoothedAim.z, aimPoint.z)
            );
        }

        Vector3d eyePosition = mc.player.getEyePosition(mc.getRenderPartialTicks());
        Vector3d aimVector = luckyDayzSmoothedAim.subtract(eyePosition);
        double horizontal = Math.sqrt(aimVector.x * aimVector.x + aimVector.z * aimVector.z);

        if (horizontal < 1.0E-4) {
            return;
        }

        float desiredYaw = (float) Math.toDegrees(Math.atan2(aimVector.z, aimVector.x)) - 90.0f;
        float desiredPitch = (float) -Math.toDegrees(Math.atan2(aimVector.y, horizontal));

        float yawDelta = MathHelper.wrapDegrees(desiredYaw - rotateVector.x);
        float pitchDelta = MathHelper.wrapDegrees(desiredPitch - rotateVector.y);

        float yawSpeed = luckyYawSpeed.get();
        float pitchSpeed = luckyPitchSpeed.get();

        float yawDeltaAbs = Math.abs(yawDelta);
        float pitchDeltaAbs = Math.abs(pitchDelta);

        if (yawDeltaAbs > yawSpeed) {
            float catchUp = MathHelper.clamp(yawDeltaAbs / 90.0f, 1.0f, 3.5f);
            yawSpeed *= catchUp;
        }

        if (pitchDeltaAbs > pitchSpeed) {
            float catchUp = MathHelper.clamp(pitchDeltaAbs / 60.0f, 1.0f, 3.0f);
            pitchSpeed *= catchUp;
        }

        if (luckyAdaptiveFocus.get()) {
            double deltaDistance = Math.abs(distance - luckyDayzLastDistance);
            double distanceFactor = MathHelper.clamp(0.6 + (distance / Math.max(1.0f, range.get())) * 0.7, 0.6, 1.5);
            yawSpeed *= distanceFactor;
            pitchSpeed *= distanceFactor;

            if (deltaDistance > 0.35) {
                double boost = Math.min(1.35, 1.0 + deltaDistance * 0.45);
                yawSpeed *= boost;
                pitchSpeed *= boost;
            }
        }

        luckyDayzYawVelocity = MathHelper.lerp((float) smoothing, luckyDayzYawVelocity, yawDelta);
        luckyDayzPitchVelocity = MathHelper.lerp((float) smoothing, luckyDayzPitchVelocity, pitchDelta);

        float yawStep = MathHelper.clamp(luckyDayzYawVelocity, -yawSpeed, yawSpeed);
        float pitchStep = MathHelper.clamp(luckyDayzPitchVelocity, -pitchSpeed, pitchSpeed);

        if (Math.abs(yawStep) < 0.05f && yawDeltaAbs > 1.5f) {
            yawStep = Math.copySign(Math.min(yawDeltaAbs, yawSpeed), yawDelta);
        }

        if (Math.abs(pitchStep) < 0.05f && pitchDeltaAbs > 1.5f) {
            pitchStep = Math.copySign(Math.min(pitchDeltaAbs, pitchSpeed), pitchDelta);
        }

        float updatedYaw = rotateVector.x + yawStep;
        float updatedPitch = MathHelper.clamp(rotateVector.y + pitchStep, -90.0f, 90.0f);

        float gcd = SensUtil.getGCDValue();
        updatedYaw -= (updatedYaw - rotateVector.x) % gcd;
        updatedPitch -= (updatedPitch - rotateVector.y) % gcd;

        rotateVector = new Vector2f(updatedYaw, updatedPitch);
        mc.player.rotationYawOffset = rotateVector.x;

        luckyDayzLastDistance = distance;
    }

    public void setTarget(LivingEntity entity) {
        if (this.target != entity) {
            this.prevTarget = this.target;
            this.target = entity;
            resetLuckyDayzTracking();
        }
    }

    public void focus(PlayerEntity player) {
        this.ticks = 1;
    }

    private void handleTest() {
        if (!LookTarget(target)) return;

        if (target != null && mc.player.getDistance(target) <= range.get()) {
            Vector3d vec = target.getPositionVec()
                    .add(0, target.getEyeHeight() - 0.15f, 0)
                    .subtract(mc.player.getEyePosition(mc.getRenderPartialTicks()))
                    .normalize();

            float rawYaw = (float) Math.toDegrees(Math.atan2(-vec.x, vec.z));
            float rawPitch = (float) MathHelper.clamp(-Math.toDegrees(Math.atan2(vec.y, Math.hypot(vec.x, vec.z))), -90, 90);

            float yawDelta = MathHelper.wrapDegrees(rawYaw - mc.player.rotationYaw);
            float pitchDelta = rawPitch - mc.player.rotationPitch;

            float yawSpeed = 360 * new Random().nextFloat(.9F, 1.F);
            float pitchSpeed = 360 * new Random().nextFloat(.9F, 1.F);

            float clampedYaw = MathHelper.clamp(yawDelta, -yawSpeed, yawSpeed);
            float clampedPitch = MathHelper.clamp(pitchDelta, -pitchSpeed, pitchSpeed);

            mc.player.rotationYaw += clampedYaw;
            mc.player.rotationPitch += clampedPitch;

            if (mc.player.getCooledAttackStrength(0.0F) >= 1) {
                if (onlycrit.get() && (mc.player.onGround || mc.player.fallDistance <= 0)) {
                    return;
                }

                if (otzhimshield.get() && target.isBlocking()) {
                    mc.player.connection.sendPacket(new CUseEntityPacket(target, mc.player.isSneaking()));
                } else {
                    mc.playerController.attackEntity(mc.player, target);
                    mc.player.swingArm(Hand.MAIN_HAND);
                }
            }
        }

        double distance = mc.player.getDistance(target);


        if (randomHitboxPoint == null || randomHitboxTicks <= 0) {
            AxisAlignedBB box = target.getBoundingBox();


            double targetSpeed = target.getMotion().length();
            double playerSpeed = mc.player.getMotion().length();

            double[] inputs = new double[] {
                    targetSpeed * 2.0,
                    playerSpeed * 1.5,
                    distance * 0.8,
                    target.getHealth() * 0.5,
                    mc.player.getHealth() * 0.5
            };


            double[] weights = new double[] {0.3, 0.2, 0.25, 0.15, 0.1};
            double maxDist = 0.3;

            double neuralFactor = 0;
            for (int i = 0; i < inputs.length; i++) {
                neuralFactor += inputs[i] * weights[i];
            }

            neuralFactor = Math.max(0.1, Math.min(1.0, neuralFactor));

            if (targetSpeed > 0.5) {
                maxDist *= (1.0 + neuralFactor * 0.5);
            }
            if (distance < 3.0) {
                maxDist *= (0.7 + neuralFactor * 0.3);
            }
            if (target.hurtTime > 0) {
                maxDist *= (1.2 + neuralFactor * 0.2);
            }

            maxDist = Math.max(0.1, Math.min(0.8, maxDist * (1.0 + neuralFactor)));

            double baseX = randomHitboxPoint != null ? randomHitboxPoint.x : (box.minX + box.maxX) / 2.0;
            double baseY = randomHitboxPoint != null ? randomHitboxPoint.y : (box.minY + box.maxY) / 2.0;
            double baseZ = randomHitboxPoint != null ? randomHitboxPoint.z : (box.minZ + box.maxZ) / 2.0;

            double randX = MathUtil.random(
                    Math.max(box.minX, baseX - (box.maxX - box.minX) * maxDist),
                    Math.min(box.maxX, baseX + (box.maxX - box.minX) * maxDist)
            );
            double randY = MathUtil.random(
                    Math.max(box.minY, baseY - (box.maxY - box.minY) * maxDist),
                    Math.min(box.maxY, baseY + (box.maxY - box.minY) * maxDist)
            );
            double randZ = MathUtil.random(
                    Math.max(box.minZ, baseZ - (box.maxZ - box.minZ) * maxDist),
                    Math.min(box.maxZ, baseZ + (box.maxZ - box.minZ) * maxDist)
            );

            targetHitboxPoint = new Vector3d(randX, randY, randZ);
            randomHitboxTicks = 3;
            interpTicks = 0;
            interpT = 0.0;
            springVelocity = new Vector3d(0, 0, 0);
        } else {
            randomHitboxTicks--;
        }

        if (randomHitboxPoint == null) {
            randomHitboxPoint = targetHitboxPoint != null ? targetHitboxPoint : target.getPositionVec();
        } else if (targetHitboxPoint != null) {
            interpTicks = Math.min(interpTicks + 1, 3);
            interpT = (double)interpTicks / 3;
            double t = interpT * MathUtil.random(0.8, 1.2);

            Vector3d next;
            if (interpolationType.get("Линейная")) {
                next = new Vector3d(
                        randomHitboxPoint.x + (targetHitboxPoint.x - randomHitboxPoint.x) * t,
                        randomHitboxPoint.y + (targetHitboxPoint.y - randomHitboxPoint.y) * t,
                        randomHitboxPoint.z + (targetHitboxPoint.z - randomHitboxPoint.z) * t
                );
            } else if (interpolationType.get("Квадратичная")) {
                double quadT = t * t;
                next = new Vector3d(
                        randomHitboxPoint.x + (targetHitboxPoint.x - randomHitboxPoint.x) * quadT,
                        randomHitboxPoint.y + (targetHitboxPoint.y - randomHitboxPoint.y) * quadT,
                        randomHitboxPoint.z + (targetHitboxPoint.z - randomHitboxPoint.z) * quadT
                );
            } else if (interpolationType.get("Кубическая")) {
                double cubicT = t * t * t;
                next = new Vector3d(
                        randomHitboxPoint.x + (targetHitboxPoint.x - randomHitboxPoint.x) * cubicT,
                        randomHitboxPoint.y + (targetHitboxPoint.y - randomHitboxPoint.y) * cubicT,
                        randomHitboxPoint.z + (targetHitboxPoint.z - randomHitboxPoint.z) * cubicT
                );
            } else if (interpolationType.get("Плавный шаг")) {
                double smoothT = t * t * (3 - 2 * t);
                next = new Vector3d(
                        randomHitboxPoint.x + (targetHitboxPoint.x - randomHitboxPoint.x) * smoothT,
                        randomHitboxPoint.y + (targetHitboxPoint.y - randomHitboxPoint.y) * smoothT,
                        randomHitboxPoint.z + (targetHitboxPoint.z - randomHitboxPoint.z) * smoothT
                );
            } else if (interpolationType.get("Пружина")) {
                double stiffness = 0.2, damping = 0.7;
                springVelocity = new Vector3d(
                        (springVelocity.x + (targetHitboxPoint.x - randomHitboxPoint.x) * stiffness) * damping,
                        (springVelocity.y + (targetHitboxPoint.y - randomHitboxPoint.y) * stiffness) * damping,
                        (springVelocity.z + (targetHitboxPoint.z - randomHitboxPoint.z) * stiffness) * damping
                );
                next = new Vector3d(
                        randomHitboxPoint.x + springVelocity.x,
                        randomHitboxPoint.y + springVelocity.y,
                        randomHitboxPoint.z + springVelocity.z
                );
            } else {
                next = new Vector3d(
                        randomHitboxPoint.x + (targetHitboxPoint.x - randomHitboxPoint.x) * t,
                        randomHitboxPoint.y + (targetHitboxPoint.y - randomHitboxPoint.y) * t,
                        randomHitboxPoint.z + (targetHitboxPoint.z - randomHitboxPoint.z) * t
                );
            }

            next = new Vector3d(
                    next.x + MathUtil.random(-0.02, 0.02),
                    next.y + MathUtil.random(-0.02, 0.02),
                    next.z + MathUtil.random(-0.02, 0.02)
            );

            randomHitboxPoint = next;
        }

        Vector3d eyePos = mc.player.getEyePosition(1.0F);
        Vector3d targetVec = randomHitboxPoint.subtract(eyePos);

        double horizontalDistance = Math.sqrt(targetVec.x * targetVec.x + targetVec.z * targetVec.z);
        float targetYaw = (float) Math.toDegrees(Math.atan2(targetVec.z, targetVec.x)) - 90F;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(targetVec.y, horizontalDistance));

        float currentYaw = rotateVector.x;
        float currentPitch = rotateVector.y;

        float yawDelta = wrapDegrees(targetYaw - currentYaw);
        float pitchDelta = wrapDegrees(targetPitch - currentPitch);

        float baseSpeed = 77.5f;

        float distanceFactor = (float) Math.max(0.75, Math.min(2.25, distance / 3.0));
        baseSpeed *= distanceFactor;

        float yawProgress = 1.0f - Math.min(1.0f, Math.abs(yawDelta) / 180.0f);
        float pitchProgress = 1.0f - Math.min(1.0f, Math.abs(pitchDelta) / 90.0f);
        float progressMultiplier = 0.5f + ((yawProgress + pitchProgress) / 2.0f) * 0.5f;


        float yawSpeed = baseSpeed * (1.0f - yawProgress * 0.7f);
        float pitchSpeed = baseSpeed * (1.0f - pitchProgress * 0.7f);


        yawSpeed = Math.min(yawSpeed, 97.5f);
        pitchSpeed = Math.min(pitchSpeed, 92.5f);


        if (Math.abs(yawDelta) > 0.1) {
            float yawMove = Math.min(Math.abs(yawDelta), yawSpeed) * Math.signum(yawDelta);
            currentYaw = wrapDegrees(currentYaw + yawMove);
        }

        if (Math.abs(pitchDelta) > 0.1) {
            float pitchMove = Math.min(Math.abs(pitchDelta), pitchSpeed) * Math.signum(pitchDelta);
            currentPitch = MathHelper.clamp(currentPitch + pitchMove, -90.0F, 90.0F);
        }


        float gcd = (float) (mc.gameSettings.mouseSensitivity * 0.6F + 0.2F);
        gcd = (float) (gcd * gcd * gcd * 8.0F);


        currentYaw = Math.round(currentYaw / gcd) * gcd;
        currentPitch = Math.round(currentPitch / gcd) * gcd;


        float noiseAmount = 0.1f;
        if (Math.abs(yawDelta) > 1.0 || Math.abs(pitchDelta) > 1.0) {
            currentYaw += MathUtil.random(-noiseAmount, noiseAmount);
            currentPitch += MathUtil.random(-noiseAmount, noiseAmount);
        }


        rotateVector = new Vector2f(currentYaw, currentPitch);
    }
}