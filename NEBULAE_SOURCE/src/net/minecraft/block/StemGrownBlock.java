package net.minecraft.block;

public abstract class StemGrownBlock extends Block
{
// leaked by itskekoff; discord.gg/sk3d TRJMlipm
    public StemGrownBlock(AbstractBlock.Properties properties)
    {
        super(properties);
    }

    public abstract StemBlock getStem();

    public abstract AttachedStemBlock getAttachedStem();
}
