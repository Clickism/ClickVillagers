/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.villager;


import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.RegistryKey;
import net.minecraft.village.VillagerProfession;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

//? if >=1.20.5 {
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
//?}

public class VillagerTextures {

    public static final String DEFAULT_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDNkZmRlNmUyYTQ2YjQ4Y2MxODJkOGQwZGJlMjE5Mzc4YjkxMGYyMjQwZTg2OWZiNzIyNDU5MTFhNjUwNzRkMyJ9fX0=";
    public static final String BABY_TEXTURE = "ewogICJ0aW1lc3RhbXAiIDogMTczNDQ4OTE4OTUxNywKICAicHJvZmlsZUlkIiA6ICJjNWY3OWQ3ODkyNDA0ZGMwOGVhZjZiZDVlNGM4ZGYyYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJMb25naG9ybnM3MDkiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2Q3Nzg4ODI2YjlhYzRkZWFmMzgzYjM4Nzk0NzA4NTIxMTQ0N2VkNTBmZGMyMWJmNzFjMjMwMDQ4ZGQ1OTg2ZiIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9";
    public static final String ZOMBIE_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGM3NTA1ZjIyNGQ1MTY0YTExN2Q4YzY5ZjAxNWY5OWVmZjQzNDQ3MWM4YTJkZjkwNzA5NmM0MjQyYzM1MjRlOCJ9fX0=";

    //? if >=1.21.5 {
    public static final Map<RegistryKey<VillagerProfession>, String> TEXTURE_MAP = Map.ofEntries(
    //?} else
    /*public static final Map<VillagerProfession, String> TEXTURE_MAP = Map.ofEntries(*/
            Map.entry(VillagerProfession.FISHERMAN, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWMxNWU1ZmI1NmZhMTZiMDc0N2IxYmNiMDUzMzVmNTVkMWZhMzE1NjFjMDgyYjVlMzY0M2RiNTU2NTQxMDg1MiJ9fX0="),
            Map.entry(VillagerProfession.ARMORER, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjUyMmRiOTJmMTg4ZWJjNzcxM2NmMzViNGNiYWVkMWNmZTI2NDJhNTk4NmMzYmRlOTkzZjVjZmIzNzI3NjY0YyJ9fX0="),
            Map.entry(VillagerProfession.BUTCHER, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY3NzRkMmRmNTE1ZWNlYWU5ZWVkMjkxYzFiNDBmOTRhZGY3MWRmMGFiODFjNzE5MTQwMmUxYTQ1YjNhMjA4NyJ9fX0="),
            Map.entry(VillagerProfession.CARTOGRAPHER, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTQyNDhkZDA2ODAzMDVhZDczYjIxNGU4YzZiMDAwOTRlMjdhNGRkZDgwMzQ2NzY5MjFmOTA1MTMwYjg1OGJkYiJ9fX0="),
            Map.entry(VillagerProfession.CLERIC, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTg4NTZlYWFmYWQ5NmQ3NmZhM2I1ZWRkMGUzYjVmNDVlZTQ5YTMwNjczMDZhZDk0ZGY5YWIzYmQ1YjJkMTQyZCJ9fX0="),
            Map.entry(VillagerProfession.FARMER, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTVhMGIwN2UzNmVhZmRlY2YwNTljOGNiMTM0YTdiZjBhMTY3ZjkwMDk2NmYxMDk5MjUyZDkwMzI3NjQ2MWNjZSJ9fX0="),
            Map.entry(VillagerProfession.FLETCHER, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTc1MzJlOTBjNTczYTM5NGM3ODAyYWE0MTU4MzA1ODAyYjU5ZTY3ZjJhMmI3ZTNmZDAzNjNhYTZlYTQyYjg0MSJ9fX0="),
            Map.entry(VillagerProfession.LEATHERWORKER, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjc2Y2Y4YjczNzhlODg5Mzk1ZDUzOGU2MzU0YTE3YTNkZTZiMjk0YmI2YmY4ZGI5YzcwMTk1MWM2OGQzYzBlNiJ9fX0="),
            Map.entry(VillagerProfession.LIBRARIAN, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTY2YTUzZmM3MDdjZTFmZjg4YTU3NmVmNDAyMDBjZThkNDlmYWU0YWNhZDFlM2IzNzg5YzdkMWNjMWNjNTQxYSJ9fX0="),
            Map.entry(VillagerProfession.MASON, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmMwMmMzZmZkNTcwNWFiNDg4YjMwNWQ1N2ZmMDE2OGUyNmRlNzBmZDNmNzM5ZTgzOTY2MWFiOTQ3ZGZmMzdiMSJ9fX0="),
            Map.entry(VillagerProfession.NITWIT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzVlNzk5ZGJmYWY5ODI4N2RmYmFmY2U5NzA2MTJjOGYwNzUxNjg5NzdhYWNjMzA5ODlkMzRhNGE1ZmNkZjQyOSJ9fX0="),
            Map.entry(VillagerProfession.SHEPHERD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTllMDRhNzUyNTk2ZjkzOWY1ODE5MzA0MTQ1NjFiMTc1NDU0ZDQ1YTA1MDY1MDFlN2QyNDg4Mjk1YTVkNWRlIn19fQ=="),
            Map.entry(VillagerProfession.TOOLSMITH, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2RmYTA3ZmQxMjQ0ZWI4OTQ1ZjRlZGVkZDAwNDI2NzUwYjc3ZWY1ZGZiYWYwM2VkNzc1NjMzNDU5ZWNlNDE1YSJ9fX0="),
            Map.entry(VillagerProfession.WEAPONSMITH, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWU0MDliOTU4YmM0ZmUwNDVlOTVkMzI1ZTZlOTdhNTMzMTM3ZTMzZmVjNzA0MmFjMDI3YjMwYmI2OTNhOWQ0MiJ9fX0=")
    );

    public static void setEntityTexture(ItemStack itemStack, Entity entity) {
        setTexture(itemStack, getTexture(entity));
    }

    //? if >=1.20.5 {
    private static void setTexture(ItemStack itemStack, String texture) {
        PropertyMap propertyMap = new PropertyMap();
        propertyMap.put("textures", new Property("textures", texture));
        itemStack.set(DataComponentTypes.PROFILE,
                new ProfileComponent(Optional.empty(), Optional.of(UUID.randomUUID()), propertyMap));
    }
    //?} else {
    /*private static void setTexture(ItemStack itemStack, String texture) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", texture));
        itemStack.getOrCreateNbt().put("SkullOwner", NbtHelper.writeGameProfile(new NbtCompound(), profile));
    }
    *///?}

    public static String getTexture(Entity entity) {
        if (entity instanceof ZombieVillagerEntity) {
            return ZOMBIE_TEXTURE;
        }
        if (!(entity instanceof VillagerEntity villager)) return DEFAULT_TEXTURE;
        if (villager.isBaby()) {
            return BABY_TEXTURE;
        }
        //? if >=1.21.5 {
        String texture = TEXTURE_MAP.get(villager.getVillagerData().profession()
                .getKey().orElseThrow());
        //?} else
        /*String texture = TEXTURE_MAP.get(villager.getVillagerData().getProfession());*/
        if (texture != null) {
            return texture;
        }
        return DEFAULT_TEXTURE;
    }
}
