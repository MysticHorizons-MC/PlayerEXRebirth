package com.mystichorizonsmc.playerexrebirth.client.ui;

import com.bibireden.playerex.ui.components.ProgressBarComponent;
import com.mystichorizonsmc.playerexrebirth.PlayerExRebirth;
import com.mystichorizonsmc.playerexrebirth.client.config.ClientPrestigeConfig;
import com.mystichorizonsmc.playerexrebirth.client.data.ClientPrestigeData;
import com.mystichorizonsmc.playerexrebirth.config.PrestigeConfig;
import com.mystichorizonsmc.playerexrebirth.network.packet.ClientPrestigeRequest;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PrestigeScreen extends BaseUIModelScreen<FlowLayout> {

    public PrestigeScreen() {
        super(FlowLayout.class, DataSource.asset(ResourceLocation.tryParse(PlayerExRebirth.MOD_ID + ":prestige_screen")));
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        int prestigeLevel = ClientPrestigeData.getCurrentPrestigeLevel();
        int requiredLevel = ClientPrestigeData.getMaxPrestigeLevel();

        // --- Prestige Label ---
        LabelComponent prestigeLabel = rootComponent.childById(LabelComponent.class, "prestige_label");
        prestigeLabel.text(Component.literal(ClientPrestigeConfig.tooltipFormat.replace("<level>", String.valueOf(prestigeLevel))));

        // --- Bonuses ---
        FlowLayout bonusList = rootComponent.childById(FlowLayout.class, "bonuses_list");
        bonusList.clearChildren(); // Clear stale content if reused
        PrestigeConfig.rewardMultipliers.forEach((key, multiplier) -> {
            double value = multiplier * prestigeLevel;
            String formatted = switch (key) {
                case "constitution" -> "§a+%.2f Constitution".formatted(value);
                case "health_regeneration" -> "§d+%.2f Health Regen".formatted(value);
                case "lifesteal" -> "§c+%.2f Lifesteal".formatted(value);
                case "dexterity" -> "§b+%.2f Dexterity".formatted(value);
                case "melee_critical_chance" -> "§e+%.2f Melee Crit Chance".formatted(value);
                default -> "§7+%.2f %s".formatted(value, key.replace("_", " "));
            };
            bonusList.child(Components.label(Component.literal("§7- " + formatted)));
        });

        // --- Progress Bar ---
        ProgressBarComponent progressBar = rootComponent.childById(ProgressBarComponent.class, "prestige_progress");
        if (progressBar != null) {
            float percent = (float) player.experienceLevel / requiredLevel;
            progressBar.setPercentage(Math.min(percent, 1.0f));
        }

        // --- Eligibility ---
        boolean canPrestige = player.experienceLevel >= requiredLevel;

        // --- Warning Label ---
        LabelComponent warning = rootComponent.childById(LabelComponent.class, "requirement_warning");
        warning.text(canPrestige ? Component.empty() : Component.literal("§cYou must reach level " + requiredLevel + " to prestige."));

        // --- Toast ---
        BoxComponent toastBox = rootComponent.childById(BoxComponent.class, "prestige_toast_box");
        LabelComponent toastLabel = rootComponent.childById(LabelComponent.class, "prestige_toast");

        if (canPrestige && ClientPrestigeData.shouldShowToast()) {
            animateToastSlide(toastBox, toastLabel, "§6🎉 You can now prestige!");
            ClientPrestigeData.setToastShown();
        } else {
            toastLabel.text(Component.empty());
        }

        // --- Prestige Button ---
        ButtonComponent prestigeButton = rootComponent.childById(ButtonComponent.class, "prestige_button");
        prestigeButton.onPress(button -> Minecraft.getInstance().setScreen(new ConfirmScreen(
                confirmed -> {
                    if (confirmed) {
                        ClientPrestigeRequest.send(); // Send a client-to-server packet to invoke tryPrestige()
                    } else {
                        Minecraft.getInstance().setScreen(new PrestigeScreen());
                    }
                },
                Component.literal("Are you sure you want to prestige?"),
                Component.literal("§cThis will reset your level and stats!")
        )));

        // --- Glowing Border ---
        BoxComponent box = rootComponent.childById(BoxComponent.class, "prestige_button_box");

        if (canPrestige) {
            box.fill(true);
            box.direction(BoxComponent.GradientDirection.TOP_TO_BOTTOM);

            float time = (System.currentTimeMillis() % 1000L) / 1000f;
            float alpha = 0.4f + 0.2f * (float) Math.sin(time * 2 * Math.PI);

            int argb = ((int)(alpha * 255) << 24) | 0xFFD700;
            box.startColor().set(Color.ofArgb(argb));
            box.endColor().set(Color.ofArgb(argb));
        } else {
            box.fill(false);
        }
    }

    private void animateToastSlide(BoxComponent toastBox, LabelComponent toastLabel, String message) {
        // Move toast offscreen initially
        toastBox.margins().set(Insets.of(8, 999, 0, 0));
        toastLabel.text(Component.literal(message));

        // Animate into view over 200ms with smooth easing
        toastBox.margins().animate(200, Easing.SINE, Insets.of(8, 10, 0, 0));

        // Slide out after 5 seconds
        new Thread(() -> {
            try {
                Thread.sleep(5000); // Wait 5s
                Minecraft.getInstance().execute(() -> {
                    toastBox.margins().animate(250, Easing.SINE, Insets.of(8, 999, 0, 0));
                    toastLabel.text(Component.empty());
                });
            } catch (InterruptedException ignored) {}
        }).start();
    }

}
