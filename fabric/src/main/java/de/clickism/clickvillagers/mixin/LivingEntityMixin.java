/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.mixin;

import de.clickism.clickvillagers.villager.VillagerHandler;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.VillagerDataHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.clickism.clickvillagers.ClickVillagersConfig.*;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable {

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    public void damage(
            //? if >1.21.1
            ServerLevel world,
            DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!(this instanceof VillagerDataHolder container)) return;
        if (CLAIMED_IMMUNE_KILL_COMMAND.get() && source.is(DamageTypes.GENERIC_KILL)) {
            return;
        }
        if (CLAIMED_DAMAGE.get()) {
            return;
        }
        VillagerHandler<?> handler = new VillagerHandler<>((LivingEntity & VillagerDataHolder) container);
        if (!handler.hasOwner()) return;
        cir.setReturnValue(false);
        cir.cancel();
    }
}
