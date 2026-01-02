/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.mixin;

import de.clickism.clickvillagers.villager.PickupHandler;
import de.clickism.clickvillagers.villager.VillagerHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import static de.clickism.clickvillagers.ClickVillagersConfig.*;

@Mixin(HopperBlock.class)
public abstract class HopperBlockMixin extends BlockWithEntity {
    protected HopperBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (world.isClient()) return;
        if (!ENABLE_HOPPERS.get()) return;
        if (!(entity instanceof LivingEntity && entity instanceof VillagerDataContainer)) return;
        if (!(world.getBlockEntity(pos) instanceof HopperBlockEntity hopper)) return;
        if (!world.getBlockState(pos.up()).isAir()) {
            // If the hopper has a block above it, don't pick up the villager.
            return;
        }
        if (IGNORE_CLAIMED_VILLAGERS.get() &&
            new VillagerHandler<>((LivingEntity & VillagerDataContainer) entity).hasOwner()) {
            // Claimed villagers can't be picked up by hoppers.
            return;
        }
        if (IGNORE_BABY_VILLAGERS.get()
            && entity instanceof PassiveEntity passiveEntity
            && passiveEntity.isBaby()) {
            // Baby villagers can't be picked up by hoppers.
            return;
        }
        if (!ALLOW_ZOMBIE_VILLAGERS.get() && entity instanceof ZombieVillagerEntity) {
            // Don't allow zombie villagers if setting is disabled
            return;
        }
        Integer slot = null;
        for (int i = 0; i < hopper.size(); i++) {
            if (!hopper.getStack(i).isEmpty()) continue;
            slot = i;
            break;
        }
        if (slot == null) return;
        var villager = (LivingEntity & VillagerDataContainer) entity;
        ItemStack itemStack = PickupHandler.toItemStack(villager);
        hopper.setStack(slot, itemStack);
        hopper.markDirty();
    }
}
