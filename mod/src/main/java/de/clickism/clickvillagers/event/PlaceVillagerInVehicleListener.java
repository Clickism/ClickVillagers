/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.event;

import de.clickism.clickvillagers.util.MessageType;
import de.clickism.clickvillagers.util.VersionHelper;
import de.clickism.clickvillagers.villager.PickupHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.*;
import net.minecraft.world.entity.vehicle.boat.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

//? if >=1.21.11
import net.minecraft.world.entity.vehicle.boat.*;

public class PlaceVillagerInVehicleListener {
    public InteractionResult event(
            Player player,
            Level world,
            InteractionHand hand,
            Entity vehicle
    ) {
        if (world.isClientSide()) return InteractionResult.PASS;
        if (!hand.equals(InteractionHand.MAIN_HAND)) return InteractionResult.PASS;
        if (player.isSpectator()) return InteractionResult.PASS;
        if (!player.isShiftKeyDown()) return InteractionResult.PASS;
        //? if >=1.21.4 {
        if (!(vehicle instanceof VehicleEntity)) return InteractionResult.PASS;
        //?} else
        //if (!(vehicle instanceof Minecart) && !(vehicle instanceof Boat)) return InteractionResult.PASS;
        if (!hasSpace(vehicle)) return InteractionResult.PASS;
        ItemStack itemStack = player.getMainHandItem();
        Entity entity = PickupHandler.readEntityFromItemStack(world, itemStack);
        if (entity == null) {
            MessageType.FAIL.send(player, Component.literal("Couldn't read villager data."));
            return InteractionResult.CONSUME;
        }
        world.addFreshEntity(entity);
        BlockPos pos = vehicle.blockPosition();
        VersionHelper.moveEntity(entity, pos);
        itemStack.shrink(1);
        entity.startRiding(vehicle);
        //? if >=1.21.4 {
        if (vehicle instanceof AbstractBoat) {
            //?} else
            //if (vehicle instanceof Boat) {
            VersionHelper.playSound(player, SoundEvents.WOOD_BREAK, SoundSource.MASTER, 1, .5f);
        } else {
            VersionHelper.playSound(player, SoundEvents.METAL_BREAK, SoundSource.MASTER, 1, .5f);
        }
        return InteractionResult.CONSUME;
    }

    private boolean hasSpace(Entity entity) {
        if (entity instanceof Boat) {
            return entity.getPassengers().size() < 2;
        }
        return !entity.isVehicle();
    }
}
