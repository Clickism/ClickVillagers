/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.gui;

import de.clickism.fgui.api.elements.GuiElementBuilder;
import de.clickism.fgui.api.elements.GuiElementInterface;
import de.clickism.fgui.api.gui.SimpleGui;
import de.clickism.clickvillagers.util.MessageType;
import de.clickism.clickvillagers.util.VersionHelper;
import net.minecraft.world.item.Items;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public abstract class DecoratedGui extends SimpleGui {
    private static final GuiElementInterface BLACK = new GuiElementBuilder(Items.BLACK_STAINED_GLASS_PANE)
            .setName(Component.literal("x").withStyle(ChatFormatting.DARK_GRAY))
            .hideDefaultTooltip()
            .setCallback((index, type, action, gui) -> {
                MessageType.FAIL.playSound(gui.getPlayer());
            })
            .build();
    private static final GuiElementInterface GRAY = new GuiElementBuilder(Items.GRAY_STAINED_GLASS_PANE)
            .setName(Component.literal("x").withStyle(ChatFormatting.DARK_GRAY))
            .hideDefaultTooltip()
            .setCallback((index, type, action, gui) -> {
                MessageType.FAIL.playSound(gui.getPlayer());
            })
            .build();
    
    public DecoratedGui(ServerPlayer player) {
        super(MenuType.GENERIC_9x3, player, false);
        addBackground();
    }
    
    protected void addBackground() {
        for (int i = 0; i < size; i++) {
            if ((i / 9) % 2 == 0) {
                setSlot(i, GRAY);
            } else {
                setSlot(i, BLACK);
            }
        }
    }
}
