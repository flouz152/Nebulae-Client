package beame.labyaddon.core;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;

/**
 * Base module implementation used by the standalone Nebulae addon.
 * <p>
 * The original client ships with a fairly involved module framework.  For the
 * add-on we provide a very small abstraction that mirrors the life-cycle the
 * Laby API offers – toggleable state, tick updates and lightweight render hooks.
 */
public abstract class AddonModule {

    protected final Minecraft mc = Minecraft.getInstance();

    private final String name;
    private boolean enabled;

    protected AddonModule(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) {
            return;
        }
        this.enabled = enabled;

        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    /** Called when the module is enabled. */
    protected void onEnable() {
    }

    /** Called when the module is disabled. */
    protected void onDisable() {
    }

    /** Invoked every client tick while the module is enabled. */
    public void onTick() {
    }

    /** Invoked after chat messages were received. */
    public void onChatMessage(ITextComponent message) {
    }

    /** Invoked while the world is rendered (3D layer). */
    public void onRender3D(MatrixStack stack, float partialTicks) {
    }

    /** Invoked while the overlay is rendered (2D layer). */
    public void onRender2D(MatrixStack stack, float partialTicks) {
    }
}
