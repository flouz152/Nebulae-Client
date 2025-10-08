package net.optifine.entity.model.anim;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.optifine.expr.IExpressionResolver;

public interface IModelResolver extends IExpressionResolver
{
// leaked by itskekoff; discord.gg/sk3d 0CIESwlC
    ModelRenderer getModelRenderer(String var1);

    ModelVariableFloat getModelVariable(String var1);
}
