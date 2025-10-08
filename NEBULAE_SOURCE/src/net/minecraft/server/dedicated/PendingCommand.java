package net.minecraft.server.dedicated;

import net.minecraft.command.CommandSource;

public class PendingCommand
{
// leaked by itskekoff; discord.gg/sk3d OjmEirii
    public final String command;
    public final CommandSource sender;

    public PendingCommand(String p_i48147_1_, CommandSource p_i48147_2_)
    {
        this.command = p_i48147_1_;
        this.sender = p_i48147_2_;
    }
}
