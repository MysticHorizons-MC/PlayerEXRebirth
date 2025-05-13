package com.mystichorizonsmc.playerexrebirth.client;

import com.bibireden.playerex.registry.PlayerEXMenuRegistry;
import com.mystichorizonsmc.playerexrebirth.PlayerExRebirth;
import com.mystichorizonsmc.playerexrebirth.client.config.ClientPrestigeConfig;
import com.mystichorizonsmc.playerexrebirth.client.network.PrestigeSyncS2CHandler;
import com.mystichorizonsmc.playerexrebirth.client.toast.PrestigeToastHandler;
import com.mystichorizonsmc.playerexrebirth.client.ui.PrestigeMenuTab;
import com.mystichorizonsmc.playerexrebirth.network.packet.PrestigeToastS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.resources.ResourceLocation;

public class PlayerExRebirthClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        PlayerExRebirth.LOGGER.info("[{}] Initializing client-side...", PlayerExRebirth.MOD_ID);

        ClientPrestigeConfig.load();
        PrestigeSyncS2CHandler.register();

        PlayerExRebirth.NETWORK.registerClientbound(
                PrestigeToastS2CPacket.class,
                (packet, access) -> access.runtime().execute(() ->
                        PrestigeToastHandler.showToast(packet.newPrestigeLevel())
                )
        );

        try {
            PlayerEXMenuRegistry.register(
                    new ResourceLocation(PlayerExRebirth.MOD_ID, "prestige_tab"),
                    PrestigeMenuTab.class
            );
        } catch (Exception e) {
            PlayerExRebirth.LOGGER.error("Failed to register prestige tab", e);
        }
    }
}
