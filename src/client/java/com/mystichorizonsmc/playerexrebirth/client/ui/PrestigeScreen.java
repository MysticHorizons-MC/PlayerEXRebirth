package com.mystichorizonsmc.playerexrebirth.client.ui;

import com.bibireden.data_attributes.api.DataAttributesAPI;
import com.bibireden.playerex.PlayerEX;
import com.bibireden.playerex.api.PlayerEXAPI;
import com.bibireden.playerex.api.attribute.PlayerEXAttributes;
import com.bibireden.playerex.ui.components.ProgressBarComponent;
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

import java.util.Map;

public class PrestigeScreen extends BaseUIModelScreen<FlowLayout> {

    private long toastShownAt = -1;

    public PrestigeScreen() {
        super(FlowLayout.class, DataSource.asset(ResourceLocation.tryParse("playerexrebirth:prestige_screen")));
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        int playerExLevel = DataAttributesAPI.getValue(PlayerEXAttributes.LEVEL, player)
                .map(Double::intValue)
                .orElse(0);

        int prestigeLevel = ClientPrestigeData.getCurrentPrestigeLevel();
        int requiredLevel = ClientPrestigeData.getEffectiveMaxLevel();
        int maxPrestige = PrestigeConfig.maxPrestige;
        float progress = (playerExLevel + player.experienceProgress) / requiredLevel;

        rootComponent.childById(LabelComponent.class, "prestige_label")
                .text(Component.literal("§7Prestige: §a" + prestigeLevel + " §7/ §c" + maxPrestige));
        rootComponent.childById(LabelComponent.class, "level_label")
                .text(Component.literal("§7Level: §b" + playerExLevel + " §7/ §3" + requiredLevel));

        FlowLayout bonusList = rootComponent.childById(FlowLayout.class, "bonuses_list");
        PrestigeConfig.rewardMultipliers.forEach((key, multiplier) -> {
            double value = multiplier * prestigeLevel;
            String formatted = switch (key) {
                case "constitution" -> "§a+%.2f Constitution".formatted(value);
                case "strength"     -> "§c+%.2f Strength".formatted(value);
                case "dexterity"    -> "§b+%.2f Dexterity".formatted(value);
                case "intelligence" -> "§d+%.2f Intelligence".formatted(value);
                case "luckiness"    -> "§e+%.2f Luckiness".formatted(value);
                case "focus"        -> "§6+%.2f Focus".formatted(value);
                default             -> null; // skip unsupported keys
            };
            if (formatted == null) return;

            var bonusEntry = this.model.expandTemplate(LabelComponent.class,
                    "bonus-entry@playerexrebirth:prestige_screen",
                    Map.of("bonus-text", "§7- " + formatted));
            bonusList.child(bonusEntry);
        });

        FlowLayout progressLayout = rootComponent.childById(FlowLayout.class, "prestige_progress");

        float clampedProgress = Math.min(progress, 1.0f);
        int totalWidth = 300;
        int filledWidth = (int) (clampedProgress * totalWidth);

        // Create the filled bar (green)
        BoxComponent fillBar = new BoxComponent(
                Sizing.fixed(filledWidth),
                Sizing.fixed(8)
        ).color(Color.ofRgb(0x4CAF50)).fill(true);

        // Optional tooltip
        fillBar.tooltip(Component.literal("%.1f%% to Prestige".formatted(clampedProgress * 100)));

        // Clear old children if re-rendering
        progressLayout.clearChildren();

        // Add the fill to the layout
        progressLayout.child(fillBar);

        boolean canPrestige = playerExLevel >= requiredLevel;

        LabelComponent warning = rootComponent.childById(LabelComponent.class, "requirement_warning");
        warning.text(canPrestige ? Component.empty()
                : Component.translatable("playerexrebirth.ui.requirement_warning", requiredLevel));

        ButtonComponent prestigeButton = rootComponent.childById(ButtonComponent.class, "prestige_button");
        prestigeButton.active(canPrestige);
        prestigeButton.tooltip(Component.translatable("playerexrebirth.ui.tooltip.prestige_button"));
        prestigeButton.onPress(button -> Minecraft.getInstance().setScreen(new ConfirmScreen(
                confirmed -> {
                    if (confirmed) {
                        ClientPrestigeRequest.send();
                        Minecraft.getInstance().setScreen(null);
                    } else {
                        Minecraft.getInstance().setScreen(new PrestigeScreen());
                    }
                },
                Component.translatable("playerexrebirth.ui.confirm_title"),
                Component.translatable("playerexrebirth.ui.confirm_warning")
        )));

        rootComponent.childById(ButtonComponent.class, "exit_button")
                .onPress(button -> Minecraft.getInstance().setScreen(null));

        FlowLayout toastBox = rootComponent.childById(FlowLayout.class, "prestige_toast_box");
        LabelComponent toastLabel = rootComponent.childById(LabelComponent.class, "prestige_toast");

        if (canPrestige && ClientPrestigeData.shouldShowToast()) {
            animateToastSlide(toastBox, toastLabel, Component.translatable("playerexrebirth.ui.toast_ready"));
            ClientPrestigeData.setToastShown();
        } else {
            toastLabel.text(Component.empty());
        }
    }

    private void animateToastSlide(FlowLayout toastBox, LabelComponent toastLabel, Component message) {
        if (toastBox == null || toastLabel == null) return;

        toastBox.margins().set(Insets.of(8, 999, 0, 0));
        toastLabel.text(message);
        toastBox.margins().animate(200, Easing.SINE, Insets.of(8, 10, 0, 0));

        toastShownAt = System.currentTimeMillis();

        Minecraft.getInstance().execute(() -> {
            long delay = 5000;
            new Thread(() -> {
                try {
                    Thread.sleep(delay);
                    Minecraft.getInstance().execute(() -> {
                        toastBox.margins().animate(250, Easing.SINE, Insets.of(8, 999, 0, 0));
                        toastLabel.text(Component.empty());
                    });
                } catch (InterruptedException ignored) {}
            }).start();
        });
    }
}
