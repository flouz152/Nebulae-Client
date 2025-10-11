package mdk.by.ghostbitbox.modules.render.targetesp;

import com.mojang.blaze3d.matrix.MatrixStack;
import mdk.by.ghostbitbox.TargetEspAddon;
import mdk.by.ghostbitbox.TargetEspConfig;
import mdk.by.ghostbitbox.modules.render.targethud.TargetHudRenderer;
import mdk.by.ghostbitbox.ui.TargetSettingsScreen;
import mdk.by.ghostbitbox.util.TargetTracker;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.events.client.TickEvent;
import net.labymod.api.event.events.client.gui.RenderGameOverlayEvent;
import net.labymod.api.event.events.client.player.AttackEntityEvent;
import net.labymod.api.event.events.client.renderer.RenderWorldEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.Entity;
import org.lwjgl.glfw.GLFW;

public class TargetEspController {

    private final TargetEspAddon addon;
    private final TargetEspRenderer renderer = new TargetEspRenderer();
    private final TargetHudRenderer hudRenderer = new TargetHudRenderer();
    private final TargetTracker tracker = new TargetTracker();
    private boolean settingsKeyDown;

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

        TargetEspConfig configuration = addon.configuration();
        renderer.applyConfiguration(configuration);
        hudRenderer.applyConfiguration(configuration);

        if (!configuration.isEnabled()) {
            tracker.clear();
            renderer.updateState(false);
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Entity player = minecraft.player;
        tracker.tick(player);
        renderer.updateState(tracker.getTarget() != null);

        handleSettingsKey(minecraft);
    }

    @Subscribe
    public void onOverlay(RenderGameOverlayEvent event) {
        if (event.getPhase() != RenderGameOverlayEvent.Phase.POST) {
            return;
        }
        Entity target = tracker.getTarget();
        MatrixStack matrices = event.getMatrixStack();
        hudRenderer.render(matrices, target, tracker.getVisibility(), event.getPartialTicks());
        if (target == null) {
            return;
        }
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
        renderer.updateState(false);
        hudRenderer.applyConfiguration(addon.configuration());
    }

    private void handleSettingsKey(Minecraft minecraft) {
        if (minecraft == null) {
            return;
        }

        long windowHandle = minecraft.getMainWindow().getHandle();
        boolean pressed = InputMappings.isKeyDown(windowHandle, GLFW.GLFW_KEY_RIGHT_SHIFT);

        if (pressed && !settingsKeyDown && minecraft.currentScreen == null) {
            minecraft.displayGuiScreen(new TargetSettingsScreen(addon));
        }

        settingsKeyDown = pressed;
    }
}
