package com.mystichorizonsmc.playerexrebirth.network.packet;

import io.wispforest.owo.network.ServerAccess;
import com.mystichorizonsmc.playerexrebirth.PlayerExRebirth;
import com.mystichorizonsmc.playerexrebirth.prestige.PrestigeManager;
import net.minecraft.server.level.ServerPlayer;

public record ClientPrestigeRequest() {

    public static void handle(ClientPrestigeRequest packet, ServerAccess access) {
        ServerPlayer player = access.player();
        PrestigeManager.tryPrestige(player);
    }

    public static void send() {
        PlayerExRebirth.NETWORK.clientHandle().send(new ClientPrestigeRequest());
    }
}
