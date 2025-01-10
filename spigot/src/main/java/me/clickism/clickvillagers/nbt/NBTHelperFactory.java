package me.clickism.clickvillagers.nbt;

import net.minecraft.world.entity.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.lang.reflect.Method;

public class NBTHelperFactory {

    public static final Class<?> CRAFT_LIVING_ENTITY_CLASS;
    public static final Method GET_HANDLE_METHOD;

    static {
        try {
            String suffix = getCraftbukkitPackageSuffix();
            CRAFT_LIVING_ENTITY_CLASS = Class.forName("org.bukkit.craftbukkit" + suffix + ".entity.CraftLivingEntity");
            GET_HANDLE_METHOD = CRAFT_LIVING_ENTITY_CLASS.getMethod("getHandle");
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public static NBTHelper create() {
        return new NBTHelperImpl();
    }

    public static EntityLiving getEntityLiving(LivingEntity entity) throws Exception {
        Object craftLivingEntity = CRAFT_LIVING_ENTITY_CLASS.cast(entity);
        return (EntityLiving) GET_HANDLE_METHOD.invoke(craftLivingEntity);
    }

    private static String getCraftbukkitPackageSuffix() {
        try {
            return "." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        } catch (Exception e) {
            return ""; // Paper 1.20.5 and above doesn't use relocation
        }
    }
}
