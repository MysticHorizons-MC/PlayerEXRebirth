package com.mystichorizonsmc.playerexrebirth.client;


import com.mystichorizonsmc.playerexrebirth.PlayerExRebirth;
import com.mystichorizonsmc.playerexrebirth.client.config.ClientPrestigeConfig;
import com.mystichorizonsmc.playerexrebirth.client.network.PrestigeSyncS2CHandler;
import com.mystichorizonsmc.playerexrebirth.client.ui.PrestigeScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;
import org.lwjgl.glfw.GLFW;

public class PlayerExRebirthClient implements ClientModInitializer {

    private static KeyMapping openPrestigeScreenKey;

    @Override
    public void onInitializeClient() {
        PlayerExRebirth.LOGGER.info("[{}] Initializing client-side...", PlayerExRebirth.MOD_ID);

        ClientPrestigeConfig.load();
        PrestigeSyncS2CHandler.register();

        // Keybind to open prestige screen
        openPrestigeScreenKey = new KeyMapping("key.playerexrebirth.open_prestige", GLFW.GLFW_KEY_END, "key.categories.ui");
        net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(openPrestigeScreenKey);

        // Tick event to check keybind
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openPrestigeScreenKey.consumeClick()) {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player != null) {
                    String screenPath = "playerexrebirth:prestige_screen";
                    PlayerExRebirth.LOGGER.info("[Client] Opening PrestigeScreen from UI path: {}", screenPath);

                    try {
                        Minecraft.getInstance().setScreen(new PrestigeScreen());
                    } catch (Exception e) {
                        PlayerExRebirth.LOGGER.error("[Client] Failed to open PrestigeScreen from '{}'", screenPath, e);
                    }
                }
            }
        });
    }
}
