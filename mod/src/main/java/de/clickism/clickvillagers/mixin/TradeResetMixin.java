/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.mixin;

import de.clickism.clickvillagers.util.TradeResetHelper;
import de.clickism.clickvillagers.util.VersionHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.npc.villager.VillagerDataHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.clickism.clickvillagers.ClickVillagersConfig.ALLOW_RESETTING_TRADES;
import static de.clickism.clickvillagers.ClickVillagersConfig.CONFIG;

@Mixin(Villager.class)
public abstract class TradeResetMixin extends AbstractVillager implements ReputationEventHandler, VillagerDataHolder {

    public TradeResetMixin(EntityType<? extends AbstractVillager> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(
            method = "updateSpecialPrices",
            at = @At("TAIL")
    )
    private void injectPrepareOffersFor(Player player, CallbackInfo ci) {
        if (VersionHelper.getWorld(player).isClientSide()) return;
        this.getOffers().removeIf(TradeResetHelper::isResetOffer);
        if (!ALLOW_RESETTING_TRADES.get()) return;
        if (this.getVillagerXp() <= 0) {
            this.getOffers().add(TradeResetHelper.getResetOffer());
        }
    }

    @Inject(
            method = "setVillagerXp",
            at = @At("TAIL")
    )
    private void injectSetExperience(int experience, CallbackInfo ci) {
        if (VersionHelper.getWorld(this).isClientSide()) return;
        if (experience <= 0) return;
        this.getOffers().removeIf(TradeResetHelper::isResetOffer);
    }

}
