package net.minecraft.network;

import net.minecraft.util.text.ITextComponent;

public interface INetHandler
{
// leaked by itskekoff; discord.gg/sk3d uVO4BX9l
    /**
     * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
     */
    void onDisconnect(ITextComponent reason);

    /**
     * Returns this the NetworkManager instance registered with this NetworkHandlerPlayClient
     */
    NetworkManager getNetworkManager();
}
