package net.minecraft.tileentity;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class DropperTileEntity extends DispenserTileEntity
{
// leaked by itskekoff; discord.gg/sk3d eyE8j8ck
    public DropperTileEntity()
    {
        super(TileEntityType.DROPPER);
    }

    protected ITextComponent getDefaultName()
    {
        return new TranslationTextComponent("container.dropper");
    }
}
