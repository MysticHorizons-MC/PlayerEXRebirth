package com.mystichorizonsmc.playerexrebirth.network.packet;

import io.wispforest.owo.network.ClientAccess;
import io.wispforest.owo.network.ServerAccess;
import io.wispforest.owo.network.serialization.RecordSerializer;
import net.minecraft.resources.ResourceLocation;
import com.mystichorizonsmc.playerexrebirth.PlayerExRebirth;
import com.mystichorizonsmc.playerexrebirth.prestige.PrestigeManager;
import net.minecraft.server.level.ServerPlayer;

public record ClientPrestigeRequest() {

    public static final ResourceLocation ID = PlayerExRebirth.id("client_prestige_request");
    public static final RecordSerializer<ClientPrestigeRequest> SERIALIZER = RecordSerializer.create(ClientPrestigeRequest.class);

    public static void handle(ClientPrestigeRequest packet, ServerAccess access) {
        ServerPlayer player = access.player();
        PrestigeManager.tryPrestige(player);
    }

    public static void send() {
        PlayerExRebirth.NETWORK.clientHandle().send(new ClientPrestigeRequest());
    }
}
