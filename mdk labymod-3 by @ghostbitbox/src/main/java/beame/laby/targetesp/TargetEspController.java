package beame.laby.targetesp;

import beame.laby.targetesp.util.TargetTracker;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.events.client.TickEvent;
import net.labymod.api.event.events.client.gui.RenderGameOverlayEvent;
import net.labymod.api.event.events.client.player.AttackEntityEvent;
import net.labymod.api.event.events.client.renderer.RenderWorldEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public class TargetEspController {

    private final TargetEspAddon addon;
    private final TargetEspRenderer renderer = new TargetEspRenderer();
    private final TargetTracker tracker = new TargetTracker();

    public TargetEspController(TargetEspAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    public void onAttack(AttackEntityEvent event) {
        if (!addon.configuration().isEnabled()) {
            return;
        }
        if (event.getEntity() != null) {
            tracker.setTarget(event.getEntity());
        }
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (event.getPhase() != TickEvent.Phase.PRE) {
            return;
        }
        if (!addon.configuration().isEnabled()) {
            tracker.clear();
            return;
        }
        Entity player = Minecraft.getInstance().player;
        tracker.tick(player);
    }

    @Subscribe
    public void onOverlay(RenderGameOverlayEvent event) {
        if (event.getPhase() != RenderGameOverlayEvent.Phase.POST) {
            return;
        }
        Entity target = tracker.getTarget();
        if (target == null) {
            return;
        }
        MatrixStack matrices = event.getMatrixStack();
        renderer.drawHud(matrices, target, addon.configuration().getMode(), tracker.getVisibility(), event.getPartialTicks());
    }

    @Subscribe
    public void onWorldRender(RenderWorldEvent event) {
        Entity target = tracker.getTarget();
        if (target == null) {
            return;
        }
        MatrixStack matrices = event.getMatrixStack();
        renderer.drawWorld(matrices, target, addon.configuration().getMode(), tracker.getVisibility(), event.getPartialTicks());
    }

    public void shutdown() {
        tracker.clear();
    }
}