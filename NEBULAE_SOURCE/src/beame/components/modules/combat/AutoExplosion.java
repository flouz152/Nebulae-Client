package beame.components.modules.combat;

import beame.util.math.MathUtil;
import beame.util.math.TimerUtil;
import beame.util.other.MoveUtil;
import beame.util.player.InventoryUtility;

import events.Event;
import events.impl.player.EventInput;
import events.impl.player.EventMotion;
import events.impl.player.EventPlace;
import events.impl.player.EventUpdate;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.AirItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.EnumSetting;
import beame.setting.SettingList.SliderSetting;

import java.util.List;
import java.util.stream.Collectors;

public class AutoExplosion extends Module {
// leaked by itskekoff; discord.gg/sk3d di2J9mVM

    public final EnumSetting options = new EnumSetting("Опции",
            new BooleanSetting("Не взрывать себя", true),
            new BooleanSetting("Коррекция движения", true)
    );

    private final SliderSetting delayAttack = new SliderSetting("Задержка", 5, 0, 10, 1);
    private Entity crystalEntity = null;
    private BlockPos obsidianPos = null;
    private int oldCurrentSlot = -1;
    public Vector2f rotationVector = new Vector2f(0, 0);
    TimerUtil attackTimerUtility = new TimerUtil();
    int bestSlot = -1;
    int oldSlot = -1;

    public boolean check() {
        return rotationVector != null && options.get("Коррекция движения").get() && crystalEntity != null && obsidianPos != null && isState();
    }

    public AutoExplosion() {
        super("AutoExplosion", Category.Combat, true,"Автоматически ставит кристалл на обсидиан");
        addSettings(options, delayAttack);
    }

    @Override
    public void event(Event event) {
        if(event instanceof EventInput eventInput) {
            if (check()) {
                MoveUtil.fixMovement(eventInput, rotationVector.x);
            }
        }
        if(event instanceof EventPlace eventObsidianPlace) {
            BlockPos obsidianPos = eventObsidianPlace.getPos();

            boolean isOffHand = Minecraft.getInstance().player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;

            int slotInInventory = getItem(Items.END_CRYSTAL, false);
            int slotInHotBar = getItem(Items.END_CRYSTAL, true);
            bestSlot = InventoryUtility.getInstance().findBestSlotInHotBar();
            boolean slotNotNull = Minecraft.getInstance().player.inventory.getStackInSlot(bestSlot).getItem() != Items.AIR;

            if (isOffHand) {
                if (obsidianPos != null) {
                    setAndUseCrystal(bestSlot, obsidianPos);
                    this.obsidianPos = obsidianPos;
                }
            }

            if (slotInHotBar == -1 && slotInInventory != -1 && bestSlot != -1) {
                InventoryUtility.moveItem2(slotInInventory,bestSlot + 36, slotNotNull);
                if (slotNotNull && oldSlot == -1) {
                    oldSlot = slotInInventory;
                }
                if (obsidianPos != null) {
                    oldCurrentSlot = Minecraft.getInstance().player.inventory.currentItem;
                    setAndUseCrystal(bestSlot, obsidianPos);
                    Minecraft.getInstance().player.inventory.currentItem = oldCurrentSlot;
                    this.obsidianPos = obsidianPos;
                }
                Minecraft.getInstance().playerController.windowClick(0, oldSlot, 0, ClickType.PICKUP, Minecraft.getInstance().player);
                Minecraft.getInstance().playerController.windowClick(0, bestSlot + 36, 0, ClickType.PICKUP, Minecraft.getInstance().player);
                Minecraft.getInstance().playerController.windowClickFixed(0, oldSlot, 0, ClickType.PICKUP, Minecraft.getInstance().player, 250);
            } else if (slotInHotBar != -1) {
                if (obsidianPos != null) {
                    oldCurrentSlot = Minecraft.getInstance().player.inventory.currentItem;
                    setAndUseCrystal(slotInHotBar, obsidianPos);
                    Minecraft.getInstance().player.inventory.currentItem = oldCurrentSlot;
                    this.obsidianPos = obsidianPos;
                }
            }
        }
        if(event instanceof EventUpdate) {
            if (obsidianPos != null)
                findEnderCrystals(obsidianPos).forEach(this::attackCrystal);

            if (crystalEntity != null)
                if (!crystalEntity.isAlive()) reset();
        }
        if (event instanceof EventMotion e) {
            if (isValid(crystalEntity)) {
                rotationVector = MathUtil.rotationToEntity(crystalEntity);
                e.setYaw(rotationVector.x);
                e.setPitch(rotationVector.y);
                Minecraft.getInstance().player.renderYawOffset = rotationVector.x;
                Minecraft.getInstance().player.rotationYawHead = rotationVector.x;
                Minecraft.getInstance().player.rotationPitchHead = rotationVector.y;

                if (options.get("Коррекция движения").get()) {
                    Minecraft.getInstance().player.rotationYawOffset = rotationVector.x;
                }
            } else {
                if (options.get("Коррекция движения").get()) {
                    Minecraft.getInstance().player.rotationYawOffset = Integer.MIN_VALUE;
                }
            }
        }
    }


