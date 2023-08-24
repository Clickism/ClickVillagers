package me.clickism.clickvillagers.managers;

import me.clickism.clickvillagers.Utils;
import me.clickism.clickvillagers.config.Messages;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.UUID;

public class SkullManager {

    public static ItemStack getVillagerHeadItem(LivingEntity entity){
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta == null) {
            meta = (SkullMeta) Bukkit.getServer().getItemFactory().getItemMeta(Material.PLAYER_HEAD);
        }
        meta.setLore(Collections.singletonList(ChatColor.DARK_GRAY + Messages.get("right-click")));

        //Textures & Name
        try {
            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
            if (entity instanceof Villager) {
                Villager villager = (Villager) entity;

                //Display Name
                if (Utils.capitalize(villager.getProfession().toString()).equals("None")) {
                    meta.setDisplayName(ChatColor.YELLOW + Messages.get("villager"));
                } else {
                    meta.setDisplayName(ChatColor.YELLOW + Utils.capitalize(villager.getProfession().toString()) + " " + Messages.get("villager"));
                }
                if (!villager.isAdult()) {
                    meta.setDisplayName(ChatColor.YELLOW + Messages.get("baby-villager"));
                }

                //Textures
                if (villager.getProfession() == Villager.Profession.FISHERMAN) {
                    profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/61d644761f706d31c99a593c8d5f7cbbd4372d73fbee8464f482fa6c139d97d4"));
                } else if (villager.getProfession() == Villager.Profession.ARMORER) {
                    profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/f522db92f188ebc7713cf35b4cbaed1cfe2642a5986c3bde993f5cfb3727664c"));
                } else if (villager.getProfession() == Villager.Profession.BUTCHER) {
                    profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/c6774d2df515eceae9eed291c1b40f94adf71df0ab81c7191402e1a45b3a2087"));
                } else if (villager.getProfession() == Villager.Profession.CLERIC) {
                    profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/a8856eaafad96d76fa3b5edd0e3b5f45ee49a3067306ad94df9ab3bd5b2d142d"));
                } else if (villager.getProfession() == Villager.Profession.CARTOGRAPHER) {
                    profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/94248dd0680305ad73b214e8c6b00094e27a4ddd8034676921f905130b858bdb"));
                } else if (villager.getProfession() == Villager.Profession.FARMER) {
                    profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/d01e035a3d8d6126072bcbe52a97913ace93552a99995b5d4070d6783a31e909"));
                } else if (villager.getProfession() == Villager.Profession.FLETCHER) {
                    profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/d831830a7bd3b1ab05beb98dc2f9fc5ea550b3cf649fd94d483da7cd39f7c063"));
                } else if (villager.getProfession() == Villager.Profession.LEATHERWORKER) {
                    profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/f76cf8b7378e889395d538e6354a17a3de6b294bb6bf8db9c701951c68d3c0e6"));
                } else if (villager.getProfession() == Villager.Profession.MASON) {
                    profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/2c02c3ffd5705ab488b305d57ff0168e26de70fd3f739e839661ab947dff37b1"));
                } else if (villager.getProfession() == Villager.Profession.SHEPHERD) {
                    profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/19e04a752596f939f581930414561b175454d45a0506501e7d2488295a5d5de"));
                } else if (villager.getProfession() == Villager.Profession.TOOLSMITH) {
                    profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/7dfa07fd1244eb8945f4ededd00426750b77ef5dfbaf03ed775633459ece415a"));
                } else if (villager.getProfession() == Villager.Profession.WEAPONSMITH) {
                    profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/5e409b958bc4fe045e95d325e6e97a533137e33fec7042ac027b30bb693a9d42"));
                } else if (villager.getProfession() == Villager.Profession.LIBRARIAN) {
                    profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/e66a53fc707ce1ff88a576ef40200ce8d49fae4acad1e3b3789c7d1cc1cc541a"));
                } else if (villager.getProfession() == Villager.Profession.NITWIT) {
                    profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/35e799dbfaf98287dfbafce970612c8f075168977aacc30989d34a4a5fcdf429"));
                } else {
                    profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/d14bff1a38c9154e5ec84ce5cf00c58768e068eb42b2d89a6bbd29787590106b"));
                }
                if (!villager.isAdult()) {
                    profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/3d7788826b9ac4deaf383b387947085211447ed50fdc21bf71c230048dd5986f"));
                }
            } else {
                //Zombie Villager
                meta.setDisplayName(ChatColor.YELLOW + Messages.get("zombie-villager"));
                profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/e5e08a8776c1764c3fe6a6ddd412dfcb87f41331dad479ac96c21df4bf3ac89c"));
            }
            meta.setOwnerProfile(profile);
        } catch (MalformedURLException ignored) { }

        item.setItemMeta(meta);
        return item;
    }
}
