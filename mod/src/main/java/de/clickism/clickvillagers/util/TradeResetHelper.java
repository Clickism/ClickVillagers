/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.util;

import de.clickism.fgui.api.elements.GuiElementBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
//? if >1.21.4 {
import net.minecraft.core.component.DataComponentExactPredicate;
//?} elif >1.20.1 {
/*import net.minecraft.core.component.DataComponentPredicate;
*///?}
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Optional;

public class TradeResetHelper {

    @Unique
    private static final ItemStack RESET_ITEM_STACK = GuiElementBuilder.from(new ItemStack(Items.BARRIER))
            .setName(Component.literal("§4♻ §lRESET TRADES"))
            .setLore(List.of(
                    Component.literal("§cClick on this recipe to reset trades."),
                    Component.literal("§c§lThis cannot be undone")
            ))
            .glow()
            .asStack();

    //? if >1.20.1 {
    @Unique
    private static final ItemCost RESET_TRADED_ITEM = new ItemCost(
            BuiltInRegistries.ITEM.wrapAsHolder(Items.BARRIER),
            1,
            //? if >1.21.4 {
            DataComponentExactPredicate.allOf(RESET_ITEM_STACK.getComponents())
             //?} else
            //DataComponentPredicate.allOf(RESET_ITEM_STACK.getComponents())
    );
    //?}

    public static boolean isResetOffer(MerchantOffer offer) {
        //? if >1.20.1 {
        return offer.getItemCostA().itemStack().is(Items.BARRIER);
        //?} else
        /*return offer.getOriginalFirstBuyItem().isOf(Items.BARRIER);*/
    }

    public static MerchantOffer getResetOffer() {
        return new MerchantOffer(
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