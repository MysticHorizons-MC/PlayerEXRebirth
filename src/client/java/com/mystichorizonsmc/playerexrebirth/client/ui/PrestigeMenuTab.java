package com.mystichorizonsmc.playerexrebirth.client.ui;

import com.bibireden.playerex.ui.components.MenuComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class PrestigeMenuTab extends MenuComponent {

    public PrestigeMenuTab() {
        super(Sizing.fill(100), Sizing.fill(100), FlowLayout.Algorithm.VERTICAL);
    }

    @Override
    public void build(@NotNull FlowLayout root) {
        root.child(
                Components.button(Component.literal("§6Open Prestige Screen"), button -> Minecraft.getInstance().setScreen(new PrestigeScreen()))
                        .sizing(Sizing.fixed(140), Sizing.fixed(20))
                        .margins(Insets.of(10))
        );
    }
}
