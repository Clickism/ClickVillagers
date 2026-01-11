/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.callback;

import de.clickism.clickvillagers.util.MessageType;
import de.clickism.clickvillagers.util.VersionHelper;
import de.clickism.clickvillagers.villager.PickupHandler;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.*;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

//? if >=1.21.11
import net.minecraft.world.entity.vehicle.boat.*;

public class UseVehicleEntityCallback implements UseEntityCallback {
    @Override
    public InteractionResult interact(Player player, Level world, InteractionHand hand, Entity vehicle, @Nullable EntityHitResult entityHitResult) {
        if (world.isClientSide()) return InteractionResult.PASS;
        if (!hand.equals(InteractionHand.MAIN_HAND)) return InteractionResult.PASS;
        if (player.isSpectator()) return InteractionResult.PASS;
        if (!player.isShiftKeyDown()) return InteractionResult.PASS;
        //? if >=1.21.4 {
        if (!(vehicle instanceof VehicleEntity)) return InteractionResult.PASS;
        //?} else
        /*if (!(vehicle instanceof Minecart) && !(vehicle instanceof Boat)) return ActionResult.PASS;*/
        if (!hasSpace(vehicle)) return InteractionResult.PASS;
        ItemStack itemStack = player.getMainHandItem();
        Entity entity = PickupHandler.readEntityFromItemStack(world, itemStack);
        if (entity == null) {
            MessageType.FAIL.send(player, Component.literal("Couldn't read villager data."));
            return InteractionResult.CONSUME;
        }
        world.addFreshEntity(entity);
        BlockPos pos = vehicle.blockPosition();
        entity.snapTo(pos, 0, 0);
        itemStack.shrink(1);
        entity.startRiding(vehicle);
        //? if >=1.21.4 {
        if (vehicle instanceof AbstractBoat) {
        //?} else
        /*if (vehicle instanceof Boat) {*/
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
