package com.mystichorizonsmc.playerexrebirth.network;

import com.mystichorizonsmc.playerexrebirth.PlayerExRebirth;
import com.mystichorizonsmc.playerexrebirth.component.ModComponents;
import com.mystichorizonsmc.playerexrebirth.component.PrestigeComponent;
import com.mystichorizonsmc.playerexrebirth.prestige.PrestigeManager;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class PrestigeSyncS2CPacket {

    public static final ResourceLocation ID = new ResourceLocation("playerexrebirth", "sync_prestige");

    public static void send(ServerPlayer player) {
        if (ModComponents.PRESTIGE == null) {
            PlayerExRebirth.LOGGER.error("[Sync] ModComponents.PRESTIGE is null — component was never registered!");
            return;
        }

        PrestigeComponent prestige = ModComponents.PRESTIGE.get(player);
        if (prestige == null) {
            PlayerExRebirth.LOGGER.error("[Sync] Failed to retrieve PrestigeComponent for player '{}'", player.getGameProfile().getName());
            return;
        }

        int currentPrestige = prestige.getPrestigeLevel();
        int effectiveMaxLevel = PrestigeManager.getEffectiveMaxLevel(player);

        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(effectiveMaxLevel);
        buf.writeVarInt(currentPrestige);
        buf.writeVarInt(0); // Placeholder

        ServerPlayNetworking.send(player, ID, buf);

        PlayerExRebirth.LOGGER.debug("[Sync] Sent prestige data to '{}': level={}, prestige={}", player.getGameProfile().getName(), effectiveMaxLevel, currentPrestige);
    }

    public static void registerJoinSync() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (ModComponents.PRESTIGE == null) {
                PlayerExRebirth.LOGGER.error("[JoinSync] PRESTIGE component was null during JOIN sync! Component may not be registered.");
                return;
            }

            send(handler.player);
        });
    }
}
