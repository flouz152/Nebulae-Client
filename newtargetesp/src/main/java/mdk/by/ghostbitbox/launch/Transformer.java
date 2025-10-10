package mdk.by.ghostbitbox.launch;

import net.labymod.addon.AddonTransformer;
import net.labymod.api.TransformerType;

public class Transformer extends AddonTransformer {

    @Override
    public void registerTransformers() {
        this.registerTransformer(TransformerType.VANILLA, "ghostbitbox.mixin.json");
    }
}