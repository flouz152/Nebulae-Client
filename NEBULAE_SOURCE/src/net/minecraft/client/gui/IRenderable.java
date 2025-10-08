package net.minecraft.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

public interface IRenderable
{
// leaked by itskekoff; discord.gg/sk3d 0rEoi29H
    void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks);
}
