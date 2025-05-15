package com.mystichorizonsmc.playerexrebirth.client;

import com.bibireden.playerex.registry.PlayerEXMenuRegistry;
import com.mystichorizonsmc.playerexrebirth.PlayerExRebirth;
import com.mystichorizonsmc.playerexrebirth.client.config.ClientPrestigeConfig;
import com.mystichorizonsmc.playerexrebirth.client.network.PrestigeSyncS2CHandler;
import com.mystichorizonsmc.playerexrebirth.client.ui.PrestigeMenuTab;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.resources.ResourceLocation;

public class PlayerExRebirthClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        PlayerExRebirth.LOGGER.info("[{}] Initializing client-side...", PlayerExRebirth.MOD_ID);

        ClientPrestigeConfig.load();
        PrestigeSyncS2CHandler.register();

        // Network Sync
        ClientNetworkHandler.registerClient(PlayerExRebirth.NETWORK);

        try {
            PlayerEXMenuRegistry.register(
                    new ResourceLocation("playerex", "rebirth_prestige"), // use playerex as namespace
                    PrestigeMenuTab.class
            );
        } catch (Exception e) {
            PlayerExRebirth.LOGGER.error("Failed to register prestige tab", e);
        }

        PlayerExRebirth.LOGGER.info("Registered menu tab IDs:");
        PlayerEXMenuRegistry.getIds().forEach(id -> PlayerExRebirth.LOGGER.info("- {}.ui.menu.{}", id.getNamespace(), id.getPath()));
    }
}
