package com.mystichorizonsmc.playerexrebirth.component;

import net.minecraft.nbt.CompoundTag;

public class PrestigeComponentImpl implements PrestigeComponent {

    private int prestigeLevel = 0;

    @Override
    public int getPrestigeLevel() {
        return prestigeLevel;
    }

    @Override
    public void setPrestigeLevel(int level) {
        this.prestigeLevel = level;
    }

    @Override
    public void incrementPrestige() {
        this.prestigeLevel++;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        this.prestigeLevel = tag.getInt("PrestigeLevel");
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putInt("PrestigeLevel", this.prestigeLevel);
    }
}
