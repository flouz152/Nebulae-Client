
package beame.components.modules.misc;

import beame.components.command.AbstractCommand;
import beame.util.color.ColorUtils;
import beame.util.math.TimerUtil;
import events.Event;
import events.impl.game.WorldEvent;
import events.impl.packet.EventPacket;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.server.SMultiBlockChangePacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.optifine.render.RenderUtils;
import org.lwjgl.opengl.GL11;
import beame.setting.ConfigSetting;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.RadioSetting;

import java.awt.*;
import java.util.ArrayList;

public class Xray extends Module {
// leaked by itskekoff; discord.gg/sk3d jHUgQW8v

    public Xray() {
        super("Xray", Category.Misc, true, "Сканирует и показывает древние обломки в аду");

        addSettings(mode,bot);
    }
        public final RadioSetting mode = new RadioSetting("Режим", "Древние обломки", "Древние обломки");
        public final BooleanSetting bot = new BooleanSetting("Авто фарм");

    @Override
    public void addSettings(ConfigSetting<?>... configSettings) {
        super.addSettings(configSettings);
    }

    public BlockPos clicking = null;
    private final ArrayList<BlockPos> highlightedDebris = new ArrayList<>();
    private final ArrayList<BlockPos> ores = new ArrayList<>();
    private final ArrayList<BlockPos> clicked = new ArrayList<>();
    private final TimerUtil chatTimer = new TimerUtil();
    private Thread thread;
    int foundDebrisCount = 0;

    @Override
    public void event(Event event) {
        if(event instanceof EventPacket) {
            EventPacket e = (EventPacket) event;
            if (e.getPacket() instanceof SMultiBlockChangePacket packet) {
                packet.func_244310_a((blockPos, blockState) -> {
                    if (blockState.getBlock() == Blocks.ANCIENT_DEBRIS) {
                        this.ores.add(new BlockPos(blockPos));
                    }
                });
            }
        }

        if(event instanceof WorldEvent) {
            BlockPos playerPos = mc.player.getPosition();
            this.highlightedDebris.clear();

            for (int x = -25; x <= 25; ++x) {
                for (int y = -25; y <= 25; ++y) {
                    for (int z = -25; z <= 25; ++z) {
                        BlockPos pos = playerPos.add(x, y, z);
                        Block block = mc.world.getBlockState(pos).getBlock();
                        if (block == Blocks.ANCIENT_DEBRIS && this.hasAtLeastTwoAirBlocksAround(pos)
                                && !this.hasTwoQuartzOrGoldNearby(pos)
                                && this.hasAtLeastFiveAirInCube(pos)
                                && !this.hasTooManyAncientDebrisNearby(pos)) {
                            Vector3d renderPos = mc.getRenderManager().info.getProjectedView();
                            GL11.glPushMatrix();
                            GL11.glTranslated(-renderPos.x, -renderPos.y, -renderPos.z);
                            RenderUtils.drawBlockBox(pos, ColorUtils.rgb(255, 215, 0));
                            GL11.glPopMatrix();
                            this.highlightedDebris.add(pos);
                        }
                    }
                }
            }

            if (!this.highlightedDebris.isEmpty() && (this.thread == null || !this.thread.isAlive())) {
                this.startClickingThread();
            }

            for (BlockPos pos : new ArrayList<>(this.ores)) {
                if (mc.world.getBlockState(pos).getBlock() != Blocks.ANCIENT_DEBRIS) {
                    this.ores.remove(pos);
                } else {
                    Vector3d renderPos = mc.getRenderManager().info.getProjectedView();
                    GL11.glPushMatrix();
                    GL11.glTranslated(-renderPos.x, -renderPos.y, -renderPos.z);
                    RenderUtils.drawBlockBox(pos, Color.CYAN.getRGB());
                    GL11.glPopMatrix();
                }
            }

            if (chatTimer.hasTimeElapsed(10000)) {
                AbstractCommand.addMessage("Найдено древних обломков: " + this.highlightedDebris.size());
                chatTimer.reset();
            }
        }
    }

