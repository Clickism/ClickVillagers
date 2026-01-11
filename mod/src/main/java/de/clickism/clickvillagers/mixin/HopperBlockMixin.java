/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.mixin;

import de.clickism.clickvillagers.villager.PickupHandler;
import de.clickism.clickvillagers.villager.VillagerHandler;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.npc.villager.VillagerDataHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

import static de.clickism.clickvillagers.ClickVillagersConfig.*;

@Mixin(HopperBlock.class)
public abstract class HopperBlockMixin extends BaseEntityBlock {
    protected HopperBlockMixin(Properties settings) {
        super(settings);
    }

    @Override
    public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity) {
        if (world.isClientSide()) return;
        if (!ENABLE_HOPPERS.get()) return;
        if (!(entity instanceof LivingEntity && entity instanceof VillagerDataHolder)) return;
        if (!(world.getBlockEntity(pos) instanceof HopperBlockEntity hopper)) return;
        if (!world.getBlockState(pos.above()).isAir()) {
            // If the hopper has a block above it, don't pick up the villager.
            return;
        }
        if (IGNORE_CLAIMED_VILLAGERS.get() &&
            new VillagerHandler<>((LivingEntity & VillagerDataHolder) entity).hasOwner()) {
            // Claimed villagers can't be picked up by hoppers.
            return;
        }
        if (IGNORE_BABY_VILLAGERS.get()
            && entity instanceof AgeableMob passiveEntity
            && passiveEntity.isBaby()) {
            // Baby villagers can't be picked up by hoppers.
            return;
        }
        if (!ALLOW_ZOMBIE_VILLAGERS.get() && entity instanceof ZombieVillager) {
            // Don't allow zombie villagers if setting is disabled
            return;
        }
        Integer slot = null;
        for (int i = 0; i < hopper.getContainerSize(); i++) {
            if (!hopper.getItem(i).isEmpty()) continue;
            slot = i;
            break;
        }
        if (slot == null) return;
        var villager = (LivingEntity & VillagerDataHolder) entity;
        ItemStack itemStack = PickupHandler.toItemStack(villager);
        hopper.setItem(slot, itemStack);
        hopper.setChanged();
    }
}
