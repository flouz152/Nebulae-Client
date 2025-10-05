package net.minecraftforge.common.property;

public interface IUnlistedProperty<V>
{
// leaked by itskekoff; discord.gg/sk3d 5eJaotdL
    String getName();

    boolean isValid(V var1);

    Class<V> getType();

    String valueToString(V var1);
}
