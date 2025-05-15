package com.mystichorizonsmc.playerexrebirth.network;

import com.mystichorizonsmc.playerexrebirth.PlayerExRebirth;
import com.mystichorizonsmc.playerexrebirth.component.ModComponents;
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
        int maxLevel = PrestigeManager.getEffectiveMaxLevel(player);
        int currentPrestige = ModComponents.PRESTIGE.get(player).getPrestigeLevel();

        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(maxLevel);
        buf.writeVarInt(currentPrestige);

        ServerPlayNetworking.send(player, ID, buf);
    }

    public static void registerJoinSync() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (ModComponents.PRESTIGE == null) {
                PlayerExRebirth.LOGGER.error("[PlayerExRebirth] PRESTIGE component was null during JOIN sync!");
                return;
            }

            send(handler.player);
        });
    }
}
