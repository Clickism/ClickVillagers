package me.clickism.clickvillagers.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import me.clickism.clickvillagers.util.MessageType;
import me.clickism.clickvillagers.util.VersionHelper;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public abstract class DecoratedGui extends SimpleGui {
    private static final GuiElementInterface BLACK = new GuiElementBuilder(Items.BLACK_STAINED_GLASS_PANE)
            .setName(Text.literal("x").formatted(Formatting.DARK_GRAY))
            //? if >=1.21.1 {
            .hideDefaultTooltip()
            //?} else
            /*.hideFlags()*/
            .setCallback((index, type, action, gui) -> {
                MessageType.FAIL.playSound(gui.getPlayer());
            })
            .build();
    private static final GuiElementInterface GRAY = new GuiElementBuilder(Items.GRAY_STAINED_GLASS_PANE)
            .setName(Text.literal("x").formatted(Formatting.DARK_GRAY))
            //? if >=1.21.1 {
            .hideDefaultTooltip()
            //?} else
            /*.hideFlags()*/
            .setCallback((index, type, action, gui) -> {
                MessageType.FAIL.playSound(gui.getPlayer());
            })
            .build();
    
    public DecoratedGui(ServerPlayerEntity player) {
        super(ScreenHandlerType.GENERIC_9X3, player, false);
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
