package beame.laby.targetesp;

import beame.laby.targetesp.util.TargetTracker;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class TargetEspController {

    private final TargetEspAddon addon;
    private final TargetEspRenderer renderer = new TargetEspRenderer();
    private final TargetTracker tracker = new TargetTracker();

    public TargetEspController(TargetEspAddon addon) {
        this.addon = addon;
    }

    @SubscribeEvent
    public void onAttack(AttackEntityEvent event) {
        if (event.getEntityPlayer() == null || !event.getEntityPlayer().world.isRemote) {
            return;
        }
        if (!addon.configuration().isEnabled()) {
            return;
        }
        Entity target = event.getTarget();
        if (target != null) {
            tracker.setTarget(target);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (!addon.configuration().isEnabled()) {
            tracker.clear();
            return;
        }
        Entity player = Minecraft.getInstance().player;
        tracker.tick(player);
    }

    @SubscribeEvent
    public void onOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        Entity target = tracker.getTarget();
        if (target == null || !addon.configuration().isEnabled()) {
            return;
        }
        MatrixStack matrices = event.getMatrixStack();
        renderer.drawHud(matrices, target, addon.configuration().getMode(), tracker.getVisibility(), event.getPartialTicks());
    }

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        Entity target = tracker.getTarget();
        if (target == null || !addon.configuration().isEnabled()) {
            return;
        }
        MatrixStack matrices = event.getMatrixStack();
        renderer.drawWorld(matrices, target, addon.configuration().getMode(), tracker.getVisibility(), event.getPartialTicks());
    }

    public void reset() {
        tracker.clear();
    }
}
