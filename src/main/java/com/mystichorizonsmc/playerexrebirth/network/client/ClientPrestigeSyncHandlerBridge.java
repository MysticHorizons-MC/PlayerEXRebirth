package com.mystichorizonsmc.playerexrebirth.network.client;

public interface ClientPrestigeSyncHandlerBridge {
    void handle(int effectiveMaxLevel, int prestigeLevel);
}
