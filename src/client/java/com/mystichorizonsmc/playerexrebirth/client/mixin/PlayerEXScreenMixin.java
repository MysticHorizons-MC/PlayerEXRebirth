package com.mystichorizonsmc.playerexrebirth.client.mixin;

import com.bibireden.playerex.ui.PlayerEXScreen;
import com.mystichorizonsmc.playerexrebirth.client.ui.PrestigeScreen;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEXScreen.class)
@Environment(EnvType.CLIENT)
public abstract class PlayerEXScreenMixin {

    @Inject(method = "*", at = @At("TAIL"))
    private void injectPrestigeButton(FlowLayout root, CallbackInfo ci) {
        // Avoid adding the button multiple times
        boolean alreadyAdded = root.children().stream().anyMatch(component ->
                component instanceof ButtonComponent button && (button.getMessage().getString().equals(Component.translatable("playerexrebirth.ui.prestige_button").getString()))
                );

        if (alreadyAdded) return;

        ButtonComponent prestigeButton = (ButtonComponent) Components.button(
                        Component.translatable("playerexrebirth.ui.prestige_button"),
                        button -> {
                            LocalPlayer player = Minecraft.getInstance().player;
                            if (player != null) {
                                Minecraft.getInstance().setScreen(new PrestigeScreen());
                            }
                        }
                ).sizing(Sizing.fixed(100), Sizing.fixed(20))
                .tooltip(Component.translatable("playerexrebirth.ui.tooltip.prestige_button"))
                .margins(Insets.of(6, 0, 0, 0));

        root.child(prestigeButton);
    }
}
