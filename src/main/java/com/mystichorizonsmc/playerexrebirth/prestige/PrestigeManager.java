package com.mystichorizonsmc.playerexrebirth.prestige;

import com.bibireden.playerex.api.attribute.PlayerEXAttributes;
import com.bibireden.playerex.util.PlayerEXUtil;
import com.mystichorizonsmc.playerexrebirth.PlayerExRebirth;
import com.mystichorizonsmc.playerexrebirth.component.ModComponents;
import com.mystichorizonsmc.playerexrebirth.component.PrestigeComponent;
import com.mystichorizonsmc.playerexrebirth.config.PrestigeConfig;
import com.mystichorizonsmc.playerexrebirth.utils.PrestigeUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import java.util.Map;
import java.util.UUID;

public class PrestigeManager {

    private static int cachedMaxLevel = -1;

    // UUIDs for tracked modifiers
    public static final Map<String, UUID> MODIFIER_UUIDS = Map.of(
            "constitution", UUID.fromString("f22df0d3-ea56-4f49-b202-6909a6b6cecf"),
            "strength", UUID.fromString("06a7393f-e7c2-4d70-a820-4bb81ecde3b0"),
            "dexterity", UUID.fromString("c17d992e-8ac1-4ff9-a4cc-d43e6094f93d"),
            "intelligence", UUID.fromString("9f0f43e4-d6e1-4f17-9679-759d9a01d5db"),
            "luckiness", UUID.fromString("70c0f7cd-e70c-4f9b-8f67-0e24e7b6dbe7"),
            "focus", UUID.fromString("90d3988e-58c7-4c32-9c8e-4732a1baff6f")
    );

    public static void tryPrestige(ServerPlayer player) {
        AttributeInstance levelAttr = player.getAttribute(PlayerEXAttributes.LEVEL);
        if (levelAttr == null) return;

        int currentLevel = (int) levelAttr.getValue();
        int maxLevel = getEffectiveMaxLevel(player);

        if (currentLevel < maxLevel) {
            player.sendSystemMessage(Component.literal("§cYou must reach level " + maxLevel + " to prestige."));
            return;
        }

        PrestigeComponent prestige = ModComponents.get().get(player);
        int prestigeLevel = prestige.getPrestigeLevel();

        if (prestigeLevel >= PrestigeConfig.maxPrestige) {
            player.sendSystemMessage(Component.literal("§cYou’ve already reached the max prestige level."));
            return;
        }

        // Reset PlayerEX attributes and level
        resetPlayerExAttributes(player);
        levelAttr.setBaseValue(0.0);

        // Increase prestige
        prestige.incrementPrestige();
        int newPrestige = prestige.getPrestigeLevel();

        // Sync component
        ModComponents.get().sync(player);

        // Apply prestige bonuses
        applyPrestigeBonuses(player);

        // Execute prestige rewards
        PrestigeUtils.runPrestigeCommands(player, newPrestige);
        PrestigeConfig.prestigeItems.forEach(item -> player.getInventory().add(item.copy()));

        // Visual + audio feedback
        triggerPrestigeFeedback(player);

        // Final message
        player.sendSystemMessage(Component.literal("§6You have prestiged! Your prestige level is now §e" + newPrestige));
        resetPlayerEXLevel(player);

    }

    public static void resetPlayerExAttributes(ServerPlayer player) {
        for (var id : PlayerEXAttributes.PRIMARY_ATTRIBUTE_IDS) {
            Attribute attr = BuiltInRegistries.ATTRIBUTE.get(id);
            AttributeInstance instance = player.getAttribute(attr);
            if (instance != null) instance.setBaseValue(0.0);
        }
    }

    public static int getEffectiveMaxLevel(ServerPlayer player) {
        if (cachedMaxLevel > 0) return cachedMaxLevel;

        int max = 1;
        final int hardCap = 1000;

        try {
            for (int i = 2; i <= hardCap; i++) {
                int xp = PlayerEXUtil.getRequiredXpForLevel(player, i);
                if (xp <= 0 || xp >= Integer.MAX_VALUE) break;
                max = i;
            }
        } catch (Exception e) {
            System.err.println("[PlayerEXRebirth] Failed to infer max level: " + e);
            max = 100;
        }

        cachedMaxLevel = max;
        return max;
    }

    public static void applyPrestigeBonuses(ServerPlayer player) {
        if (ModComponents.get() == null) {
            PlayerExRebirth.LOGGER.error("[PrestigeManager] ModComponents.PRESTIGE is null! Component registration may have failed.");
            player.sendSystemMessage(Component.literal("§c[Error] Prestige component not loaded. Please report this to server staff."));
            return;
        }

        PrestigeComponent prestige = ModComponents.get().get(player);
        if (prestige == null) {
            PlayerExRebirth.LOGGER.error("[PrestigeManager] Failed to get PrestigeComponent from player {}", player.getGameProfile().getName());
            player.sendSystemMessage(Component.literal("§c[Error] Failed to load your prestige data. Try re-logging or contact staff."));
            return;
        }
        int prestigeLevel = prestige.getPrestigeLevel();

        for (Map.Entry<String, Double> entry : PrestigeConfig.rewardMultipliers.entrySet()) {
            String key = entry.getKey();
            double multiplier = entry.getValue();

            Attribute attr = switch (key) {
                case "constitution" -> PlayerEXAttributes.CONSTITUTION;
                case "strength" -> PlayerEXAttributes.STRENGTH;
                case "dexterity" -> PlayerEXAttributes.DEXTERITY;
                case "intelligence" -> PlayerEXAttributes.INTELLIGENCE;
                case "luckiness" -> PlayerEXAttributes.LUCKINESS;
                case "focus" -> PlayerEXAttributes.FOCUS;
                default -> null;
            };

            if (attr == null) continue;

            double value = multiplier * prestigeLevel;
            UUID uuid = MODIFIER_UUIDS.getOrDefault(key, UUID.randomUUID());
            applyBonus(player, attr, uuid, value);
        }
    }


    private static void applyBonus(ServerPlayer player, Attribute attr, UUID uuid, double value) {
        AttributeInstance instance = player.getAttribute(attr);
        if (instance == null) return;

        instance.removeModifier(uuid);
        if (value > 0) {
            instance.addPermanentModifier(new AttributeModifier(uuid, "Prestige Bonus", value, AttributeModifier.Operation.ADDITION));
        }
    }

    private static void triggerPrestigeFeedback(ServerPlayer player) {
        // Sound effect
        player.level().playSound(null, player.blockPosition(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.2f, 1.0f);

        // Particles
        (player).serverLevel().sendParticles(
                net.minecraft.core.particles.ParticleTypes.ENCHANT,
                player.getX(), player.getY() + 1, player.getZ(),
                120, 0.5, 1.0, 0.5, 0.05
        );
    }

    public static int getRequiredLevelToPrestige() {
        return PrestigeConfig.requiredLevel; // NEW
    }

    public static void resetPlayerEXLevel(ServerPlayer player) {
        MinecraftServer server = player.server;
        String command = "playerex reset " + player.getName().getString();

        server.getCommands().performPrefixedCommand(
                player.createCommandSourceStack().withPermission(4),
                command
        );
    }
}
