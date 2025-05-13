package com.mystichorizonsmc.playerexrebirth.component;

import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;

public class PrestigeComponentPlugin implements EntityComponentInitializer {

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(
                ModComponents.PRESTIGE,
                player -> new PrestigeComponentImpl(),
                RespawnCopyStrategy.ALWAYS_COPY
        );
    }
}
