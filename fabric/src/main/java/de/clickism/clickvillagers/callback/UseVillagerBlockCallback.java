/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.callback;

import de.clickism.clickvillagers.util.MessageType;
import de.clickism.clickvillagers.util.VersionHelper;
import de.clickism.clickvillagers.villager.PickupHandler;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class UseVillagerBlockCallback implements UseBlockCallback {
    @Override
    public InteractionResult interact(Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {
        if (!hand.equals(InteractionHand.MAIN_HAND)) return InteractionResult.PASS;
        if (hitResult == null) return InteractionResult.PASS;
        if (world.isClientSide()) return InteractionResult.PASS;
        if (player.isSpectator()) return InteractionResult.PASS;
        ItemStack itemStack = player.getMainHandItem();
        if (!itemStack.is(Items.PLAYER_HEAD)) return InteractionResult.PASS;
        if (!PickupHandler.isVillager(itemStack)) return InteractionResult.PASS;
        Entity entity = PickupHandler.readEntityFromItemStack(world, itemStack);
        if (entity == null) {
            MessageType.FAIL.send(player, Component.literal("Couldn't read villager data."));
            return InteractionResult.CONSUME;
        }
        BlockPos clickedPos = hitResult.getBlockPos();
        //? if >=1.20.5 {
        InteractionResult actionResult = world.getBlockState(clickedPos).useWithoutItem(world, player, hitResult);
        //?} else
        /*ActionResult actionResult = world.getBlockState(clickedPos).onUse(world, player, hand, hitResult);*/
        if (actionResult.consumesAction()) return actionResult;
        BlockPos pos = clickedPos.relative(hitResult.getDirection());
        entity.snapTo(pos, 0, 0);
        world.addFreshEntity(entity);
        itemStack.shrink(1);
        if (itemStack.getCount() <= 0) {
            int slot = VersionHelper.getSelectedSlot(player.getInventory());
            player.getInventory().setItem(slot, Items.AIR.getDefaultInstance());
        }
        BlockPos posBelow = pos.below();
        VersionHelper.playSound(player, SoundEvents.PLAYER_ATTACK_WEAK, SoundSource.NEUTRAL, 1, .5f);
        ((ServerLevel) world).sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, world.getBlockState(posBelow)),
                pos.getX() + .5, pos.getY(), pos.getZ() + .5,
                30, 0, 0, 0, 1
        );
        return InteractionResult.SUCCESS;
    }
}
