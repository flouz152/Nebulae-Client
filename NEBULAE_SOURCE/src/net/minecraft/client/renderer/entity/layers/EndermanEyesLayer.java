package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.EndermanModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

public class EndermanEyesLayer<T extends LivingEntity> extends AbstractEyesLayer<T, EndermanModel<T>>
{
// leaked by itskekoff; discord.gg/sk3d QmZXh1nt
    private static final RenderType RENDER_TYPE = RenderType.getEyes(new ResourceLocation("textures/entity/enderman/enderman_eyes.png"));

    public EndermanEyesLayer(IEntityRenderer<T, EndermanModel<T>> rendererIn)
    {
        super(rendererIn);
    }

    public RenderType getRenderType()
    {
        return RENDER_TYPE;
    }
}
