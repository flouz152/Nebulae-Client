package net.minecraftforge.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;

@FunctionalInterface
public interface ISkyRenderHandler
{
// leaked by itskekoff; discord.gg/sk3d nDpfduch
    void render(int var1, float var2, MatrixStack var3, ClientWorld var4, Minecraft var5);
}
