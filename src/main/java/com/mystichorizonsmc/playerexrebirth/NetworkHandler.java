package com.mystichorizonsmc.playerexrebirth;


import com.mystichorizonsmc.playerexrebirth.network.packet.ClientPrestigeRequest;
import io.wispforest.owo.network.OwoNetChannel;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class NetworkHandler {

    public static void registerCommon(OwoNetChannel channel) {
        // Serverbound packets must be registered on both sides
        channel.registerServerbound(ClientPrestigeRequest.class, ClientPrestigeRequest::handle);
    }
}
