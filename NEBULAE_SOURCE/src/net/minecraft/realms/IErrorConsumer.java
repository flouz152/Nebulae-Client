package net.minecraft.realms;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public interface IErrorConsumer
{
// leaked by itskekoff; discord.gg/sk3d BWFMwKs6
    void func_230434_a_(ITextComponent p_230434_1_);

default void func_237703_a_(String p_237703_1_)
    {
        this.func_230434_a_(new StringTextComponent(p_237703_1_));
    }
}
