package net.optifine.util;

import java.util.Random;

public class RandomUtils
{
// leaked by itskekoff; discord.gg/sk3d 18e9WiP1
    private static final Random random = new Random();

    public static Random getRandom()
    {
        return random;
    }

    public static int getRandomInt(int bound)
    {
        return random.nextInt(bound);
    }
}
