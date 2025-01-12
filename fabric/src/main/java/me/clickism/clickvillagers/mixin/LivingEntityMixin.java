package me.clickism.clickvillagers.mixin;

import me.clickism.clickvillagers.config.Settings;
import me.clickism.clickvillagers.villager.VillagerHandler;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    public void damage(
            //? if >1.21.1
            ServerWorld world,
            DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!(this instanceof VillagerDataContainer container)) return;
        if (Settings.CLAIMED_IMMUNE_KILL_COMMAND.isDisabled() && source.isOf(DamageTypes.GENERIC_KILL)) {
            return;
        }
        if (Settings.CLAIMED_DAMAGE.isEnabled()) {
            return;
        }
        VillagerHandler<?> handler = new VillagerHandler<>((LivingEntity & VillagerDataContainer) container);
        if (!handler.hasOwner()) return;
        cir.setReturnValue(false);
        cir.cancel();
    }
}
