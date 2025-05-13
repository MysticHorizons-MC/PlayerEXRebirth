package com.mystichorizonsmc.playerexrebirth.client;

import com.mystichorizonsmc.playerexrebirth.client.toast.PrestigeToastHandler;
import com.mystichorizonsmc.playerexrebirth.network.packet.PrestigeToastS2CPacket;
import io.wispforest.owo.network.OwoNetChannel;

public class ClientNetworkHandler {

    public static void registerClient(OwoNetChannel channel) {
        // Actual clientbound packet logic (toast UI)
        channel.registerClientbound(
                PrestigeToastS2CPacket.class,
                (packet, access) -> access.runtime().execute(() ->
                        PrestigeToastHandler.showToast(packet.newPrestigeLevel())
                )
        );
    }

}
