package me.clickism.clickvillagers.managers;

import me.clickism.clickvillagers.ClickVillagers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.UUID;

public class VillagerManager {

    public static void setPlugin(ClickVillagers pl) {
        plugin = pl;
    }
    static ClickVillagers plugin;

    public static ItemStack turnVillagerIntoHead(LivingEntity entity) {
        ItemStack item = SkullManager.getVillagerHeadItem(entity);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(new NamespacedKey(plugin, "villager_uuid"), PersistentDataType.STRING, entity.getUniqueId().toString());
        dataContainer.set(new NamespacedKey(plugin, "villager_owner"), PersistentDataType.STRING, VillagerData.getOwner(entity));
        dataContainer.set(new NamespacedKey(plugin, "villager_tradable"), PersistentDataType.BOOLEAN, VillagerData.isTradable(entity));
        item.setItemMeta(meta);
        entity.setRemoveWhenFarAway(false);
        entity.setInvulnerable(true);
        entity.setGravity(false);
        entity.setAI(false);
        entity.teleport(new Location(Bukkit.getWorlds().get(0), 0, -70, 0));
        return item;
    }

    @Nullable
    public static LivingEntity getVillagerFromHead(ItemStack head) {
        ItemMeta meta = head.getItemMeta();
        PersistentDataContainer itemDataContainer = meta.getPersistentDataContainer();
        if (itemDataContainer.has(new NamespacedKey(plugin, "villager_uuid"), PersistentDataType.STRING)) {
            UUID uuid = UUID.fromString(itemDataContainer.get(new NamespacedKey(plugin, "villager_uuid"), PersistentDataType.STRING));
            LivingEntity entity = (LivingEntity) Bukkit.getEntity(uuid);
            if (entity != null) {
                PersistentDataContainer entityDataContainer = meta.getPersistentDataContainer();
                entityDataContainer.set(new NamespacedKey(plugin, "villager_owner"), PersistentDataType.STRING, VillagerData.getOwner(head));
                entityDataContainer.set(new NamespacedKey(plugin, "villager_tradable"), PersistentDataType.BOOLEAN, VillagerData.isTradable(head));
                entity.setRemoveWhenFarAway(true);
                entity.setGravity(true);
                entity.setAI(true);
                Bukkit.getScheduler().runTaskLater(plugin, task -> {
                    entity.setInvulnerable(false);
                },2L);
                return (LivingEntity) Bukkit.getEntity(uuid);
            }
        }
        return null;
    }

    public static boolean isVillagerHead(ItemStack head) {
        if (head.getType() == Material.PLAYER_HEAD || head.getType() == Material.PLAYER_WALL_HEAD) {
            ItemMeta meta = head.getItemMeta();
            PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
            if (dataContainer.has(new NamespacedKey(plugin, "villager_uuid"), PersistentDataType.STRING)) {
                return true;
            }
        }
        return false;
    }
}
