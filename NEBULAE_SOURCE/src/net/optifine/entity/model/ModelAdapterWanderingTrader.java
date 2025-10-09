package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.WanderingTraderRenderer;
import net.minecraft.client.renderer.entity.model.VillagerModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;

public class ModelAdapterWanderingTrader extends ModelAdapterVillager
{
// leaked by itskekoff; discord.gg/sk3d Yds0DPlT
    public ModelAdapterWanderingTrader()
    {
        super(EntityType.WANDERING_TRADER, "wandering_trader", 0.5F);
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        WanderingTraderRenderer wanderingtraderrenderer = new WanderingTraderRenderer(entityrenderermanager);
        wanderingtraderrenderer.entityModel = (VillagerModel)modelBase;
        wanderingtraderrenderer.shadowSize = shadowSize;
        return wanderingtraderrenderer;
    }
}
