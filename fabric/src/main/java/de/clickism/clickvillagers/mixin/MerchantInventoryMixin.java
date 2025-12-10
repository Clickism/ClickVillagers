/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.mixin;

import de.clickism.clickvillagers.util.MessageType;
import de.clickism.clickvillagers.util.TradeResetHelper;
import de.clickism.clickvillagers.util.VersionHelper;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.village.Merchant;
import net.minecraft.village.MerchantInventory;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.clickism.clickvillagers.ClickVillagersConfig.ALLOW_RESETTING_TRADES;
import static de.clickism.clickvillagers.ClickVillagersConfig.CONFIG;

@Mixin(MerchantInventory.class)
public abstract class MerchantInventoryMixin implements Inventory {
    @Shadow
    @Final
    private Merchant merchant;

    @Inject(method = "setOfferIndex", at = @At("HEAD"), cancellable = true)
    private void onSetOfferIndex(int index, CallbackInfo ci) {
        if (this.merchant.isClient()) return;
        if (!CONFIG.get(ALLOW_RESETTING_TRADES)) return;
        TradeOfferList offers = this.merchant.getOffers();
        if (offers.isEmpty() || offers.size() <= index) return;
        TradeOffer offer = offers.get(index);
        if (TradeResetHelper.isResetOffer(offer)) {
            if (this.merchant instanceof VillagerEntity villager) {
                villager.setOffers(new TradeOfferList());
                villager.fillRecipes(
                        //? if >=1.21.11
                        (ServerWorld) villager.getEntityWorld()
                );
                var customer = (ServerPlayerEntity) villager.getCustomer();
                if (customer != null) {
                    customer.closeHandledScreen();
                    VersionHelper.playSound(customer, SoundEvents.BLOCK_SMITHING_TABLE_USE, SoundCategory.NEUTRAL, 1, .5f);
                    MessageType.WARN.sendActionbarSilently(customer, Text.literal("You reset this villager's trades."));
                }
                ci.cancel();
            }
        }
    }
}
