package me.clickism.clickvillagers.villager;

import me.clickism.clickvillagers.ClickVillagers;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class LegacyCompatibility {

    private static final NamespacedKey LEGACY_VILLAGER_UUID_KEY = new NamespacedKey(ClickVillagers.INSTANCE, "villager_uuid");
    private static final NamespacedKey LEGACY_OWNER_NAME_KEY = new NamespacedKey(ClickVillagers.INSTANCE, "villager_owner");
    private static final NamespacedKey LEGACY_TRADE_KEY = new NamespacedKey(ClickVillagers.INSTANCE, "villager_tradable");

    public static boolean isLegacyVillager(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(LEGACY_VILLAGER_UUID_KEY, PersistentDataType.STRING);
    }

    @NotNull
    public static LivingEntity spawnFromItemStack(ItemStack item, Location location) throws IllegalArgumentException {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) throw new IllegalArgumentException("Item meta is null.");
        String uuidString = meta.getPersistentDataContainer().get(LEGACY_VILLAGER_UUID_KEY, PersistentDataType.STRING);
        if (uuidString == null) throw new IllegalArgumentException("Legacy villager UUID not found.");
        UUID uuid = UUID.fromString(uuidString);
        LivingEntity entity = loadChunkAndGetVillager(uuid, location);
        unfreeze(entity);
        entity.teleport(location);
        convertData(entity);
        return entity;
    }

    private static void unfreeze(LivingEntity entity) {
        entity.setRemoveWhenFarAway(true);
        entity.setGravity(true);
        entity.setAI(true);
        Bukkit.getScheduler().runTaskLater(ClickVillagers.INSTANCE, task -> {
            entity.setInvulnerable(false);
        }, 2L);
    }

    private static LivingEntity loadChunkAndGetVillager(UUID uuid, Location location) {
        World world = location.getWorld();
        if (world == null) {
            throw new IllegalArgumentException("World not found.");
        }
        Chunk spawnChunk = world.getChunkAt(0, 0);
        spawnChunk.load(false);
        LivingEntity entity = (LivingEntity) Bukkit.getEntity(uuid);
        if (entity == null) {
            throw new IllegalArgumentException("Legacy villager entity not found.");
        }
        return entity;
    }

    public static void convertDataIfLegacy(LivingEntity entity) {
        if (entity.getPersistentDataContainer().has(LEGACY_OWNER_NAME_KEY, PersistentDataType.STRING)) {
            convertData(entity);
        }
    }

    @SuppressWarnings("deprecation")
    private static void convertData(LivingEntity entity) {
        PersistentDataContainer data = entity.getPersistentDataContainer();
        move(data, LEGACY_OWNER_NAME_KEY, ClaimManager.OWNER_NAME_KEY, PersistentDataType.STRING);
        move(data, LEGACY_TRADE_KEY, ClaimManager.TRADE_KEY, PersistentDataType.BOOLEAN);
        String ownerName = data.get(ClaimManager.OWNER_NAME_KEY, PersistentDataType.STRING);
        if (ownerName == null) return;
        UUID ownerUUID = Bukkit.getOfflinePlayer(ownerName).getUniqueId();
        data.set(ClaimManager.OWNER_KEY, PersistentDataType.STRING, ownerUUID.toString());
    }

    private static <T, Z> void move(PersistentDataContainer data, NamespacedKey fromKey, NamespacedKey toKey, PersistentDataType<T, Z> type) {
        if (!data.has(fromKey, PersistentDataType.STRING)) return;
        Z value = data.get(fromKey, type);
        if (value == null) return;
        data.set(toKey, type, value);
        data.remove(fromKey);
    }
}
