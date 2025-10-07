package beame.components.modules.combat;
import beame.util.math.TimerUtil2;
import beame.util.player.InventoryUtility;

import events.impl.render.Render2DEvent;
import net.minecraft.client.MainWindow;
import beame.Essence;
import com.mojang.blaze3d.platform.GlStateManager;
import events.Event;
import events.impl.player.EventUpdate;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.EnumSetting;
import beame.setting.SettingList.SliderSetting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AutoTotem extends Module {
// leaked by itskekoff; discord.gg/sk3d 1I9db2nz
    public AutoTotem() {
        super("AutoTotem", Category.Combat, true, "Автоматически берет тотем бессмертия в левую руку");
        addSettings(health, drawCounter, swapBack, saveEnchanted, noBallSwitch, mode, obsidianRadius, crystalRadius, anchorRadius);
    }

    private final SliderSetting health = new SliderSetting("Здоровье", 4F, 1F, 20F, 0.5F);
    private final BooleanSetting drawCounter = new BooleanSetting("Отображать кол-во", true, 0);
    private final BooleanSetting swapBack = new BooleanSetting("Возвращать предмет", true, 0);
    private final BooleanSetting saveEnchanted = new BooleanSetting( "Сохранять зачарованный", true);
    private final BooleanSetting noBallSwitch = new BooleanSetting("Не брать если шар", false, 0);

    private final EnumSetting mode = new EnumSetting("Учитывать",
            new BooleanSetting("Поглощение", true),
            new BooleanSetting("Обсидиан", false),
            new BooleanSetting("Кристалл", true),
            new BooleanSetting("Якорь", true),
            new BooleanSetting("Падение", true)
    );
    private int nonEnchantedTotems;
    private int totemCount = 0;

    private final SliderSetting obsidianRadius = new SliderSetting("Радиус от обсы", 6, 1, 8, 1).setVisible(() -> mode.get("Обсидиан").get());
    private final SliderSetting crystalRadius = new SliderSetting("Радиус от кристалла", 6, 1, 8, 1).setVisible(() -> mode.get("Кристалл").get());
    private final SliderSetting anchorRadius = new SliderSetting("Радиус от якоря", 6, 1, 8, 1).setVisible(() -> mode.get("Якорь").get());
    private int oldItem = -1;
    private ItemStack oldItemStack = ItemStack.EMPTY;
    private final TimerUtil2 timerUtil2 = new TimerUtil2();
    private boolean isBlocking = false;
    private final TimerUtil2 blockTimer = new TimerUtil2();
    private final ItemStack stack = new ItemStack(Items.TOTEM_OF_UNDYING);

    @Override
    public void event(Event event) {
        if (event instanceof EventUpdate) {
            totemCount = InventoryUtility.getInventoryCount(Items.TOTEM_OF_UNDYING);
            nonEnchantedTotems = (int) mc.player.openContainer.inventorySlots.stream().filter(s -> s.getStack().getItem().equals(Items.TOTEM_OF_UNDYING) && !s.getStack().isEnchanted()).count();
            Slot slot = mc.player.openContainer.inventorySlots.stream().filter(s -> s.getStack().getItem().equals(Items.TOTEM_OF_UNDYING) && this.isNotSaveEnchanted(s.getStack())).findFirst().orElse(null);

            if (timerUtil2.isReached(400)) {
                if (condition()) {
                    if (slot != null && !isTotemInHands()) {
                        if (!mc.player.getHeldItemOffhand().isEmpty() && oldItem == -1) {
                            oldItem = slot.slotNumber;
                            oldItemStack = mc.player.getHeldItemOffhand().copy();
                        }
                        handleSwap(slot.slotNumber, true);
                        timerUtil2.reset();
                    }
                } else if (oldItem != -1 && swapBack.get()) {
                    Slot returnSlot = mc.player.openContainer.inventorySlots.get(oldItem);
                    if (returnSlot != null && !returnSlot.getStack().isEmpty() && 
                        returnSlot.getStack().getItem() == oldItemStack.getItem()) {
                        handleSwap(oldItem, false);
                        timerUtil2.reset();
                    } else {
                        oldItem = -1;
                        oldItemStack = ItemStack.EMPTY;
                    }
                }
            }
        } else if (event instanceof Render2DEvent) {
            if (!drawCounter.get())
                return;
            {
                MainWindow window = mc.getMainWindow();
                float x = window.getScaledWidth() / 2 + 120f;
                float y = window.getScaledHeight() - 17;

                int totemX = (int)(x - 128);
                int totemY = (int)(y - 35);

                GlStateManager.pushMatrix();
                GlStateManager.disableBlend();
                mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, totemX, totemY);
                GlStateManager.popMatrix();

                GlStateManager.pushMatrix();
                GlStateManager.translated(0.0F, 0.0F, 200.0F);
                String count = String.valueOf(getTotemCount());
                mc.fontRenderer.drawStringWithShadow(((Render2DEvent) event).getMatrix(), count, totemX + 16.5f - mc.fontRenderer.getStringWidth(count), totemY + 8, 0xFFFFFF);
                GlStateManager.popMatrix();
            }
        }
    }

    private void handleSwap(int slotNumber, boolean isToTotem) {
        boolean curgui = Essence.getHandler().getModuleList().guiMove.isState();
        boolean shouldDisableMove = Essence.getHandler().getModuleList().guiMove.funtime.get();
        KeyBinding[] pressedKeys = {
            mc.gameSettings.keyBindForward,
            mc.gameSettings.keyBindBack,
            mc.gameSettings.keyBindLeft,
            mc.gameSettings.keyBindRight,
            mc.gameSettings.keyBindJump,
            mc.gameSettings.keyBindSprint
        };

        for (KeyBinding keyBinding : pressedKeys) {
            keyBinding.setPressed(false);
        }

        if (shouldDisableMove) {
            Essence.getHandler().disableMove = true;
        }

        new Thread(() -> {
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (mc.player != null && mc.currentScreen == null) {
                InventoryUtility.swapHands(slotNumber, Hand.OFF_HAND, false);
                
                if (!isToTotem) {
                    oldItem = -1;
                    oldItemStack = ItemStack.EMPTY;
                }
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (mc.player != null) {
                for (KeyBinding keyBinding : pressedKeys) {
                    boolean press = InputMappings.isKeyDown(mc.getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode());
                    keyBinding.setPressed(press);
                }
            }
            
            if (curgui) Essence.getHandler().getModuleList().guiMove.setState(true);
            if (shouldDisableMove) Essence.getHandler().disableMove = false;
        }).start();
    }

    private boolean condition() {
        float health = mc.player.getHealth();
        if (mode.get("Поглощение").get()) {
            health += mc.player.getAbsorptionAmount();
        }

        if (this.health.get().floatValue() >= health) {
            return true;
        }

        if (!isBall()) {
            for (Entity entity : mc.world.getAllEntities()) {
                if (mode.get("Кристалл").get()) {
                    if (entity instanceof EnderCrystalEntity && mc.player.getDistanceSq(entity) <= crystalRadius.get().floatValue()) {
                        return true;
                    }
                }
            }

            if (mode.get("Якорь").get()) {
                BlockPos pos = getSphere(mc.player.getPosition(), obsidianRadius.get().floatValue(), 6, false, true, 0).stream().filter(this::IsValidBlockPosAnchor).min(Comparator.comparing(blockPos -> getDistanceToBlock(mc.player, blockPos))).orElse(null);
                return pos != null;
            }

            if (mode.get("Обсидиан").get()) {
                BlockPos pos = getSphere(mc.player.getPosition(), anchorRadius.get().floatValue(), 6, false, true, 0).stream().filter(this::IsValidBlockPosObisdian).min(Comparator.comparing(blockPos -> getDistanceToBlock(mc.player, blockPos))).orElse(null);
                return pos != null;
            }
            if (mode.get("Падение").get()) {
                return mc.player.fallDistance >= 30;
            }
        }

        return false;
    }

    public boolean isBall() {
        if (!noBallSwitch.get()) {
            return false;
        }
        ItemStack stack = mc.player.getHeldItemOffhand();
        return stack.getDisplayName().getString().toLowerCase().contains("шар") || stack.getDisplayName().getString().toLowerCase().contains("голова") || stack.getDisplayName().getString().toLowerCase().contains("head");
    }

    private int getTotemCount() {
        int count = 0;

        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                count++;
            }
        }
        if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            count++;
        }
        return count;
    }

    private boolean isTotemInHands() {
        Hand[] hands = Hand.values();

        for (Hand hand : hands) {
            ItemStack heldItem = mc.player.getHeldItem(hand);
            if (heldItem.getItem() == Items.TOTEM_OF_UNDYING && this.isNotSaveEnchanted(heldItem)) {
                return true;
            }
        }

        return false;
    }

    private boolean IsValidBlockPosObisdian(BlockPos pos) {
        BlockState state = mc.world.getBlockState(pos);
        return state.getBlock().equals(Blocks.OBSIDIAN);
    }

    private boolean IsValidBlockPosAnchor(BlockPos pos) {
        BlockState state = mc.world.getBlockState(pos);
        return state.getBlock().equals(Blocks.RESPAWN_ANCHOR);
    }
    private boolean isNotSaveEnchanted(ItemStack itemStack) {
        return !this.saveEnchanted.get() || !itemStack.isEnchanted() || this.nonEnchantedTotems <= 0;
    }

    private List<BlockPos> getSphere(final BlockPos blockPos, final float radius, final int height, final boolean hollow, final boolean semiHollow, final int yOffset) {
        final ArrayList<BlockPos> spherePositions = new ArrayList<>();
        final int x = blockPos.getX();
        final int y = blockPos.getY();
        final int z = blockPos.getZ();
        final int minX = x - (int) radius;
        final int maxX = x + (int) radius;
        final int minZ = z - (int) radius;
        final int maxZ = z + (int) radius;

        for (int xPos = minX; xPos <= maxX; ++xPos) {
            for (int zPos = minZ; zPos <= maxZ; ++zPos) {
                final int minY = semiHollow ? (y - (int) radius) : y;
                final int maxY = semiHollow ? (y + (int) radius) : (y + height);
                for (int yPos = minY; yPos < maxY; ++yPos) {
                    final double distance = (x - xPos) * (x - xPos) + (z - zPos) * (z - zPos) + (semiHollow ? ((y - yPos) * (y - yPos)) : 0);
                    if (distance < radius * radius && (!hollow || distance >= (radius - 1.0f) * (radius - 1.0f))) {
                        spherePositions.add(new BlockPos(xPos, yPos + yOffset, zPos));
                    }
                }
            }
        }
        return spherePositions;
    }

    private double getDistanceToBlock(Entity entity, final BlockPos blockPos) {
        return getDistance(entity.getPosX(), entity.getPosY(), entity.getPosZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    private double getDistance(final double x1, final double y1, final double z1, final double x2, final double y2, final double z2) {
        final double x = x1 - x2;
        final double y = y1 - y2;
        final double z = z1 - z2;
        return MathHelper.sqrt(x * x + y * y + z * z);
    }
}