package net.minecraft.resources;

import java.io.Closeable;
import java.io.InputStream;
import javax.annotation.Nullable;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;

public interface IResource extends Closeable
{
// leaked by itskekoff; discord.gg/sk3d 0T7WT9fn
    ResourceLocation getLocation();

    InputStream getInputStream();

    @Nullable
    <T> T getMetadata(IMetadataSectionSerializer<T> serializer);

    String getPackName();
}
