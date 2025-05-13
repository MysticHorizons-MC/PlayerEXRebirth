package com.mystichorizonsmc.playerexrebirth.component;

import com.mystichorizonsmc.playerexrebirth.PlayerExRebirth;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;

import net.minecraft.resources.ResourceLocation;

public class ModComponents {
    public static final ComponentKey<PrestigeComponent> PRESTIGE =
            ComponentRegistry.getOrCreate(
                    new ResourceLocation(PlayerExRebirth.MOD_ID, "prestige"),
                    PrestigeComponent.class
            );
}