package me.clickism.clickvillagers.gui;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.GuiInterface;
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
            //? if >=1.21.1 {
            previous.getPlayer().playSoundToPlayer(SoundEvents.UI_LOOM_SELECT_PATTERN, SoundCategory.MASTER, 1, 1);
            //?} else
            /*previous.getPlayer().playSound(SoundEvents.UI_LOOM_SELECT_PATTERN, SoundCategory.MASTER, 1, 1);*/
        });
    }
}
