package com.mystichorizonsmc.playerexrebirth.component;

import com.mystichorizonsmc.playerexrebirth.PlayerExRebirth;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;

import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.resources.ResourceLocation;



public class ModComponents implements EntityComponentInitializer {

    public static ComponentKey<PrestigeComponent> PRESTIGE;

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        PRESTIGE = ComponentRegistry.getOrCreate(
                new ResourceLocation(PlayerExRebirth.MOD_ID, "prestige"),
                PrestigeComponent.class
        );

        registry.registerForPlayers(
                PRESTIGE,
                player -> new DefaultPrestigeComponent(),
                RespawnCopyStrategy.ALWAYS_COPY
        );

        PlayerExRebirth.LOGGER.info("[Components] Registered PRESTIGE component key.");
    }
}