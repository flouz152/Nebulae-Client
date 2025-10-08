/*
package beame.feature.features.Player;

import beame.command.api.CMD;
import beame.util.ClientHelper;
import beame.util.color.ColorUtils;
import beame.util.fonts.CustomFont;
import beame.util.fonts.Fonts;
import beame.util.other.Mathf;
import beame.util.player.PlayerUtil;
import beame.util.render.ClientHandler;
import beame.util.render.ProjectionUtil;
import com.ibm.icu.impl.Pair;
import com.mojang.blaze3d.matrix.MatrixStack;
import events.Event;
import events.impl.packet.EventPacket;
import events.impl.render.Render2DEvent;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.block.Blocks;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TrapDuration extends Module {

    private final List<Pair<Long, Vector3d>> consumables = new ArrayList<>();
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final float fontSize = 16.0f;
    private final CustomFont font = Fonts.SFREGULAR.get(fontSize);

    public TrapDuration() {
        super("TrapDuration", Category.Player, true, "Показывает время жизни расходников.");
    }

    @Override
    public void event(Event event) {
        if (event instanceof EventPacket) {
            EventPacket e = (EventPacket) event;
            if (e.isReceivePacket()) {
                IPacket<?> packet = e.getPacket();

//                [19:43:28] [Netty Client IO #0/INFO]: [STDERR]: minecraft:entity.wither.break_block
//                        [19:43:28] [Netty Client IO #0/INFO]: [STDERR]: minecraft:entity.evoker_fangs.attack
//                        [19:43:28] [Netty Client IO #0/INFO]: [STDERR]: minecraft:ui.toast.challenge_complete
//                        [19:43:28] [Netty Client IO #0/INFO]: [STDERR]: minecraft:entity.ender_dragon.growl

//                [19:43:48] [Netty Client IO #0/INFO]: [STDERR]: minecraft:item.totem.use
//                        [19:43:48] [Netty Client IO #0/INFO]: [STDERR]: minecraft:entity.evoker.prepare_attack
//                        [19:43:48] [Netty Client IO #0/INFO]: [STDERR]: minecraft:entity.ender_dragon.hurt

                if (packet instanceof SPlaySoundEffectPacket) {
                    SPlaySoundEffectPacket wrapper = (SPlaySoundEffectPacket) packet;
                    CMD.addMessage(wrapper.getSound().getName());
                    if (wrapper.getSound().getName().getPath().equals("block.piston.contract") || wrapper.getSound().getName().getPath().equals("block.piston.extend") || wrapper.getSound().getName().getPath().equals("entity.wither.break_block")) {
                        consumables.add(Pair.of(System.currentTimeMillis() + 15000,
                                Vector3d.copyCentered(new BlockPos(wrapper.getX(), wrapper.getY(), wrapper.getZ()))));
                    }
                    if (wrapper.getSound().getName().getPath().equals("block.anvil.place") || wrapper.getSound().getName().getPath().equals("entity.wither.break_block")) {
                        BlockPos soundPos = new BlockPos(wrapper.getX(), wrapper.getY(), wrapper.getZ());
                        long delay = 250;


                        if (scheduler.isShutdown() || scheduler.isTerminated()) {
                            scheduler = Executors.newSingleThreadScheduledExecutor();
                        }
                        scheduler.schedule(() -> {
                            List<BlockPos> cubes = PlayerUtil.getCube(soundPos, 4, 4);
                            cubes.stream()
                                    .filter(pos -> Mathf.getDistance(soundPos, pos) > 2 &&
                                            mc.world.getBlockState(pos).getBlock().equals(Blocks.COBBLESTONE))
                                    .min(Comparator.comparing(pos -> Mathf.getDistance(soundPos, pos)))
                                    .ifPresent(pos -> {
                                        long andesiteCount = PlayerUtil.getCube(pos, 1, 1).stream()
                                                .filter(pos2 -> mc.world.getBlockState(pos2).getBlock().equals(Blocks.ANDESITE))
                                                .count();

                                        if (andesiteCount == 16 || andesiteCount == 9 || andesiteCount == 10) {
                                            int time = andesiteCount == 16 ? 60000 : 20000;
                                            consumables.add(Pair.of(System.currentTimeMillis() + time - delay,
                                                    Vector3d.copyCentered(pos).add(0, andesiteCount == 16 ? -0.5 : 0, 0)));
                                        }
                                    });
                        }, delay, TimeUnit.MILLISECONDS);

                    }
                }
            }
        } else if (event instanceof Render2DEvent) {
            if (!ClientHelper.isFuntime()) return;

            Iterator<Pair<Long, Vector3d>> iterator = consumables.iterator();
            while (iterator.hasNext()) {
                Pair<Long, Vector3d> cons = iterator.next();
                double timeLeft = (double) (cons.first - System.currentTimeMillis()) / 1000;

                if (timeLeft <= 0) {
                    iterator.remove();
                    continue;
                }

                Vector2f vec2f = ProjectionUtil.project2D(cons.second);
                String text = Mathf.round(timeLeft, 1) + "с";

                int width = font.getWidth(text);
                float posX = vec2f.x - width / 2f;
                float posY = vec2f.y;
                float textOffsetY = 5f;

                ClientHandler.drawRect(posX - 1, posY - 1, posX + width + 2, posY + fontSize - 1, ColorUtils.getColor(0, 0, 0, 128));

                MatrixStack matrixStack = new MatrixStack();
                font.draw(matrixStack, text, posX, posY + textOffsetY, -1);
            }

        }
    }

    @Override
    public void onDisable() {
        consumables.clear();
        scheduler.shutdown();
        super.onDisable();
    }
}
*/
// leaked by itskekoff; discord.gg/sk3d SXvzn3uR
