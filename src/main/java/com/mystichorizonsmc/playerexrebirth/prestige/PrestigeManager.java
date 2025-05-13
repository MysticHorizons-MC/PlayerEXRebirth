package com.mystichorizonsmc.playerexrebirth.prestige;

import com.bibireden.playerex.api.attribute.PlayerEXAttributes;
import com.bibireden.playerex.util.PlayerEXUtil;
import com.mystichorizonsmc.playerexrebirth.PlayerExRebirth;
import com.mystichorizonsmc.playerexrebirth.component.ModComponents;
import com.mystichorizonsmc.playerexrebirth.component.PrestigeComponent;
import com.mystichorizonsmc.playerexrebirth.config.PrestigeConfig;
import com.mystichorizonsmc.playerexrebirth.network.packet.PrestigeToastS2CPacket;
import com.mystichorizonsmc.playerexrebirth.utils.PrestigeUtils;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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
            "constitution", UUID.fromString("6e6f7a71-bc7e-4c9d-b6d3-fb930a71fddb"),
            "health_regeneration", UUID.fromString("add0b889-7936-456e-a694-d6c0fcb37c02"),
            "lifesteal", UUID.fromString("96f4022e-2022-4f07-8c16-59b5f8f2a9b9"),
            "dexterity", UUID.fromString("6a5f87f4-d7ab-4263-9245-9aa3483b9d99"),
            "melee_critical_chance", UUID.fromString("d2e28f16-26e6-4df1-8d41-4b10a32bba44")
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

        PrestigeComponent prestige = ModComponents.PRESTIGE.get(player);
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

        // Apply prestige bonuses
        applyPrestigeBonuses(player);

        // Execute prestige rewards
        PrestigeUtils.runPrestigeCommands(player, newPrestige);
        PrestigeConfig.prestigeItems.forEach(item -> player.getInventory().add(item.copy()));

        // Visual + audio feedback
        triggerPrestigeFeedback(player, newPrestige);

        // Final message
        player.sendSystemMessage(Component.literal("§6You have prestiged! Your prestige level is now §e" + newPrestige));

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
        PrestigeComponent prestige = ModComponents.PRESTIGE.get(player);
        int prestigeLevel = prestige.getPrestigeLevel();

        for (Map.Entry<String, Double> entry : PrestigeConfig.rewardMultipliers.entrySet()) {
            String key = entry.getKey();
            double multiplier = entry.getValue();

            Attribute attr = switch (key) {
                case "constitution" -> PlayerEXAttributes.CONSTITUTION;
                case "health_regeneration" -> PlayerEXAttributes.HEALTH_REGENERATION;
                case "lifesteal" -> PlayerEXAttributes.LIFESTEAL;
                case "dexterity" -> PlayerEXAttributes.DEXTERITY;
                case "melee_critical_chance" -> PlayerEXAttributes.MELEE_CRITICAL_CHANCE;
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

    private static void triggerPrestigeFeedback(ServerPlayer player, int prestigeLevel) {
        // Sound effect
        player.level().playSound(null, player.blockPosition(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.2f, 1.0f);

        // Particles
        (player).serverLevel().sendParticles(
                net.minecraft.core.particles.ParticleTypes.ENCHANT,
                player.getX(), player.getY() + 1, player.getZ(),
                120, 0.5, 1.0, 0.5, 0.05
        );

        // Toast message via networking
        PlayerExRebirth.NETWORK.serverHandle(player).send(new PrestigeToastS2CPacket(prestigeLevel));
    }
}
