package com.mystichorizonsmc.playerexrebirth.client.toast;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class PrestigeToastHandler {
    public static void showToast(int prestigeLevel) {
        var client = Minecraft.getInstance();
        if (client != null && client.getToasts() != null) {
            client.getToasts().addToast(new SystemToast(
                    SystemToast.SystemToastIds.TUTORIAL_HINT,
                    Component.literal("§6You Have Prestiged!"),
                    Component.literal("§ePrestige Level: §l" + prestigeLevel)
            ));
        }
    }
}
