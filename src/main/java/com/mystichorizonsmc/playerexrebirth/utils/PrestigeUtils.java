package com.mystichorizonsmc.playerexrebirth.utils;

import com.mystichorizonsmc.playerexrebirth.config.PrestigeConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class PrestigeUtils {

    public static void runPrestigeCommands(ServerPlayer player, int prestigeLevel) {
        MinecraftServer server = player.server;

        for (String rawCommand : PrestigeConfig.prestigeCommands) {
            String command = rawCommand
                    .replace("<player>", player.getGameProfile().getName())
                    .replace("<prestige>", String.valueOf(prestigeLevel));

            server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), command);
        }
    }
}

