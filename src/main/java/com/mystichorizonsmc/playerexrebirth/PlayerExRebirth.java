package com.mystichorizonsmc.playerexrebirth;

import com.mystichorizonsmc.playerexrebirth.config.PrestigeConfig;
import com.mystichorizonsmc.playerexrebirth.network.PrestigeSyncS2CPacket;
import io.wispforest.owo.network.OwoNetChannel;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerExRebirth implements ModInitializer {

    public static final String MOD_ID = "playerexrebirth";
    public static final Logger LOGGER = LoggerFactory.getLogger("PlayerEx Rebirth");
    public static final OwoNetChannel NETWORK = OwoNetChannel.create(id("playerexrebirthnet"));

    @Override
    public void onInitialize() {
        LOGGER.info("[{}] Initializing...", MOD_ID);

        // Network packets
        NetworkHandler.registerCommon(NETWORK);
        PrestigeSyncS2CPacket.registerJoinSync();

        // Config
        PrestigeConfig.load();
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
