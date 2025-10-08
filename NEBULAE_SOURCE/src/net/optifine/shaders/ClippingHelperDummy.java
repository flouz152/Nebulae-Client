package net.optifine.shaders;

import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Matrix4f;

public class ClippingHelperDummy extends ClippingHelper
{
// leaked by itskekoff; discord.gg/sk3d midjHoW6
    public ClippingHelperDummy()
    {
        super(new Matrix4f(), new Matrix4f());
    }

    public boolean isBoundingBoxInFrustum(AxisAlignedBB aabbIn)
    {
        return true;
    }
}
