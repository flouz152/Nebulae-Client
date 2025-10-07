package beame.util.player;

import beame.Nebulae;
import beame.components.modules.combat.Aura;
import beame.util.math.MathUtil;
import beame.util.other.MoveUtil;
import events.Event;
import events.impl.player.*;
import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static beame.util.ClientHelper.isConnectedToServer;
import static beame.util.IMinecraft.mc;
@UtilityClass
public class PlayerUtil {
// leaked by itskekoff; discord.gg/sk3d usRMiyu4
    public static int findItemSlot(Item item) {
        return findItemSlot(item, true);
    }
    public static boolean nullCheck() {
        return mc.player == null || mc.world == null;
    }

    public final Pattern NAME_REGEX = Pattern.compile("^[A-zА-я0-9_]{3,16}$");

    public boolean isInvalidName(String name) {
        return !NAME_REGEX.matcher(name).matches();
    }

    public static int findItemSlot(Item item, boolean armor) {
        if (armor) {
            for (ItemStack stack : mc.player.getArmorInventoryList()) {
                if (stack.getItem() == item) {
                    return -2;
                }
            }
        }
        int slot = -1;
        for (int i = 0; i < 36; i++) {
            ItemStack s = mc.player.inventory.getStackInSlot(i);
            if (s.getItem() == item) {
                slot = i;
                break;
            }
        }
        if (slot < 9 && slot != -1) {
            slot = slot + 36;
        }
        return slot;
    }
    public static boolean isHoly() {
        if (nullCheck()) return false;
        if (mc.player.getServerBrand() == null) return false;
        return isConnectedToServer("holyworld") && mc.player.getServerBrand().contains("HolyWorld");
    }

    public static void moveItem(int from, int to, boolean air) {
        moveItem(0, from, to, air);
    }

    public static void moveItem(int windowId, int from, int to, boolean air) {
        if (from == to) return;
        pickupItem(windowId, from, 0);
        pickupItem(windowId, to, 0);
        if (air)
            pickupItem(windowId, from, 0);
    }

    public Item getChest() {
        return mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem();
    }

    public ItemStack find(int slot) {
        return mc.player.inventory.getStackInSlot(slot);
    }
    public boolean collideWith(LivingEntity entity) {
        return collideWith(entity, 0);
    }

    public boolean collideWith(LivingEntity entity, float grow) {
        AxisAlignedBB box = mc.player.getBoundingBox();
        AxisAlignedBB targetbox = entity.getBoundingBox().grow(grow, 0, grow); //.expand(-0.1f, 0, -0.1f);

        if (box.maxX > targetbox.minX
                && box.maxY > targetbox.minY
                && box.maxZ > targetbox.minZ
                && box.minX < targetbox.maxX
                && box.minY < targetbox.maxY
                && box.minZ < targetbox.maxZ) return true;

        return false;
    }

