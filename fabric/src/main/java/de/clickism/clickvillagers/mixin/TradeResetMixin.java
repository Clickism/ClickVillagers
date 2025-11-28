/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.mixin;

import de.clickism.clickvillagers.util.TradeResetHelper;
import de.clickism.clickvillagers.util.VersionHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InteractionObserver;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.clickism.clickvillagers.ClickVillagersConfig.ALLOW_RESETTING_TRADES;
import static de.clickism.clickvillagers.ClickVillagersConfig.CONFIG;

@Mixin(VillagerEntity.class)
public abstract class TradeResetMixin extends MerchantEntity implements InteractionObserver, VillagerDataContainer {

    public TradeResetMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "prepareOffersFor",
            at = @At("TAIL")
    )
    private void injectPrepareOffersFor(PlayerEntity player, CallbackInfo ci) {
        if (VersionHelper.getWorld(player).isClient()) return;
        this.getOffers().removeIf(TradeResetHelper::isResetOffer);
        if (!CONFIG.get(ALLOW_RESETTING_TRADES)) return;
        if (this.getExperience() <= 0) {
            this.getOffers().add(TradeResetHelper.getResetOffer());
        }
    }

    @Inject(
            method = "setExperience",
            at = @At("TAIL")
    )
    private void injectSetExperience(int experience, CallbackInfo ci) {
        if (VersionHelper.getWorld(this).isClient()) return;
        if (experience <= 0) return;
        this.getOffers().removeIf(TradeResetHelper::isResetOffer);
    }

}
