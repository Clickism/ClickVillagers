/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.villager;

import de.clickism.clickvillagers.ClickVillagers;
import de.clickism.clickvillagers.listener.AutoRegistered;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static de.clickism.clickvillagers.ClickVillagersConfig.CLAIMED_DAMAGE;
import static de.clickism.clickvillagers.ClickVillagersConfig.CLAIMED_IMMUNE_KILL_COMMAND;

public class ClaimManager implements Listener {

    public static final NamespacedKey OWNER_KEY = new NamespacedKey(ClickVillagers.INSTANCE, "owner");
    public static final NamespacedKey OWNER_NAME_KEY = new NamespacedKey(ClickVillagers.INSTANCE, "owner_name");
    public static final NamespacedKey TRADE_KEY = new NamespacedKey(ClickVillagers.INSTANCE, "trade");

    @AutoRegistered
    public ClaimManager(JavaPlugin plugin) {
        if (!CLAIMED_DAMAGE.get()) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        EntityType type = entity.getType();
        if (type != EntityType.VILLAGER && type != EntityType.ZOMBIE_VILLAGER) return;
        LivingEntity villager = (LivingEntity) entity;
        if (!hasOwner(villager)) return;
        if (!CLAIMED_IMMUNE_KILL_COMMAND.get()
            && event.getCause() == EntityDamageEvent.DamageCause.KILL) return;
        event.setCancelled(true);
    }

    public boolean isOwner(LivingEntity entity, Player player) {
        return player.getUniqueId().equals(getOwnerUUID(entity));
    }

    public boolean hasOwner(LivingEntity entity) {
        return getOwnerUUID(entity) != null;
    }

    @Nullable
    public UUID getOwnerUUID(LivingEntity entity) {
        String uuidString = read(entity, OWNER_KEY, PersistentDataType.STRING);
        if (uuidString == null) return null;
        return UUID.fromString(uuidString);
    }

    public String getOwnerName(LivingEntity entity) {
        UUID ownerUUID = getOwnerUUID(entity);
        if (ownerUUID == null) return "?";
        String name = Bukkit.getOfflinePlayer(ownerUUID).getName();
        if (name != null) {
            return name;
        }
        name = readOrDefault(entity, OWNER_NAME_KEY, PersistentDataType.STRING, "?");
        return name;
    }

    public void setOwner(LivingEntity entity, Player owner) {
        write(entity, OWNER_KEY, PersistentDataType.STRING, owner.getUniqueId().toString());
        write(entity, OWNER_NAME_KEY, PersistentDataType.STRING, owner.getName());
    }

    public void removeOwner(LivingEntity entity) {
        remove(entity, OWNER_KEY, PersistentDataType.STRING);
        remove(entity, OWNER_NAME_KEY, PersistentDataType.STRING);
    }

    public boolean isTradeOpen(LivingEntity entity) {
        return readOrDefault(entity, TRADE_KEY, PersistentDataType.BOOLEAN, true);
    }

    public void setTradeOpen(LivingEntity entity, boolean trade) {
        write(entity, TRADE_KEY, PersistentDataType.BOOLEAN, trade);
    }

    protected <T, Z> void write(LivingEntity entity, NamespacedKey key, PersistentDataType<T, Z> type, Z obj) {
        entity.getPersistentDataContainer().set(key, type, obj);
    }

    protected <T, Z> Z readOrDefault(LivingEntity entity, NamespacedKey key, PersistentDataType<T, Z> type, Z defaultValue) {
        return entity.getPersistentDataContainer().getOrDefault(key, type, defaultValue);
    }

    @Nullable
    protected <T, Z> Z read(LivingEntity entity, NamespacedKey key, PersistentDataType<T, Z> type) {
        return entity.getPersistentDataContainer().get(key, type);
    }

    protected <T, Z> void remove(LivingEntity entity, NamespacedKey key, PersistentDataType<T, Z> type) {
        entity.getPersistentDataContainer().remove(key);
    }
}
