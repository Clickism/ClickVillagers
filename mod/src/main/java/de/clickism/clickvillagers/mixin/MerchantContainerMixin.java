/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.mixin;

import de.clickism.clickvillagers.util.MessageType;
import de.clickism.clickvillagers.util.TradeResetHelper;
import de.clickism.clickvillagers.util.VersionHelper;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.Container;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.clickism.clickvillagers.ClickVillagersConfig.ALLOW_RESETTING_TRADES;
import static de.clickism.clickvillagers.ClickVillagersConfig.CONFIG;

@Mixin(MerchantContainer.class)
public abstract class MerchantContainerMixin implements Container {
    @Shadow
    @Final
    private Merchant merchant;

    @Inject(method = "setSelectionHint", at = @At("HEAD"), cancellable = true)
    private void onSetOfferIndex(int index, CallbackInfo ci) {
        if (this.merchant.isClientSide()) return;
        if (!ALLOW_RESETTING_TRADES.get()) return;
        MerchantOffers offers = this.merchant.getOffers();
        if (offers.isEmpty() || offers.size() <= index) return;
        MerchantOffer offer = offers.get(index);
        if (TradeResetHelper.isResetOffer(offer)) {
            if (this.merchant instanceof Villager villager) {
                villager.setOffers(new MerchantOffers());
                villager.updateTrades(
                        //? if >=1.21.11
                        (ServerLevel) villager.level()
                );
                var customer = (ServerPlayer) villager.getTradingPlayer();
                if (customer != null) {
                    customer.closeContainer();
                    VersionHelper.playSound(customer, SoundEvents.SMITHING_TABLE_USE, SoundSource.NEUTRAL, 1, .5f);
                    MessageType.WARN.sendActionbarSilently(customer, Component.literal("You reset this villager's trades."));
                }
                ci.cancel();
            }
        }
    }
}
