package com.mystichorizonsmc.playerexrebirth.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.CompoundTag;

public class DefaultPrestigeComponent implements PrestigeComponent, AutoSyncedComponent {

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
    public void readFromNbt(CompoundTag compoundTag) {
        this.prestige = compoundTag.getInt("prestige");
    }

    @Override
    public void writeToNbt(CompoundTag compoundTag) {
        compoundTag.putInt("prestige", this.prestige);
    }
}
