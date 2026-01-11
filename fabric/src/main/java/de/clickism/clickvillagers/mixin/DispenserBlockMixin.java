/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.mixin;

import de.clickism.clickvillagers.util.VersionHelper;
import de.clickism.clickvillagers.villager.PickupHandler;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin extends BaseEntityBlock {
    protected DispenserBlockMixin(Properties settings) {
        super(settings);
    }

    //? if >=1.20.5 {
    @Inject(
            method = "getDispenseMethod(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/core/dispenser/DispenseItemBehavior;",
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
            Level world,
            ItemStack itemStack,
            CallbackInfoReturnable<DispenseItemBehavior> cir
    ) {
        if (!PickupHandler.isVillager(itemStack)) return;
        cir.setReturnValue((pointer, stack) -> {
            //? if <1.20.5
            /*World world = world(pointer);*/
            Direction direction = state(pointer).getValue(DispenserBlock.FACING);
            BlockPos blockPos = pos(pointer).relative(direction);
            Entity entity = PickupHandler.readEntityFromItemStack(world, stack);
            if (entity == null) return stack;
            VersionHelper.moveEntity(entity, blockPos);
            world.addFreshEntity(entity);
            stack.shrink(1);
            return stack;
        });
    }
    
    private static ServerLevel world(BlockSource pointer) {
        //? if >=1.20.5 {
        return pointer.level();
        //?} else
        /*return pointer.getWorld();*/
    }
    
    private static BlockPos pos(BlockSource pointer) {
        //? if >=1.20.5 {
        return pointer.pos();
        //?} else
        /*return pointer.getPos();*/
    }
    
    private static BlockState state(BlockSource pointer) {
        //? if >=1.20.5 {
        return pointer.state();
        //?} else
        /*return pointer.getBlockState();*/
    }
}