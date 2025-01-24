/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.mixin;

import me.clickism.clickvillagers.villager.PickupHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin extends BlockWithEntity {
    protected DispenserBlockMixin(Settings settings) {
        super(settings);
    }

    //? if >=1.20.5 {
    @Inject(
            method = "getBehaviorForItem(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/block/dispenser/DispenserBehavior;", 
            at = @At("HEAD"),
            cancellable = true
    )
    //?} else {
    /*@Inject(
            method = "Lnet/minecraft/block/DispenserBlock;getBehaviorForItem(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/block/dispenser/DispenserBehavior;",
            at = @At("HEAD"),
            cancellable = true
    )
    *///?}
    protected void getBehaviorForItem(
            //? if >=1.20.5
            World world,
            ItemStack itemStack, 
            CallbackInfoReturnable<DispenserBehavior> cir
    ) {
        if (!PickupHandler.isVillager(itemStack)) return;
        cir.setReturnValue((pointer, stack) -> {
            //? if <1.20.5
            /*World world = world(pointer);*/
            Direction direction = state(pointer).get(DispenserBlock.FACING);
            BlockPos blockPos = pos(pointer).offset(direction);
            Entity entity = PickupHandler.readEntityFromItemStack(world, stack);
            if (entity == null) return stack;
            entity.refreshPositionAndAngles(blockPos, 0, 0);
            world.spawnEntity(entity);
            stack.decrement(1);
            return stack;
        });
    }
    
    private static ServerWorld world(BlockPointer pointer) {
        //? if >=1.20.5 {
        return pointer.world();
        //?} else
        /*return pointer.getWorld();*/
    }
    
    private static BlockPos pos(BlockPointer pointer) {
        //? if >=1.20.5 {
        return pointer.pos();
        //?} else
        /*return pointer.getPos();*/
    }
    
    private static BlockState state(BlockPointer pointer) {
        //? if >=1.20.5 {
        return pointer.state();
        //?} else
        /*return pointer.getBlockState();*/
    }
}