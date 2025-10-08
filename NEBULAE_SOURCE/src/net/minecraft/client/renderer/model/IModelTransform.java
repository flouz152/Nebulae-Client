package net.minecraft.client.renderer.model;

import net.minecraft.util.math.vector.TransformationMatrix;

public interface IModelTransform
{
// leaked by itskekoff; discord.gg/sk3d 88M5aqVN
default TransformationMatrix getRotation()
    {
        return TransformationMatrix.identity();
    }

default boolean isUvLock()
    {
        return false;
    }
}
