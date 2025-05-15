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

    private long toastShownAt = -1;

    public PrestigeScreen() {
        super(FlowLayout.class, DataSource.asset(ResourceLocation.tryParse(PlayerExRebirth.MOD_ID + ":prestige_screen")));
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        int prestigeLevel = ClientPrestigeData.getCurrentPrestigeLevel();
        int requiredLevel = ClientPrestigeData.getMaxPrestigeLevel();

        LabelComponent prestigeLabel = rootComponent.childById(LabelComponent.class, "prestige_label");
        if (prestigeLabel != null)
            prestigeLabel.text(Component.translatable("playerexrebirth.ui.prestige_level", prestigeLevel));

        FlowLayout bonusList = rootComponent.childById(FlowLayout.class, "bonuses_list");
        if (bonusList != null) {
            bonusList.clearChildren();
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
        }

        ProgressBarComponent progressBar = rootComponent.childById(ProgressBarComponent.class, "prestige_progress");
        if (progressBar != null) {
            float percent = (float) player.experienceLevel / requiredLevel;
            progressBar.setPercentage(Math.min(percent, 1.0f));
            progressBar.tooltip(Component.literal("%.0f%% to Prestige".formatted(percent * 100)));
        }

        boolean canPrestige = player.experienceLevel >= requiredLevel;

        LabelComponent warning = rootComponent.childById(LabelComponent.class, "requirement_warning");
        if (warning != null) {
            warning.text(canPrestige
                    ? Component.empty()
                    : Component.translatable("playerexrebirth.ui.requirement_warning", requiredLevel));
        }

        BoxComponent toastBox = rootComponent.childById(BoxComponent.class, "prestige_toast_box");
        LabelComponent toastLabel = rootComponent.childById(LabelComponent.class, "prestige_toast");

        if (canPrestige && ClientPrestigeData.shouldShowToast()) {
            animateToastSlide(toastBox, toastLabel, Component.translatable("playerexrebirth.ui.toast_ready"));
            ClientPrestigeData.setToastShown();
        } else if (toastLabel != null) {
            toastLabel.text(Component.empty());
        }

        ButtonComponent prestigeButton = rootComponent.childById(ButtonComponent.class, "prestige_button");
        if (prestigeButton != null) {
            prestigeButton.tooltip(Component.translatable("playerexrebirth.ui.tooltip.prestige_button"));
            prestigeButton.onPress(button -> Minecraft.getInstance().setScreen(new ConfirmScreen(
                    confirmed -> {
                        if (confirmed) {
                            ClientPrestigeRequest.send();
                        } else {
                            Minecraft.getInstance().setScreen(new PrestigeScreen());
                        }
                    },
                    Component.translatable("playerexrebirth.ui.confirm_title"),
                    Component.translatable("playerexrebirth.ui.confirm_warning")
            )));
        }

        BoxComponent box = rootComponent.childById(BoxComponent.class, "prestige_button_box");
        if (box != null) {
            if (canPrestige) {
                box.fill(true);
                box.direction(BoxComponent.GradientDirection.TOP_TO_BOTTOM);

                float time = (System.nanoTime() % 1_000_000_000L) / 1_000_000_000f;
                float alpha = 0.5f + 0.3f * (float) Math.sin(time * 2 * Math.PI);

                int argb = ((int) (alpha * 255) << 24) | 0xFFD700;
                box.startColor().set(Color.ofArgb(argb));
                box.endColor().set(Color.ofArgb(argb));
            } else {
                box.fill(false);
            }
        }
    }

    private void animateToastSlide(BoxComponent toastBox, LabelComponent toastLabel, Component message) {
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
