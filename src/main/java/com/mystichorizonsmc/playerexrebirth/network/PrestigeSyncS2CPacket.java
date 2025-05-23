package com.mystichorizonsmc.playerexrebirth.network;

import com.mystichorizonsmc.playerexrebirth.PlayerExRebirth;
import com.mystichorizonsmc.playerexrebirth.network.client.ClientPrestigeSyncHandlerBridge;
import io.wispforest.owo.network.ClientAccess;
import io.wispforest.owo.network.ServerAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public record PrestigeSyncS2CPacket(int effectiveMaxLevel, int prestigeLevel) {

    private static final String CLIENT_HANDLER_CLASS = "com.mystichorizonsmc.playerexrebirth.client.network.PrestigeSyncS2CPacketClientHandler";

    public void handle(ClientAccess access) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
            PlayerExRebirth.LOGGER.warn("[ClientSync] Packet received on non-client environment. Skipping.");
            return;
        }

        try {
            Class<?> clazz = Class.forName(CLIENT_HANDLER_CLASS);
            ClientPrestigeSyncHandlerBridge handler = (ClientPrestigeSyncHandlerBridge) clazz.getDeclaredConstructor().newInstance();
            handler.handle(this.effectiveMaxLevel, this.prestigeLevel);
        } catch (Exception e) {
            PlayerExRebirth.LOGGER.error("[ClientSync] Failed to invoke client handler for PrestigeSyncS2CPacket", e);
        }
    }

    // Called during encoding
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(effectiveMaxLevel);
        buf.writeVarInt(prestigeLevel);
    }

    // Called during decoding
    public static PrestigeSyncS2CPacket decode(FriendlyByteBuf buf) {
        return new PrestigeSyncS2CPacket(buf.readVarInt(), buf.readVarInt());
    }

    // Util method to send the packet
    public static void send(ServerPlayer player, int effectiveMaxLevel, int prestigeLevel) {
        PlayerExRebirth.NETWORK.serverHandle(player).send(new PrestigeSyncS2CPacket(effectiveMaxLevel, prestigeLevel));
    }
}
