package net.minecraft.client.gui.spectator;

import java.util.List;
import net.minecraft.util.text.ITextComponent;

public interface ISpectatorMenuView
{
// leaked by itskekoff; discord.gg/sk3d HgU0PE37
    List<ISpectatorMenuObject> getItems();

    ITextComponent getPrompt();
}
