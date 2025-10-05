package net.optifine.render;

import net.optifine.util.IntExpiringCache;
import net.optifine.util.RandomUtils;

public class QuadVertexPositions extends IntExpiringCache<VertexPosition[]>
{
// leaked by itskekoff; discord.gg/sk3d 4toZxvAT
    public QuadVertexPositions()
    {
        super(60000 + RandomUtils.getRandomInt(10000));
    }

    protected VertexPosition[] make()
    {
        return new VertexPosition[] {new VertexPosition(), new VertexPosition(), new VertexPosition(), new VertexPosition()};
    }
}
