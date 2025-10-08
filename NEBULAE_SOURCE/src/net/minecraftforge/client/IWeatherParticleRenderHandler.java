package net.minecraftforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;

@FunctionalInterface
public interface IWeatherParticleRenderHandler
{
// leaked by itskekoff; discord.gg/sk3d 2WsV26CQ
    void render(int var1, ClientWorld var2, Minecraft var3, ActiveRenderInfo var4);
}
