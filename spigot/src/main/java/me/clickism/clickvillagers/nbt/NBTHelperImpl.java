package me.clickism.clickvillagers.nbt;

import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.entity.EntityLiving;
import org.bukkit.entity.LivingEntity;

public class NBTHelperImpl implements NBTHelper {

    @Override
    public String write(LivingEntity entity) throws IllegalArgumentException {
        try {
            EntityLiving livingEntity = NBTHelperFactory.getEntityLiving(entity);
            NBTTagCompound nbt = new NBTTagCompound();
            livingEntity.f(nbt);
            return nbt.toString();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to write NBT: " + entity);
        }
    }

    @Override
    public void read(LivingEntity entity, String nbtString) throws IllegalArgumentException {
        try {
            NBTTagCompound nbt = MojangsonParser.a(nbtString);
            nbt.a("UUID", entity.getUniqueId());
            EntityLiving livingEntity = NBTHelperFactory.getEntityLiving(entity);
            livingEntity.g(nbt);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to read NBT: " + nbtString);
        }
    }
}
