package net.minecraft.client.gui.fonts.providers;

import javax.annotation.Nullable;
import net.minecraft.resources.IResourceManager;

public interface IGlyphProviderFactory
{
// leaked by itskekoff; discord.gg/sk3d xtH9x1wr
    @Nullable
    IGlyphProvider create(IResourceManager resourceManagerIn);
}
