package me.clickism.clickvillagers.mixin;

import me.clickism.clickvillagers.PickupHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(HopperBlock.class)
public abstract class HopperBlockMixin extends BlockWithEntity {
    protected HopperBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (world.isClient()) return;
        if (!(entity instanceof VillagerEntity) && !(entity instanceof ZombieVillagerEntity)) return;
        if (!(world.getBlockEntity(pos) instanceof HopperBlockEntity hopper)) return;
        Integer slot = null;
        for (int i = 0; i < hopper.size(); i++) {
            if (!hopper.getStack(i).isEmpty()) continue;
            slot = i;
            break;
        }
        if (slot == null) return;
        ItemStack itemStack = PickupHandler.toItemStack(entity);
        hopper.setStack(slot, itemStack);
        hopper.markDirty();
    }
}
