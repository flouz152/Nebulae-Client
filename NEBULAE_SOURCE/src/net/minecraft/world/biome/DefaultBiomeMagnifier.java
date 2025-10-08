package net.minecraft.world.biome;

public enum DefaultBiomeMagnifier implements IBiomeMagnifier
{
// leaked by itskekoff; discord.gg/sk3d qTaNOQOX
    INSTANCE;

    public Biome getBiome(long seed, int x, int y, int z, BiomeManager.IBiomeReader biomeReader)
    {
        return biomeReader.getNoiseBiome(x >> 2, y >> 2, z >> 2);
    }
}