    public void send(float yaw, float pitch) {
        send(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), yaw, pitch, mc.player.isOnGround());
    }

    public void send(double x, double y, double z, float yaw, float pitch, boolean ground) {
        mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(x,y,z,yaw,pitch,ground));
        mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
    }



    public static void pickupItem(int slot, int button) {
        pickupItem(0, slot, button);
    }

    public static void pickupItem(int windowId, int slot, int button) {
        mc.playerController.windowClick(windowId, slot, button, ClickType.PICKUP, mc.player);
    }

    public List<BlockPos> getCube(final BlockPos center, final float radiusXZ, final float radiusY) {
        List<BlockPos> positions = new ArrayList<>();
        int centerX = center.getX();
        int centerY = center.getY();
        int centerZ = center.getZ();

        for (int x = centerX - (int) radiusXZ; x <= centerX + radiusXZ; x++) {
            for (int z = centerZ - (int) radiusXZ; z <= centerZ + radiusXZ; z++) {
                for (int y = centerY - (int) radiusY; y < centerY + radiusY; y++) {
                    positions.add(new BlockPos(x, y, z));
                }
            }
        }

        return positions;
    }

    public void moveItemOld(int one, int two, boolean swap) {
        mc.playerController.windowClick(0, one, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, two, 0, ClickType.PICKUP, mc.player);
        if (swap) {
            mc.playerController.windowClick(0, one, 0, ClickType.PICKUP, mc.player);
        }
    }

    public boolean isInGame() {
        return mc.world != null && mc.player != null;
    }

    public Block getBlock() {
        return getBlock(0, 0, 0);
    }

    public Block getBlock(double x, double y, double z) {
        return !PlayerUtil.isInGame() ? Blocks.AIR : mc.world.getBlockState(mc.player.getPosition().add(x, y, z)).getBlock();
    }

    public int findItem(Item input) {
        return IntStream.range(0, 9).filter(i -> mc.player.inventory.getStackInSlot(i).getItem() == input).findFirst()
                .orElse(-1);
    }

    public int findItemDefault(final int endSlot, final Item ofType) {
        int slot = -1;

        for (int i = 0; i < endSlot; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() != ofType) continue;
            slot = i;
        }

        return slot;
    }

    public int findItem(final int endSlot, final Item ofType) {
        int slot = -1;

        for (int i = 0; i < endSlot; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() != ofType) continue;
            slot = i == 40 ? 45 : i < 9 ? 36 + i : i;
        }

        return slot;
    }

    public int findItem(int maxSlots, Item item, int startSlot) {
        for (int i = startSlot; i < maxSlots; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() == item) {
                return i == 40 ? 45 : i < 9 ? 36 + i : i;
            }
        }
        return -1;
    }



    public void look(Event event, float yaw, float pitch, boolean visual) {
        look(event, yaw, pitch, visual, 2, yaw);
    }

    public void look(Event event, float yaw, float pitch, boolean visual, int correction, float visualYaw) {
        if (event instanceof EventTrace eventTrace) {
            eventTrace.setYaw(yaw);
            eventTrace.setPitch(pitch);
            eventTrace.cancel();
        }
        if (event instanceof EventMotion eventMotion) {
            Aura aura = Nebulae.getHandler().getModuleList().aura;
            //aura.rotation().x = yaw;
            //aura.rotation().y = pitch;
            eventMotion.setYaw(yaw);
            eventMotion.setPitch(pitch);
            if (visual) {
                mc.player.renderYawOffset = visualYaw;
                mc.player.rotationYawHead = visualYaw;
                mc.player.rotationPitchHead = pitch;
            }
        }
    }

        public static void dropItem(int slot) {
        mc.playerController.windowClick(0, slot, 0, ClickType.THROW, mc.player);
    }

    public static int getFireworkSlot() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() instanceof FireworkRocketItem) {
                return i;
            }
        }
        return -1;
    }

    public boolean isBlockSolid(final double x, final double y, final double z) {
        return PlayerUtil.block(new BlockPos(x, y, z)).getDefaultState().getMaterial().isSolid();
    }

    public Block block(final BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock();
    }

    public static double getBps(Entity entity, int decimal) {
        double x = entity.getPosX() - entity.prevPosX;
        double z = entity.getPosZ() - entity.prevPosZ;
        double speed = Math.sqrt(x * x + z * z) * 20.0D;
        return MathUtil.round(speed, decimal == 0 ? 0.05f : decimal);
    }

    private List<BlockPos> getNearbyBlockPositions(BlockPos center) {
        List<BlockPos> positions = new ArrayList<>();
        for (int x = center.getX() - 2; x <= center.getX() + 2; x++) {
            for (int y = center.getY() - 1; y <= center.getY() + 4; y++) {
                for (int z = center.getZ() - 2; z <= center.getZ() + 2; z++) {
                    positions.add(new BlockPos(x, y, z));
                }
            }
        }
        return positions;
    }

    private boolean isBlockCobweb(AxisAlignedBB playerBox, BlockPos blockPos) {
        return playerBox.intersects(new AxisAlignedBB(blockPos)) && mc.world.getBlockState(blockPos).getBlock() == Blocks.COBWEB;
    }

    public static boolean isPlayerInWeb() {
        if (mc.player == null) return false;
        
        for (double x = -0.31; x <= 0.31; x += 0.31) {
            for (double z = -0.31; z <= 0.31; z += 0.31) {
                for (double y = mc.player.getEyeHeight(); y >= 0.0; y -= 0.1) {
                    BlockPos pos = new BlockPos(mc.player.getPosX() + x, mc.player.getPosY() + y, mc.player.getPosZ() + z);
                    if (mc.world != null && mc.world.getBlockState(pos).getBlock() == Blocks.COBWEB) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isBlockAboveHead() {
        if (nullCheck()) return false;
        float width = mc.player.getWidth() / 2F;
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(
                mc.player.getPosX() - width, mc.player.getPosY() + mc.player.getEyeHeight(), mc.player.getPosZ() + width,
                mc.player.getPosX() + width, mc.player.getPosY() + (!mc.player.isOnGround() ? 1.5 : 2.5), mc.player.getPosZ() - width
        );
        return mc.world.getCollisionShapes(mc.player, axisAlignedBB).findAny().isEmpty();
    }

    public static boolean isBlockUnder(final double height) {
        for (int offset = 0; offset < height; offset++) {
            if (blockRelativeToPlayer(0, -offset, 0).getDefaultState().isCollisionShapeLargerThanFullBlock()) {
                return true;
            }
        }
        return false;
    }

    public static Block blockRelativeToPlayer(final double offsetX, final double offsetY, final double offsetZ) {
        return mc.world.getBlockState(new BlockPos(mc.player.getPositionVec()).add(offsetX, offsetY, offsetZ)).getBlock();
    }

    public static Block blockAheadOfPlayer(final double offsetXZ, final double offsetY) {
        return blockRelativeToPlayer(-Math.sin(MoveUtil.direction()) * offsetXZ, offsetY, Math.cos(MoveUtil.direction()) * offsetXZ);
    }

    public static double getEntityArmor(PlayerEntity target) {
        double totalArmor = 0.0;

        for (ItemStack armorStack : target.inventory.armorInventory) {
            if (armorStack != null && armorStack.getItem() instanceof ArmorItem) {
                totalArmor += getProtectionLvl(armorStack);
            }
        }
        return totalArmor;
    }

    public static double getEntityHealth(Entity ent) {
        if (ent instanceof PlayerEntity player) {
            double armorValue = getEntityArmor(player) / 20.0;
            return (player.getHealth() + player.getAbsorptionAmount()) * armorValue;
        } else if (ent instanceof LivingEntity livingEntity) {
            return livingEntity.getHealth() + livingEntity.getAbsorptionAmount();
        }
        return 0.0;
    }


    public static double getProtectionLvl(ItemStack stack) {
        ArmorItem armor = (ArmorItem) stack.getItem();
        double damageReduce = armor.getDamageReduceAmount();
        if (stack.isEnchanted()) {
            damageReduce += (double) EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, stack) * 0.25;
        }
        return damageReduce;
    }

    public static int getPearls() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() instanceof EnderPearlItem) {
                return i;
            }
        }
        return -1;
    }

    public static int getPing() {
        return mc.getConnection() == null || mc.getConnection().getPlayerInfo(mc.player.getUniqueID()) == null ? -1 : mc.getConnection().getPlayerInfo(mc.player.getUniqueID()).getResponseTime();
    }

    public static float squaredDistance2d(double x, double z) {
        if (mc.player == null)
            return 0.0f;
        double d = mc.player.getPosX() - x;
        double f = mc.player.getPosZ() - z;
        return (float) (d * d + f * f);
    }

    public static boolean chestContainerOpened() {
        return mc.currentScreen instanceof ChestScreen;
    }

    public static long delay() {
        return (long) (Math.random() * (20));
    }

    public static void rsleep() throws InterruptedException {
        Thread.sleep(delay());
    }

    public boolean isInView(AxisAlignedBB box) {
        if (mc.getRenderViewEntity() == null) {
            return false;
        }
        return mc.worldRenderer.getClippinghelper().isBoundingBoxInFrustum(box);
    }


    public static int getCeilingHeight() {
        if (mc.player == null || mc.world == null) {
            return Integer.MAX_VALUE;
        }
        
        BlockPos playerPos = mc.player.getPosition();
        int height = 0;
        
        // Проверяем до 10 блоков вверх
        for (int i = 0; i < 10; i++) {
            BlockPos checkPos = playerPos.up(i + 1); // +1, т.к. начинаем с блока над головой
            if (!mc.world.getBlockState(checkPos).isAir()) {
                return i;
            }
            height = i + 1;
        }
        
        return height;
    }
}
