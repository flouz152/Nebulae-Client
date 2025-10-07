package net.minecraft.block;

import beame.Nebulae;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Random;

public class WebBlock extends Block
{
// leaked by itskekoff; discord.gg/sk3d ckHA9ftl
    public WebBlock(AbstractBlock.Properties properties)
    {
        super(properties);
    }

    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
        Random random = new Random();
        double x = 0.60 + (0.62 - 0.59) * random.nextDouble();
        double z = 0.60 + (0.62 - 0.59) * random.nextDouble();
        entityIn.setMotionMultiplier(state, Nebulae.getHandler().getModuleList().getCollisionDisabler().isState() ? Nebulae.getHandler().getModuleList().getCollisionDisabler().addtivites.get(0).get() ? Nebulae.getHandler().getModuleList().getCollisionDisabler().randomize.get() ? new Vector3d(x, 0.10D, z) : new Vector3d(0.61D, 0.10D, 0.61D) : new Vector3d(0.25D, 0.05F, 0.25D) : new Vector3d(0.25D, 0.05F, 0.25D));
    }
}