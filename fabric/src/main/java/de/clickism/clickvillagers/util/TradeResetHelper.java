/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.util;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
//? if >1.21.4 {
import net.minecraft.predicate.component.ComponentMapPredicate;
import net.minecraft.village.TradedItem;
//?} elif >1.20.1 {
/*import net.minecraft.predicate.ComponentPredicate;
import net.minecraft.village.TradedItem;
*///?}
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Optional;

public class TradeResetHelper {

    @Unique
    private static final ItemStack RESET_ITEM_STACK = GuiElementBuilder.from(new ItemStack(Items.BARRIER))
            .setName(Text.literal("§4♻ §lRESET TRADES"))
            .setLore(List.of(
                    Text.literal("§cClick on this recipe to reset trades."),
                    Text.literal("§c§lThis cannot be undone")
            ))
            .glow()
            .asStack();

    //? if >1.20.1 {
    @Unique
    private static final TradedItem RESET_TRADED_ITEM = new TradedItem(
            RegistryEntry.of(Items.BARRIER), 1,
            //? if >1.21.4 {
            ComponentMapPredicate.of(RESET_ITEM_STACK.getComponents())
            //?} else
            /*ComponentPredicate.of(RESET_ITEM_STACK.getComponents())*/
    );
    //?}

    public static boolean isResetOffer(TradeOffer offer) {
        //? if >1.20.1 {
        return offer.getFirstBuyItem().itemStack().isOf(Items.BARRIER);
        //?} else
        /*return offer.getOriginalFirstBuyItem().isOf(Items.BARRIER);*/
    }

    public static TradeOffer getResetOffer() {
        return new TradeOffer(
                //? if >1.20.1 {
                RESET_TRADED_ITEM,
                Optional.of(RESET_TRADED_ITEM),
                RESET_TRADED_ITEM.itemStack(),
                //?} else {
                /*RESET_ITEM_STACK,
                RESET_ITEM_STACK,
                RESET_ITEM_STACK,
                *///?}
                1, 0, 0f
        );
    }
}