package net.minecraft.world.biome;

public interface IBiomeMagnifier
{
// leaked by itskekoff; discord.gg/sk3d ho0jq3KD
    Biome getBiome(long seed, int x, int y, int z, BiomeManager.IBiomeReader biomeReader);
}
