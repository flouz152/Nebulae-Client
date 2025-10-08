package dev.nebulae.targetesp;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TargetEntityTracker {

    private final Minecraft minecraft = Minecraft.getInstance();
    private final TargetEspAddon addon;
    private final TargetEspRenderer renderer;

    private LivingEntity currentTarget;
    private float alpha;

    public TargetEntityTracker(TargetEspAddon addon) {
        this.addon = addon;
        this.renderer = new TargetEspRenderer(addon);
    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        if (event.getPlayer() != this.minecraft.player) {
            return;
        }

        Entity target = event.getTarget();
        if (target instanceof LivingEntity living) {
            this.currentTarget = living;
            this.alpha = 0.0F;
            this.renderer.resetAnimation();
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (!this.addon.configuration().enabled().get()) {
            this.currentTarget = null;
            this.alpha = 0.0F;
            return;
        }

        if (this.currentTarget != null) {
            ClientPlayerEntity player = this.minecraft.player;
            if (player == null || !this.currentTarget.isAlive() || this.currentTarget.removed || player.getDistance(this.currentTarget) > 5.0F) {
                this.currentTarget = null;
            }
        }

        float targetAlpha = this.currentTarget != null ? 1.0F : 0.0F;
        this.alpha = MathHelper.lerp(0.25F, this.alpha, targetAlpha);
        if (this.alpha < 0.01F && this.currentTarget == null) {
            this.alpha = 0.0F;
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (this.currentTarget == null) {
            return;
        }

        TargetEspMode mode = this.addon.configuration().modeProperty().get();
        if (!mode.rendersInWorld() || this.alpha <= 0.01F) {
            return;
        }

        this.renderer.renderWorld(event, this.currentTarget, this.alpha);
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        if (this.currentTarget == null) {
            return;
        }

        TargetEspMode mode = this.addon.configuration().modeProperty().get();
        if (!mode.rendersOnOverlay() || this.alpha <= 0.01F) {
            return;
        }

        this.renderer.renderOverlay(event, this.currentTarget, this.alpha);
    }
}
