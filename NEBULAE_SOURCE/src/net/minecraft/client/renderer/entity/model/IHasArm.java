package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.HandSide;

public interface IHasArm
{
// leaked by itskekoff; discord.gg/sk3d 26dKgBXt
    void translateHand(HandSide sideIn, MatrixStack matrixStackIn);
}
