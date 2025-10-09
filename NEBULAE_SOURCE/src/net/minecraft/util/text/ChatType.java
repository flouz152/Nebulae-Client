package net.minecraft.util.text;

public enum ChatType
{
// leaked by itskekoff; discord.gg/sk3d 2Hjrl7pf
    CHAT((byte)0, false),
    SYSTEM((byte)1, true),
    GAME_INFO((byte)2, true);

    private final byte id;
    private final boolean interrupts;

    private ChatType(byte id, boolean interrupts)
    {
        this.id = id;
        this.interrupts = interrupts;
    }

    public byte getId()
    {
        return this.id;
    }

    public static ChatType byId(byte idIn)
    {
        for (ChatType chattype : values())
        {
            if (idIn == chattype.id)
            {
                return chattype;
            }
        }

        return CHAT;
    }

    public boolean getInterrupts()
    {
        return this.interrupts;
    }
}
