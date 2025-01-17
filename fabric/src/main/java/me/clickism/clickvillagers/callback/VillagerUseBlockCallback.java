/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.callback;

import me.clickism.clickvillagers.util.VersionHelper;
import me.clickism.clickvillagers.villager.PickupHandler;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VillagerUseBlockCallback implements UseBlockCallback {
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (!hand.equals(Hand.MAIN_HAND)) return ActionResult.PASS;
        if (hitResult == null) return ActionResult.PASS;
        if (world.isClient()) return ActionResult.PASS;
        if (player.isSpectator()) return ActionResult.PASS;
        ItemStack itemStack = player.getMainHandStack();
        Entity entity = PickupHandler.readEntityFromItemStack(world, itemStack);
        if (entity == null) return ActionResult.PASS;
        BlockPos clickedPos = hitResult.getBlockPos();
        //? if >=1.21.1 {
        ActionResult actionResult = world.getBlockState(clickedPos).onUse(world, player, hitResult);
        //?} else
        /*ActionResult actionResult = world.getBlockState(clickedPos).onUse(world, player, hand, hitResult);*/
        if (actionResult.isAccepted()) return actionResult;
        BlockPos pos = clickedPos.offset(hitResult.getSide());
        entity.refreshPositionAndAngles(pos, 0, 0);
        world.spawnEntity(entity);
        itemStack.decrement(1);
        if (itemStack.getCount() <= 0) {
            player.getInventory().setStack(player.getInventory().selectedSlot, Items.AIR.getDefaultStack());
        }
        BlockPos posBelow = pos.down();
        VersionHelper.playSound(player, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, SoundCategory.NEUTRAL, 1, .5f);
        ((ServerWorld) world).spawnParticles(
                new BlockStateParticleEffect(ParticleTypes.BLOCK, world.getBlockState(posBelow)),
                pos.getX() + .5, pos.getY(), pos.getZ() + .5,
                30, 0, 0, 0, 1
        );
        return ActionResult.SUCCESS;
    }
}