    @Override
    public void onDisable() {
        reset();
        super.onDisable();

        if (options.get("Коррекция движения").get()) {
            mc.player.rotationYawOffset = Integer.MIN_VALUE;
        }
    }

    private void attackCrystal(Entity entity) {
        if (isValid(entity) && mc.player.getCooledAttackStrength(1.0f) >= 1.0f && attackTimerUtility.hasTimeElapsed()) {
            long delay = delayAttack.get().longValue() * 100;
            attackTimerUtility.setMs(delay);
            mc.playerController.attackEntity(mc.player, entity);
            mc.player.swingArm(Hand.MAIN_HAND);
            crystalEntity = entity;
        }
        if (!entity.isAlive()) {
            reset();
        }
    }

    private int getItem(Item input, boolean inHotBar) {
        int firstSlot = inHotBar ? 0 : 9;
        int lastSlot = inHotBar ? 9 : 36;
        for (int i = firstSlot; i < lastSlot; i++) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);

            if (itemStack.getItem() instanceof AirItem) {
                continue;
            }
            if (itemStack.getItem() == input) {
                return i;
            }
        }
        return -1;

    }

    private void setAndUseCrystal(int slot, BlockPos pos) {
        boolean isOffHand = mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;

        Vector3d center = new Vector3d(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f);
        if (!isOffHand)
            mc.player.inventory.currentItem = slot;


        Hand hand = isOffHand ? Hand.OFF_HAND : Hand.MAIN_HAND;

        if (mc.playerController.func_217292_a(
                mc.player, mc.world, hand,
                new BlockRayTraceResult(center, Direction.UP, pos, false)) == ActionResultType.SUCCESS) {
            mc.player.swingArm(Hand.MAIN_HAND);
        }
    }

    private boolean isValid(Entity base) {
        if (base == null) {
            return false;
        }
        if (obsidianPos == null) {
            return false;
        }
        if (options.get("Не взрывать себя").get() && mc.player.getPosY() > obsidianPos.getY()) {
            return false;
        }
        return isCorrectDistance();
    }

    private boolean isCorrectDistance() {
        if (obsidianPos == null) {
            return false;
        }
        return mc.player.getPositionVec().distanceTo(
                new Vector3d(obsidianPos.getX(),
                        obsidianPos.getY(),
                        obsidianPos.getZ())) <= mc.playerController.getBlockReachDistance();
    }

    public List<Entity> findEnderCrystals(BlockPos position) {
        return mc.world.getEntitiesWithinAABBExcludingEntity(null,
                        new AxisAlignedBB(
                                position.getX(),
                                position.getY(),
                                position.getZ(),
                                position.getX() + 1.0,
                                position.getY() + 2.0,
                                position.getZ() + 1.0))
                .stream()
                .filter(entity -> entity instanceof EnderCrystalEntity)
                .collect(Collectors.toList());
    }

    private void reset() {
        crystalEntity = null;
        obsidianPos = null;
        rotationVector = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
        oldCurrentSlot = -1;
        bestSlot = -1;
    }
}
