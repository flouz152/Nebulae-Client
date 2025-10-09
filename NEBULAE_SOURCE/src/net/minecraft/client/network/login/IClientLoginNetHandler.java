package net.minecraft.client.network.login;

import net.minecraft.network.INetHandler;
import net.minecraft.network.login.server.SCustomPayloadLoginPacket;
import net.minecraft.network.login.server.SDisconnectLoginPacket;
import net.minecraft.network.login.server.SEnableCompressionPacket;
import net.minecraft.network.login.server.SEncryptionRequestPacket;
import net.minecraft.network.login.server.SLoginSuccessPacket;

public interface IClientLoginNetHandler extends INetHandler
{
// leaked by itskekoff; discord.gg/sk3d tzUDpe9O
    void handleEncryptionRequest(SEncryptionRequestPacket packetIn);

    void handleLoginSuccess(SLoginSuccessPacket packetIn);

    void handleDisconnect(SDisconnectLoginPacket packetIn);

    void handleEnableCompression(SEnableCompressionPacket packetIn);

    void handleCustomPayloadLogin(SCustomPayloadLoginPacket packetIn);
}
