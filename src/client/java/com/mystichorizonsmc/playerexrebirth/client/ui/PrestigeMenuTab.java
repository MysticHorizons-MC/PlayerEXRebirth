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
        // Header Label
        root.child(
                Components.label(Component.translatable("playerexrebirth.ui.menu.prestige"))
                        .sizing(Sizing.content(), Sizing.fixed(20))
                        .margins(Insets.of(10, 5, 5, 5))
        );

        // Button to open PrestigeScreen
        root.child(
                Components.button(Component.translatable("playerexrebirth.ui.open_prestige_screen"),
                                button -> Minecraft.getInstance().setScreen(new PrestigeScreen()))
                        .sizing(Sizing.fixed(160), Sizing.fixed(20))
                        .margins(Insets.of(5))
        );
    }
}
