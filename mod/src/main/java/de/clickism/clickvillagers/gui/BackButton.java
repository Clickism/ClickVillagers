/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.gui;

import de.clickism.fgui.api.elements.GuiElement;
import de.clickism.fgui.api.elements.GuiElementBuilder;
import de.clickism.fgui.api.gui.GuiInterface;
import de.clickism.clickvillagers.util.VersionHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class BackButton extends GuiElement {
    private static final ItemStack item = new GuiElementBuilder(Items.MAP)
            .setName(Component.literal("◀ ").withStyle(ChatFormatting.WHITE)
                    .append(Component.literal("BACK").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD)))
            .addLoreLine(Component.literal("Go back to the previous menu.").withStyle(ChatFormatting.GRAY))
            .asStack();
    
    public BackButton(GuiInterface previous) {
        super(item, (index, type, action, gui) -> {
            previous.open();
            VersionHelper.playSound(previous.getPlayer(), SoundEvents.UI_LOOM_SELECT_PATTERN, SoundSource.MASTER, 1, 1);
        });
    }
}
