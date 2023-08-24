package me.clickism.clickvillagers.managers;

import me.clickism.clickvillagers.ClickVillagers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.UUID;

public class VillagerManager {

    public static void setPlugin(ClickVillagers plugin) {
        VillagerManager.plugin = plugin;
    }

    static ClickVillagers plugin;

    public static ItemStack turnVillagerIntoHead(LivingEntity entity) {
        ItemStack item = SkullManager.getVillagerHeadItem(entity);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(new NamespacedKey(plugin, "villager_uuid"), PersistentDataType.STRING, entity.getUniqueId().toString());
        item.setItemMeta(meta);
        entity.setRemoveWhenFarAway(false);
        entity.setInvisible(true);
        entity.setInvulnerable(true);
        entity.setGravity(false);
        entity.setAI(false);
        entity.teleport(new Location(Bukkit.getWorlds().get(0), 55000, -70, -55000));
        return item;
    }

    @Nullable
    public static LivingEntity getVillagerFromHead(ItemStack head) {
        ItemMeta meta = head.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        if (dataContainer.has(new NamespacedKey(plugin, "villager_uuid"), PersistentDataType.STRING)) {
            UUID uuid = UUID.fromString(dataContainer.get(new NamespacedKey(plugin, "villager_uuid"), PersistentDataType.STRING));
            LivingEntity entity = (LivingEntity) Bukkit.getEntity(uuid);
            if (entity != null) {
                entity.setRemoveWhenFarAway(true);
                entity.setInvisible(false);
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
}
