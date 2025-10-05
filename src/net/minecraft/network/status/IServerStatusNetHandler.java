package net.minecraft.network.status;

import net.minecraft.network.INetHandler;
import net.minecraft.network.status.client.CPingPacket;
import net.minecraft.network.status.client.CServerQueryPacket;

public interface IServerStatusNetHandler extends INetHandler
{
// leaked by itskekoff; discord.gg/sk3d QGGK1moN
    void processPing(CPingPacket packetIn);

    void processServerQuery(CServerQueryPacket packetIn);
}
