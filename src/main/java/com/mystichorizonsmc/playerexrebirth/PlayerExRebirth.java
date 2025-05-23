package com.mystichorizonsmc.playerexrebirth;

import com.mystichorizonsmc.playerexrebirth.component.ModComponents;
import com.mystichorizonsmc.playerexrebirth.config.PrestigeConfig;
import com.mystichorizonsmc.playerexrebirth.network.PrestigeSyncS2CPacket;
import com.mystichorizonsmc.playerexrebirth.prestige.PrestigeManager;
import io.wispforest.owo.network.OwoNetChannel;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
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

        // Join sync
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> server.execute(() -> {
            var player = handler.player;
            var component = ModComponents.get().get(player);

            if (component == null) {
                PlayerExRebirth.LOGGER.error("[JoinSync] PRESTIGE component was null during JOIN sync for '{}'", player.getGameProfile().getName());
                return;
            }

            int prestigeLevel = component.getPrestigeLevel();
            int maxLevel = PrestigeManager.getEffectiveMaxLevel(player);

            PrestigeSyncS2CPacket.send(player, maxLevel, prestigeLevel);
        }));

        // Config
        PrestigeConfig.load();
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
