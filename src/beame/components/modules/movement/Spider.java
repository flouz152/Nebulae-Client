package beame.components.modules.movement;

import beame.components.command.AbstractCommand;
import beame.util.math.TimerUtil;
import events.Event;
import events.impl.player.EventMotion;
import events.impl.player.EventUpdate;
import events.impl.player.TickEvent;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.LadderBlock;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MoverType;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import beame.setting.SettingList.RadioSetting;

import java.util.Random;

public class Spider extends Module {
// leaked by itskekoff; discord.gg/sk3d fworsPbv

    public final RadioSetting mode = new RadioSetting("Режим", "Фантайм", "Фантайм", "Кораллы", "СпукиТайм");

    public Spider() {
        super("Spider", Category.Movement, true, "Позволяет ползать по стенам.");
        addSettings(mode);
    }

    private final TimerUtil stopWatch = new TimerUtil();
    private final Random random = new Random();
    private int oldItem = -1;
    private int oldItem1 = -1;
    private int i;

    @Override
    public void event(Event event) {
        if (event instanceof TickEvent) {
            if (mode.is("Кораллы")) {
                if (mc.player.collidedHorizontally) {
                    mc.player.setMotion(mc.player.getMotion().x, 0.42, mc.player.getMotion().z);
                }
            }
        }

        if (event instanceof EventUpdate) {
            if (mode.is("СпукиТайм")) {
                for (i = 0; i < 9; ++i) {
                    if (mc.player.inventory.getStackInSlot(i).getItem() == Items.ELYTRA && !mc.player.isOnGround() && mc.player.collidedHorizontally && mc.player.fallDistance == 0.0F) {
                        mc.playerController.windowClick(0, 6, i, ClickType.SWAP, mc.player);
                        mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                        mc.player.setMotion(mc.player.getMotion().x, 0.366, mc.player.getMotion().z);
                        mc.playerController.windowClick(0, 6, i, ClickType.SWAP, mc.player);
                        this.oldItem = i;
                    }
                }
            } else {
                if (mc.player.inventory.armorInventory.get(2).getItem() != Items.ELYTRA && mc.player.collidedHorizontally) {
                    for (i = 0; i < 9; ++i) {
                        if (mc.player.inventory.getStackInSlot(i).getItem() == Items.ELYTRA) {
                            mc.playerController.windowClick(0, 6, i, ClickType.SWAP, mc.player);
                            this.oldItem1 = i;
                            this.stopWatch.reset();
                        }
                    }
                }

                if (mc.player.collidedHorizontally) {
                    mc.gameSettings.keyBindJump.setPressed(false);
                    if (this.stopWatch.hasReached(180L)) {
                        mc.gameSettings.keyBindJump.setPressed(true);
                    }
                }

                if (mc.player.inventory.armorInventory.get(2).getItem() == Items.ELYTRA && !mc.player.collidedHorizontally && this.oldItem1 != -1) {
                    mc.playerController.windowClick(0, 6, this.oldItem1, ClickType.SWAP, mc.player);
                    this.oldItem1 = -1;
                }

                if (mc.player.inventory.armorInventory.get(2).getItem() == Items.ELYTRA && !mc.player.isOnGround() && mc.player.collidedHorizontally) {
                    if (mc.player.fallDistance != 0.0F) {
                        return;
                    }

                    mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                    mc.player.setMotion(mc.player.getMotion().x, 0.36, mc.player.getMotion().z);
                }
            }
        }


        if (event instanceof EventMotion motion) {
            if (mode.is("Фантайм")) {
                if (mc.player == null || mc.world == null) return;
                if (!mc.player.collidedHorizontally) return;

                BlockPos feetPos = mc.player.getPosition();
                BlockPos headPos = feetPos.up();
                BlockPos aboveHeadPos = headPos.up();
                Direction dir = mc.player.getHorizontalFacing();
                World world = mc.world;

                BlockPos attachPos = headPos.offset(dir);
                BlockPos feetForwardPos = feetPos.offset(dir);

                Block blockAtHead = world.getBlockState(headPos).getBlock();
                Block blockAtFeetForward = world.getBlockState(feetForwardPos).getBlock();

                long delay = (long) (250 / 2.5 + random.nextInt(50));
                if (!stopWatch.hasReached(delay)) return;

                int slot = getSlotForLadder();
                if (slot == -1) {
                    AbstractCommand.addMessage("Нет лестниц!");
                    toggle();
                    return;
                }

                int lastSlot = mc.player.inventory.currentItem;
                mc.player.inventory.currentItem = slot;

                boolean placed = false;

                if ((blockAtHead instanceof LadderBlock || world.getBlockState(attachPos).isSolid()) && world.getBlockState(aboveHeadPos).isAir()) {
                    placeLadderAt(headPos, dir);
                    placed = true;
                } else if (world.getBlockState(feetForwardPos).isSolid() && world.getBlockState(headPos).isAir()) {
                    placeLadderAt(feetPos, dir);
                    placed = true;
                }

                mc.player.inventory.currentItem = lastSlot;

                if (placed) {
                    motion.setOnGround(true);
                    mc.player.setOnGround(true);
                    mc.player.jump();
                    stopWatch.reset();
                }
          /*  } else if (mode.is("СпукиТайм")) {
                if (!mc.player.collidedHorizontally) {
                    return;
                }

                long speed = (long) MathHelper.clamp(500 - (spiderSpeed.get() / 2 * 100), 0, 500);
                if (stopWatch.hasReached(speed)) {
                    motion.setOnGround(true);
                    mc.player.setOnGround(true);
                    mc.player.collidedVertically = true;
                    mc.player.collidedHorizontally = true;
                    mc.player.isAirBorne = true;
                    mc.player.jump();
                    placeSlimeStack(motion);
                    stopWatch.reset();
                }*/
            } else if (mode.is("СпукиТайм")) {
                motion.setPitch(0.0F);
                mc.player.rotationPitchHead = 0.0F;
            }
        }
    }

