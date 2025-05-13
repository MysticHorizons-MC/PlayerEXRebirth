package com.mystichorizonsmc.playerexrebirth;

import com.mystichorizonsmc.playerexrebirth.config.PrestigeConfig;
import com.mystichorizonsmc.playerexrebirth.network.PrestigeSyncS2CPacket;
import com.mystichorizonsmc.playerexrebirth.network.packet.ClientPrestigeRequest;
import io.wispforest.owo.network.OwoNetChannel;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerExRebirth implements ModInitializer {

    public static final String MOD_ID = "playerexrebirth";
    public static final Logger LOGGER = LoggerFactory.getLogger("PlayerEx Rebirth");
    public static final OwoNetChannel NETWORK = OwoNetChannel.create(id("main"));

    @Override
    public void onInitialize() {
        LOGGER.info("[{}] Initializing...", MOD_ID);

        NETWORK.registerServerbound(ClientPrestigeRequest.class, ClientPrestigeRequest::handle);
        PrestigeSyncS2CPacket.registerJoinSync();
        PrestigeConfig.load();
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
