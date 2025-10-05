package net.minecraft.loot;

import java.util.Random;
import net.minecraft.util.ResourceLocation;

public interface IRandomRange
{
// leaked by itskekoff; discord.gg/sk3d KtFgGy6q
    ResourceLocation CONSTANT = new ResourceLocation("constant");
    ResourceLocation UNIFORM = new ResourceLocation("uniform");
    ResourceLocation BINOMIAL = new ResourceLocation("binomial");

    int generateInt(Random rand);

    ResourceLocation getType();
}
