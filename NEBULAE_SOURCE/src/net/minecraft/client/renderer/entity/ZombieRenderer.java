package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.ZombieModel;
import net.minecraft.entity.monster.ZombieEntity;

public class ZombieRenderer extends AbstractZombieRenderer<ZombieEntity, ZombieModel<ZombieEntity>>
{
// leaked by itskekoff; discord.gg/sk3d BIh2UkuY
    public ZombieRenderer(EntityRendererManager renderManagerIn)
    {
        super(renderManagerIn, new ZombieModel<>(0.0F, false), new ZombieModel<>(0.5F, true), new ZombieModel<>(1.0F, true));
    }
}
