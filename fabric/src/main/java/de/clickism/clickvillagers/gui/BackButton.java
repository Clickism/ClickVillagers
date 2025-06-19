/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.gui;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.GuiInterface;
import de.clickism.clickvillagers.util.VersionHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class BackButton extends GuiElement {
    private static final ItemStack item = new GuiElementBuilder(Items.MAP)
            .setName(Text.literal("◀ ").formatted(Formatting.WHITE)
                    .append(Text.literal("BACK").formatted(Formatting.WHITE, Formatting.BOLD)))
            .addLoreLine(Text.literal("Go back to the previous menu.").formatted(Formatting.GRAY))
            .asStack();
    
    public BackButton(GuiInterface previous) {
        super(item, (index, type, action, gui) -> {
            previous.open();
            VersionHelper.playSound(previous.getPlayer(), SoundEvents.UI_LOOM_SELECT_PATTERN, SoundCategory.MASTER, 1, 1);
        });
    }
}
