package net.minecraft.block;

public class MelonBlock extends StemGrownBlock
{
// leaked by itskekoff; discord.gg/sk3d 4oFmijoE
    protected MelonBlock(AbstractBlock.Properties builder)
    {
        super(builder);
    }

    public StemBlock getStem()
    {
        return (StemBlock)Blocks.MELON_STEM;
    }

    public AttachedStemBlock getAttachedStem()
    {
        return (AttachedStemBlock)Blocks.ATTACHED_MELON_STEM;
    }
}
