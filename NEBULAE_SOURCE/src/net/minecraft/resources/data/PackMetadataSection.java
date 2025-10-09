package net.minecraft.resources.data;

import net.minecraft.util.text.ITextComponent;

public class PackMetadataSection
{
// leaked by itskekoff; discord.gg/sk3d 6sY9PfEa
    public static final PackMetadataSectionSerializer SERIALIZER = new PackMetadataSectionSerializer();
    private final ITextComponent description;
    private final int packFormat;

    public PackMetadataSection(ITextComponent packDescriptionIn, int packFormatIn)
    {
        this.description = packDescriptionIn;
        this.packFormat = packFormatIn;
    }

    public ITextComponent getDescription()
    {
        return this.description;
    }

    public int getPackFormat()
    {
        return this.packFormat;
    }
}
