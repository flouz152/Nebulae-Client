package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public interface IAnimatedSprite
{
// leaked by itskekoff; discord.gg/sk3d HTj7MZ9J
    TextureAtlasSprite get(int particleAge, int particleMaxAge);

    TextureAtlasSprite get(Random rand);
}
