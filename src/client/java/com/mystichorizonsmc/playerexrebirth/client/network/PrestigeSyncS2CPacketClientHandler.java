package com.mystichorizonsmc.playerexrebirth.client.network;

import com.mystichorizonsmc.playerexrebirth.client.data.ClientPrestigeData;
import com.mystichorizonsmc.playerexrebirth.client.ui.PrestigeScreen;
import com.mystichorizonsmc.playerexrebirth.network.client.ClientPrestigeSyncHandlerBridge;
import net.minecraft.client.Minecraft;

public class PrestigeSyncS2CPacketClientHandler implements ClientPrestigeSyncHandlerBridge {

    @Override
    public void handle(int effectiveMaxLevel, int prestigeLevel) {
        ClientPrestigeData.setCurrentPrestigeLevel(prestigeLevel);
        ClientPrestigeData.setEffectiveMaxLevel(effectiveMaxLevel);

        Minecraft.getInstance().execute(() -> {
            Minecraft.getInstance().setScreen(new PrestigeScreen());
        });
    }
}
