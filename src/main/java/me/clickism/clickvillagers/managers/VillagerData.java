package me.clickism.clickvillagers.managers;

import me.clickism.clickvillagers.ClickVillagers;
import me.clickism.clickvillagers.managers.data.DataManager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class VillagerData {

    public static void setPlugin(ClickVillagers pl) {
        plugin = pl;
    }

    static ClickVillagers plugin;

    public static String getOwner(ItemStack head) {
        ItemMeta meta = head.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        if (dataContainer.has(new NamespacedKey(plugin, "villager_owner"), PersistentDataType.STRING)) {
            return (dataContainer.get(new NamespacedKey(plugin, "villager_owner"), PersistentDataType.STRING));
        }
        return "";
    }

    public static String getOwner(LivingEntity entity) {
        PersistentDataContainer dataContainer = entity.getPersistentDataContainer();
        if (dataContainer.has(new NamespacedKey(plugin, "villager_owner"), PersistentDataType.STRING)) {
            return (dataContainer.get(new NamespacedKey(plugin, "villager_owner"), PersistentDataType.STRING));
        }
        return "";
    }

    public static void setOwner(LivingEntity entity, Player owner) {
        entity.setRemoveWhenFarAway(false);
        PersistentDataContainer dataContainer = entity.getPersistentDataContainer();
        dataContainer.set(new NamespacedKey(plugin, "villager_owner"), PersistentDataType.STRING, owner.getName());
    }

    public static void removeOwner(LivingEntity entity) {
        entity.setRemoveWhenFarAway(true);
        PersistentDataContainer dataContainer = entity.getPersistentDataContainer();
        dataContainer.remove(new NamespacedKey(plugin, "villager_owner"));
    }

    public static List<String> getPartners(String player) {
        DataManager data = ClickVillagers.getData();
        if (data.getConfig().get("players." + player + ".partners") != null) {
            return (List<String>) data.getConfig().get("players." + player + ".partners");
        } else {
            return new ArrayList<>();
        }
    }

    public static void addPartner(String player, String partner) {
        DataManager data = ClickVillagers.getData();
        List<String> partners = getPartners(player);
        partners.add(partner);
        data.getConfig().set("players." + player + ".partners", partners);
        data.saveConfig();
    }

    public static void removePartner(String player, String partner) {
        DataManager data = ClickVillagers.getData();
        List<String> partners = getPartners(player);
        partners.remove(partner);
        data.getConfig().set("players." + player + ".partners", partners);
        data.saveConfig();
    }

    public static boolean isPartner(String partner, String owner) {
        return getPartners(owner).contains(partner);
    }

    public static boolean isClaimed(ItemStack head) {
        return !getOwner(head).equals("");
    }

    public static boolean isClaimed(LivingEntity entity) {
        return !getOwner(entity).equals("");
    }

    public static boolean isTradable(ItemStack head) {
        ItemMeta meta = head.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        if (dataContainer.has(new NamespacedKey(plugin, "villager_tradable"), PersistentDataType.BOOLEAN)) {
            return dataContainer.get(new NamespacedKey(plugin, "villager_tradable"), PersistentDataType.BOOLEAN);
        }
        return true;
    }

    public static boolean isTradable(LivingEntity entity) {
        PersistentDataContainer dataContainer = entity.getPersistentDataContainer();
        if (dataContainer.has(new NamespacedKey(plugin, "villager_tradable"), PersistentDataType.BOOLEAN)) {
            return dataContainer.get(new NamespacedKey(plugin, "villager_tradable"), PersistentDataType.BOOLEAN);
        }
        return true;
    }

    public static void setTradable(LivingEntity entity, boolean tradable) {
        PersistentDataContainer dataContainer = entity.getPersistentDataContainer();
        dataContainer.set(new NamespacedKey(plugin, "villager_tradable"), PersistentDataType.BOOLEAN, tradable);
    }
}
