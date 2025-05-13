package com.mystichorizonsmc.playerexrebirth;


import com.mystichorizonsmc.playerexrebirth.network.packet.ClientPrestigeRequest;
import com.mystichorizonsmc.playerexrebirth.network.packet.PrestigeToastS2CPacket;
import io.wispforest.owo.network.OwoNetChannel;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class NetworkHandler {

    public static void registerCommon(OwoNetChannel channel) {
        // Serverbound packets must be registered on both sides
        channel.registerServerbound(ClientPrestigeRequest.class, ClientPrestigeRequest::handle);

        // Only register the dummy clientbound handler if on dedicated server
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            channel.registerClientbound(PrestigeToastS2CPacket.class, (packet, access) -> {});
        }
    }
}
