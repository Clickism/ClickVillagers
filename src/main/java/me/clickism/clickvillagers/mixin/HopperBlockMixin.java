package me.clickism.clickvillagers.mixin;

import me.clickism.clickvillagers.PickupHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HopperBlock.class)
public abstract class HopperBlockMixin extends BlockWithEntity {
    protected HopperBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (world.isClient()) return;
        if (!(entity instanceof LivingEntity && entity instanceof VillagerDataContainer)) return;
        if (!(world.getBlockEntity(pos) instanceof HopperBlockEntity hopper)) return;
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
