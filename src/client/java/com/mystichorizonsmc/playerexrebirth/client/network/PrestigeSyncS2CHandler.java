package com.mystichorizonsmc.playerexrebirth.client.network;

import com.mystichorizonsmc.playerexrebirth.client.data.ClientPrestigeData;
import com.mystichorizonsmc.playerexrebirth.network.PrestigeSyncS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;

public class PrestigeSyncS2CHandler {

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(PrestigeSyncS2CPacket.ID, (client, handler, buf, responseSender) -> {
            int effectiveMaxLevel = buf.readVarInt();
            int prestige = buf.readVarInt();
//            int ignoredRequiredLevel = buf.readVarInt(); // Legacy — now ignored

            Minecraft.getInstance().execute(() -> {
                ClientPrestigeData.setEffectiveMaxLevel(effectiveMaxLevel);
                ClientPrestigeData.setCurrentPrestigeLevel(prestige);
            });
        });
    }
}
