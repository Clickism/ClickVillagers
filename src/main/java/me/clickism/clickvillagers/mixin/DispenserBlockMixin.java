package me.clickism.clickvillagers.mixin;

import me.clickism.clickvillagers.villager.PickupHandler;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
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

    @Inject(
            method = "getBehaviorForItem(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/block/dispenser/DispenserBehavior;", 
            at = @At("HEAD"),
            cancellable = true
    )
    protected void getBehaviorForItem(World world, ItemStack itemStack, CallbackInfoReturnable<DispenserBehavior> cir) {
        Entity entity = PickupHandler.readEntityFromItemStack(world, itemStack);
        if (entity != null) {
            cir.setReturnValue((pointer, stack) -> {
                Direction direction = pointer.state().get(DispenserBlock.FACING);
                BlockPos blockPos = pointer.pos().offset(direction);
                entity.refreshPositionAndAngles(blockPos, 0, 0);
                world.spawnEntity(entity);
                stack.decrement(1);
                return stack;
            });
        }
    }
}
