package com.mystichorizonsmc.playerexrebirth.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class DefaultPrestigeComponent implements PrestigeComponent, Component {

    private int prestige = 0;

    @Override
    public int getPrestigeLevel() {
        return prestige;
    }

    @Override
    public void setPrestigeLevel(int level) {
        this.prestige = level;
    }

    @Override
    public void incrementPrestige() {
        this.prestige++;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        this.prestige = tag.getInt("prestige");
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putInt("prestige", this.prestige);
    }

    public void sync(ServerPlayer player) {
        if (ModComponents.get().isProvidedBy(player)) {
            ModComponents.get().sync(player);
        }
    }
}