    private void startClickingThread() {
        this.thread = new Thread(() -> {
            for (BlockPos pos : this.highlightedDebris) {
                if (!this.clicked.contains(pos) && bot.get()) {

                    AbstractCommand.addMessage("Найден Ancient Debris на " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
                    String gotoCommand = String.format("#goto %d %d %d", pos.getX(), pos.getY(), pos.getZ());
                    mc.player.sendChatMessage(gotoCommand);

                    while (!isPlayerAtPosition(pos, 2.0)) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                            break;
                        }
                    }

                    try {
                        AbstractCommand.addMessage("Ожидание на позиции...");
                        Thread.sleep(4000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                    mc.player.connection.sendPacket(new CPlayerDiggingPacket(
                            CPlayerDiggingPacket.Action.START_DESTROY_BLOCK,
                            pos,
                            Direction.UP
                    ));

                    this.clicked.add(pos);
                    this.clicking = pos;
                    ++this.foundDebrisCount;

                    try {
                        Thread.sleep(450L);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            this.clicking = null;
        });
        this.thread.start();
    }

    private boolean isPlayerAtPosition(BlockPos targetPos, double radius) {
        if (mc.player == null) return false;

        BlockPos playerPos = mc.player.getPosition();
        double distance = Math.sqrt(
                Math.pow(playerPos.getX() - targetPos.getX(), 2) +
                        Math.pow(playerPos.getY() - targetPos.getY(), 2) +
                        Math.pow(playerPos.getZ() - targetPos.getZ(), 2)
        );

        return distance <= radius;
    }

    private boolean hasAtLeastTwoAirBlocksAround(BlockPos pos) {
        int airBlockCount = 0;
        BlockPos[] surroundingPositions = {pos.north(), pos.south(), pos.east(), pos.west(), pos.up(), pos.down()};

        for (BlockPos surroundingPos : surroundingPositions) {
            Block surroundingBlock = mc.world.getBlockState(surroundingPos).getBlock();
            if (surroundingBlock == Blocks.AIR || surroundingBlock == Blocks.LAVA) {
                ++airBlockCount;
            }

            if (airBlockCount >= 2) {
                return true;
            }
        }
        return false;
    }

    private boolean hasTwoQuartzOrGoldNearby(BlockPos pos) {
        int quartzOrGoldCount = 0;


        for (int x = -1; x <= 1; ++x) {
            for (int y = -1; y <= 1; ++y) {
                for (int z = -1; z <= 1; ++z) {
                    BlockPos nearbyPos = pos.add(x, y, z);
                    Block nearbyBlock = mc.world.getBlockState(nearbyPos).getBlock();
                    if (nearbyBlock == Blocks.NETHER_QUARTZ_ORE || nearbyBlock == Blocks.NETHER_GOLD_ORE) {
                        ++quartzOrGoldCount;
                    }

                    if (quartzOrGoldCount >= 4) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean hasAtLeastFiveAirInCube(BlockPos pos) {
        int airBlockCount = 0;

        for (int x = -1; x <= 1; ++x) {
            for (int y = -1; y <= 1; ++y) {
                for (int z = -1; z <= 1; ++z) {
                    BlockPos nearbyPos = pos.add(x, y, z);
                    Block nearbyBlock = mc.world.getBlockState(nearbyPos).getBlock();
                    if (nearbyBlock == Blocks.AIR || nearbyBlock == Blocks.LAVA) {
                        ++airBlockCount;
                    }

                    if (airBlockCount >= 4) {
                        return true;
                    }
                }
            }
        }

        return airBlockCount >= 4;
    }

    private boolean hasTooManyAncientDebrisNearby(BlockPos pos) {
        int ancientDebrisCount = 0;

        for (int x = -3; x <= 2; ++x) {
            for (int y = -2; y <= 2; ++y) {
                for (int z = -2; z <= 3; ++z) {
                    BlockPos nearbyPos = pos.add(x, y, z);
                    Block nearbyBlock = mc.world.getBlockState(nearbyPos).getBlock();
                    if (nearbyBlock == Blocks.ANCIENT_DEBRIS) {
                        ++ancientDebrisCount;
                    }

                    if (ancientDebrisCount > 3) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    }
