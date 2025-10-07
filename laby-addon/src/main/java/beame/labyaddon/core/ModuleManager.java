package beame.labyaddon.core;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Central registry for the add-on modules.
 */
public class ModuleManager {

    private final List<AddonModule> modules = new ArrayList<>();

    public void register(AddonModule module) {
        modules.add(module);
    }

    public List<AddonModule> getModules() {
        return Collections.unmodifiableList(modules);
    }

    public void clear() {
        modules.clear();
    }

    public void onTick() {
        for (AddonModule module : modules) {
            if (module.isEnabled()) {
                module.onTick();
            }
        }
    }

    public void onChatMessage(ITextComponent component) {
        for (AddonModule module : modules) {
            if (module.isEnabled()) {
                module.onChatMessage(component);
            }
        }
    }

    public void onRender3D(MatrixStack stack, float partialTicks) {
        for (AddonModule module : modules) {
            if (module.isEnabled()) {
                module.onRender3D(stack, partialTicks);
            }
        }
    }

    public void onRender2D(MatrixStack stack, float partialTicks) {
        for (AddonModule module : modules) {
            if (module.isEnabled()) {
                module.onRender2D(stack, partialTicks);
            }
        }
    }
}
