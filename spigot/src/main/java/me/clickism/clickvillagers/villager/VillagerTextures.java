/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.villager;

import me.clickism.clickvillagers.ClickVillagers;
import me.clickism.clickvillagers.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

public class VillagerTextures {

    public static final String DEFAULT_TEXTURE = "http://textures.minecraft.net/texture/d14bff1a38c9154e5ec84ce5cf00c58768e068eb42b2d89a6bbd29787590106b";
    public static final String BABY_TEXTURE = "http://textures.minecraft.net/texture/3d7788826b9ac4deaf383b387947085211447ed50fdc21bf71c230048dd5986f";
    public static final String ZOMBIE_TEXTURE = "http://textures.minecraft.net/texture/e5e08a8776c1764c3fe6a6ddd412dfcb87f41331dad479ac96c21df4bf3ac89c";
    public static final String WANDERING_TRADER_TEXTURE = "http://textures.minecraft.net/texture/ee011aac817259f2b48da3e5ef266094703866608b3d7d1754432bf249cd2234";

    public static final Map<Villager.Profession, String> TEXTURE_MAP = Map.ofEntries(
            Map.entry(Villager.Profession.FISHERMAN, "http://textures.minecraft.net/texture/61d644761f706d31c99a593c8d5f7cbbd4372d73fbee8464f482fa6c139d97d4"),
            Map.entry(Villager.Profession.ARMORER, "http://textures.minecraft.net/texture/f522db92f188ebc7713cf35b4cbaed1cfe2642a5986c3bde993f5cfb3727664c"),
            Map.entry(Villager.Profession.BUTCHER, "http://textures.minecraft.net/texture/c6774d2df515eceae9eed291c1b40f94adf71df0ab81c7191402e1a45b3a2087"),
            Map.entry(Villager.Profession.CARTOGRAPHER, "http://textures.minecraft.net/texture/94248dd0680305ad73b214e8c6b00094e27a4ddd8034676921f905130b858bdb"),
            Map.entry(Villager.Profession.CLERIC, "http://textures.minecraft.net/texture/a8856eaafad96d76fa3b5edd0e3b5f45ee49a3067306ad94df9ab3bd5b2d142d"),
            Map.entry(Villager.Profession.FARMER, "http://textures.minecraft.net/texture/d01e035a3d8d6126072bcbe52a97913ace93552a99995b5d4070d6783a31e909"),
            Map.entry(Villager.Profession.FLETCHER, "http://textures.minecraft.net/texture/d831830a7bd3b1ab05beb98dc2f9fc5ea550b3cf649fd94d483da7cd39f7c063"),
            Map.entry(Villager.Profession.LEATHERWORKER, "http://textures.minecraft.net/texture/f76cf8b7378e889395d538e6354a17a3de6b294bb6bf8db9c701951c68d3c0e6"),
            Map.entry(Villager.Profession.LIBRARIAN, "http://textures.minecraft.net/texture/e66a53fc707ce1ff88a576ef40200ce8d49fae4acad1e3b3789c7d1cc1cc541a"),
            Map.entry(Villager.Profession.MASON, "http://textures.minecraft.net/texture/2c02c3ffd5705ab488b305d57ff0168e26de70fd3f739e839661ab947dff37b1"),
            Map.entry(Villager.Profession.NITWIT, "http://textures.minecraft.net/texture/35e799dbfaf98287dfbafce970612c8f075168977aacc30989d34a4a5fcdf429"),
            Map.entry(Villager.Profession.SHEPHERD, "http://textures.minecraft.net/texture/19e04a752596f939f581930414561b175454d45a0506501e7d2488295a5d5de"),
            Map.entry(Villager.Profession.TOOLSMITH, "http://textures.minecraft.net/texture/7dfa07fd1244eb8945f4ededd00426750b77ef5dfbaf03ed775633459ece415a"),
            Map.entry(Villager.Profession.WEAPONSMITH, "http://textures.minecraft.net/texture/5e409b958bc4fe045e95d325e6e97a533137e33fec7042ac027b30bb693a9d42")
    );

    public static ItemStack getDefaultVillagerItem(LivingEntity entity) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        setEntityTexture(item, entity);
        return item;
    }

    public static void setEntityTexture(ItemStack item, LivingEntity entity) {
        try {
            URL texture = getTexture(entity);
            setEntityTexture(item, texture);
        } catch (Exception exception) {
            ClickVillagers.LOGGER.warning("Failed to set villager texture: " + exception.getMessage());
        }
    }

    private static void setEntityTexture(ItemStack item, URL texture) {
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta == null) return;
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        profile.getTextures().setSkin(texture);
        meta.setOwnerProfile(profile);
        item.setItemMeta(meta);
    }

    private static URL getTexture(Entity entity) throws Exception {
        if (entity instanceof WanderingTrader) {
            return URI.create(WANDERING_TRADER_TEXTURE).toURL();
        }
        boolean isZombie = entity instanceof ZombieVillager;
        if (!(entity instanceof Villager) && !isZombie) {
            throw new IllegalArgumentException("Unsupported entity type: " + entity.getClass().getName());
        }
        LivingEntity villager = (LivingEntity) entity;
        Villager.Profession profession = Utils.getVillagerProfession(villager);
        boolean isAdult = ((Ageable) villager).isAdult();
        if (!isAdult) {
            return URI.create(BABY_TEXTURE).toURL();
        }
        if (isZombie) {
            return URI.create(ZOMBIE_TEXTURE).toURL();
        }
        return URI.create(TEXTURE_MAP.getOrDefault(profession, DEFAULT_TEXTURE)).toURL();
    }
}
