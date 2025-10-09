package beame.laby.targetesp;

import beame.laby.targetesp.util.TargetTracker;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.events.client.entity.player.AttackEvent;
import net.labymod.api.event.events.client.lifecycle.GameTickEvent;
import net.labymod.api.event.events.render.game.OverlayRenderEvent;
import net.labymod.api.event.events.render.world.WorldRenderEvent;
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
    public void onAttack(AttackEvent event) {
        if (!addon.configuration().enabled().get()) {
            return;
        }
        if (event.getTarget() != null) {
            tracker.setTarget(event.getTarget());
        }
    }

    @Subscribe
    public void onTick(GameTickEvent event) {
        if (!addon.configuration().enabled().get()) {
            tracker.clear();
            return;
        }
        Entity player = Minecraft.getInstance().player;
        tracker.tick(player);
    }

    @Subscribe
    public void onOverlay(OverlayRenderEvent event) {
        Entity target = tracker.getTarget();
        if (target == null) {
            return;
        }
        MatrixStack matrices = event.getMatrixStack();
        renderer.drawHud(matrices, target, addon.configuration().mode().get(), tracker.getVisibility(), event.getPartialTicks());
    }

    @Subscribe
    public void onWorldRender(WorldRenderEvent event) {
        Entity target = tracker.getTarget();
        if (target == null) {
            return;
        }
        MatrixStack matrices = event.getMatrixStack();
        renderer.drawWorld(matrices, target, addon.configuration().mode().get(), tracker.getVisibility(), event.getPartialTicks());
    }
}
