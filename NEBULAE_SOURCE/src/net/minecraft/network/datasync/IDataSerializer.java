package net.minecraft.network.datasync;

import net.minecraft.network.PacketBuffer;

public interface IDataSerializer<T>
{
// leaked by itskekoff; discord.gg/sk3d KsFA99gb
    void write(PacketBuffer buf, T value);

    T read(PacketBuffer buf);

default DataParameter<T> createKey(int id)
    {
        return new DataParameter<>(id, this);
    }

    T copyValue(T value);
}
