package com.mystichorizonsmc.playerexrebirth.component;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;

public interface PrestigeComponent extends ComponentV3 {
    int getPrestigeLevel();
    void setPrestigeLevel(int level);
    void incrementPrestige();
}
