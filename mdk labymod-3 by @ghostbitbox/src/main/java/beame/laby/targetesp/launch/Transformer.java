package beame.laby.targetesp.launch;

import net.labymod.addon.AddonTransformer;
import net.labymod.api.TransformerType;

public class Transformer extends AddonTransformer {

  @Override
  public void registerTransformers() {
    this.registerTransformer(TransformerType.VANILLA, "targetesp.mixin.json");
  }
}
