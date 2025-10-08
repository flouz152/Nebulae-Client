package net.minecraft.network.login;

import net.minecraft.network.INetHandler;
import net.minecraft.network.login.client.CCustomPayloadLoginPacket;
import net.minecraft.network.login.client.CEncryptionResponsePacket;
import net.minecraft.network.login.client.CLoginStartPacket;

public interface IServerLoginNetHandler extends INetHandler
{
// leaked by itskekoff; discord.gg/sk3d Dn3AiA2o
    void processLoginStart(CLoginStartPacket packetIn);

    void processEncryptionResponse(CEncryptionResponsePacket packetIn);

    void processCustomPayloadLogin(CCustomPayloadLoginPacket p_209526_1_);
}