    private void placeSlimeStack(EventMotion motion) {
        int slotSlime = getSlotForSlime();
        if (slotSlime == -1) {
            AbstractCommand.addMessage("Блоки слизи не найдены!");
            toggle();
            return;
        }

        int lastSlot = mc.player.inventory.currentItem;
        mc.player.inventory.currentItem = slotSlime;

        BlockPos playerPos = mc.player.getPosition();

        for (int i = 1; i <= 2; i++) {
            BlockPos slimePos = playerPos.up(i);
            if (mc.world.getBlockState(slimePos).isAir()) {
                placeSlimeBlock(slimePos, Hand.MAIN_HAND, mc.playerController, mc.world, mc.player);
            }
        }

        mc.player.inventory.currentItem = lastSlot;
    }

    private void placeSlimeBlock(BlockPos pos, Hand hand, PlayerController playerController, World world, ClientPlayerEntity player) {
        if (!world.getBlockState(pos).isAir()) {
            return;
        }

        Direction direction = Direction.UP;
        BlockPos tPos = pos.down();
        BlockRayTraceResult traceResult = new BlockRayTraceResult(
                new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5),
                direction, tPos, false);

        playerController.func_217292_a(player, (ClientWorld) world, hand, traceResult);
        player.swingArm(hand);
    }

    private int getSlotForSlime() {
        for (int i = 0; i < 36; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.SLIME_BLOCK) {
                return i;
            }
        }
        return -1;
    }

    private void stepOntoBlock() {
        mc.player.jump();
        mc.player.setVelocity(mc.player.getMotion().x, 0.42f, mc.player.getMotion().z);
        Vector3d forward = mc.player.getLookVec().normalize().scale(0.1);
        mc.player.move(MoverType.SELF, forward);
    }

    private void placeLadderAt(BlockPos pos, Direction attachDir) {
        BlockPos neighbor = pos.offset(attachDir);
        Direction sideHit = attachDir.getOpposite();

        double x = neighbor.getX() + (sideHit == Direction.EAST ? 1.0 : sideHit == Direction.WEST ? 0.0 : 0.5);
        double y = neighbor.getY() + 0.5;
        double z = neighbor.getZ() + (sideHit == Direction.SOUTH ? 1.0 : sideHit == Direction.NORTH ? 0.0 : 0.5);

        Vector3d hitVec = new Vector3d(x, y, z);
        BlockRayTraceResult trace = new BlockRayTraceResult(hitVec, sideHit, neighbor, false);

        PlayerController controller = mc.playerController;
        ClientPlayerEntity player = mc.player;
        Hand hand = Hand.MAIN_HAND;
        controller.func_217292_a(player, (ClientWorld) mc.world, hand, trace);
    }

    private int getSlotForLadder() {
        for (int i = 0; i < mc.player.inventory.mainInventory.size(); i++) {
            ItemStack stack = mc.player.inventory.mainInventory.get(i);
            if (!stack.isEmpty() && stack.getItem() == Items.LADDER) {
                return i;
            }
        }
        return -1;
    }
}