/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.util;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.component.ComponentMapPredicate;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
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

    @Unique
    private static final TradedItem RESET_TRADED_ITEM = new TradedItem(
            RegistryEntry.of(Items.BARRIER), 1, ComponentMapPredicate.of(RESET_ITEM_STACK.getComponents()));


    public static boolean isResetOffer(TradeOffer offer) {
        return offer.getFirstBuyItem().itemStack().isOf(Items.BARRIER);
    }

    public static TradeOffer getResetOffer() {
        return new TradeOffer(
                RESET_TRADED_ITEM,
                Optional.of(RESET_TRADED_ITEM),
                RESET_TRADED_ITEM.itemStack(),
                1, 0, 0f
        );
    }
}
