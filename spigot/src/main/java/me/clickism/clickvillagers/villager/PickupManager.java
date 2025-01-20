/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.villager;

import me.clickism.clickgui.menu.Icon;
import me.clickism.clickvillagers.ClickVillagers;
import me.clickism.clickvillagers.config.Permission;
import me.clickism.clickvillagers.config.Setting;
import me.clickism.clickvillagers.legacy.LegacyVillagerCompatibility;
import me.clickism.clickvillagers.listener.AutoRegistered;
import me.clickism.clickvillagers.message.Message;
import me.clickism.clickvillagers.nbt.NBTHelper;
import me.clickism.clickvillagers.util.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PickupManager implements Listener {
    private enum VillagerType {
        VILLAGER,
        ZOMBIE
    }

    public static final NamespacedKey VILLAGER_KEY = new NamespacedKey(ClickVillagers.INSTANCE, "villager");
    public static final NamespacedKey TYPE_KEY = new NamespacedKey(ClickVillagers.INSTANCE, "type");
    public static final NamespacedKey NBT_KEY = new NamespacedKey(ClickVillagers.INSTANCE, "nbt");

    private final NBTHelper nbtHelper;
    private final ClaimManager claimManager;
    private final AnchorManager anchorManager;

    @AutoRegistered
    public PickupManager(JavaPlugin plugin, NBTHelper nbtHelper, ClaimManager claimManager, AnchorManager anchorManager) {
        this.nbtHelper = nbtHelper;
        this.claimManager = claimManager;
        this.anchorManager = anchorManager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    private void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        ItemStack item = inventory.getItemInMainHand();
        if (item.getType() != Material.PLAYER_HEAD) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (!isVillager(item)) return;

        event.setCancelled(true);
        if (Permission.PLACE.lacksAndNotify(player)) return;
        Block block = event.getBlockPlaced();
        Location location = block.getLocation().add(.5, 0, .5);
        float yaw = player.getLocation().getYaw();
        location.setYaw((yaw + 360) % 360 - 180); // Face the villager towards the player
        try {
            spawnFromItemStack(item, location);
            item.setAmount(item.getAmount() - 1);
            inventory.setItemInMainHand(item);
            World world = player.getWorld();
            world.playSound(location, Sound.ENTITY_PLAYER_ATTACK_WEAK, 1, .5f);
            Block blockBelow = block.getRelative(BlockFace.DOWN);
            world.spawnParticle(Particle.BLOCK_CRACK, location, 30, blockBelow.getBlockData());
        } catch (IllegalArgumentException exception) {
            Message.READ_ERROR.send(player);
            ClickVillagers.LOGGER.severe("Failed to read villager data: " + exception.getMessage());
        }
    }

    @NotNull
    public ItemStack toItemStack(LivingEntity entity) throws IllegalArgumentException {
        if (!(entity instanceof Villager) && !(entity instanceof ZombieVillager)) {
            throw new IllegalArgumentException("Entity is not a villager");
        }
        ItemStack item = createItem(entity);
        writeData(entity, item);
        entity.remove();
        return item;
    }

    private void writeData(LivingEntity entity, ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(VILLAGER_KEY, PersistentDataType.BOOLEAN, true);
        data.set(TYPE_KEY, PersistentDataType.STRING, (entity instanceof ZombieVillager)
                ? VillagerType.ZOMBIE.toString()
                : VillagerType.VILLAGER.toString());
        String nbt = nbtHelper.write(entity);
        data.set(NBT_KEY, PersistentDataType.STRING, nbt);
        item.setItemMeta(meta);
    }

    public void sendPickupEffect(LivingEntity entity) {
        Location location = entity.getLocation().add(0, .25, 0);
        World world = entity.getWorld();
        world.spawnParticle(Particle.SWEEP_ATTACK, location, 1);
        world.playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, .5f);
    }

    @NotNull
    public LivingEntity spawnFromItemStack(ItemStack item, Location location) throws IllegalArgumentException {
        if (LegacyVillagerCompatibility.isLegacyVillager(item)) {
            return LegacyVillagerCompatibility.spawnFromItemStack(item, location);
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) throw new IllegalArgumentException("ItemMeta is null");
        PersistentDataContainer data = meta.getPersistentDataContainer();
        if (!data.has(VILLAGER_KEY, PersistentDataType.BOOLEAN))
            throw new IllegalArgumentException("Item is not a villager");
        if (!data.has(TYPE_KEY, PersistentDataType.STRING))
            throw new IllegalArgumentException("Villager type is missing");
        VillagerType villagerType = VillagerType.valueOf(data.get(TYPE_KEY, PersistentDataType.STRING));
        World world = location.getWorld();
        if (world == null) throw new IllegalArgumentException("World is null");
        EntityType type = switch (villagerType) {
            case VILLAGER -> EntityType.VILLAGER;
            case ZOMBIE -> EntityType.ZOMBIE_VILLAGER;
        };
        LivingEntity entity = (LivingEntity) world.spawnEntity(location, type);
        String nbt = data.get(NBT_KEY, PersistentDataType.STRING);
        nbtHelper.read(entity, nbt);
        entity.teleport(location);
        return entity;
    }

    public boolean isVillager(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        PersistentDataContainer data = meta.getPersistentDataContainer();
        return data.has(VILLAGER_KEY, PersistentDataType.BOOLEAN)
               || LegacyVillagerCompatibility.isLegacyVillager(item);
    }

    private ItemStack createItem(LivingEntity entity) {
        String customName = entity.getCustomName();
        Villager.Profession profession = Utils.getVillagerProfession(entity);
        boolean adult = ((Ageable) entity).isAdult();
        int modelData = Setting.getCustomModelData(profession, !adult, entity instanceof ZombieVillager);
        Icon icon = Message.VILLAGER.toIcon(Material.PLAYER_HEAD)
                .setName(ChatColor.YELLOW + getName(customName, profession, adult))
                .runIf(modelData != 0,
                        i -> i.applyToMeta(meta -> meta.setCustomModelData(modelData)))
                .runIf(claimManager.hasOwner(entity),
                        i -> i.addLoreLine("&6🔑 " + Message.INFO_OWNER + ": &f" + claimManager.getOwnerName(entity)))
                .runIf(anchorManager.isAnchored(entity),
                        i -> i.addLoreLine("&3⚓ " + Message.INFO_ANCHORED))
                .runIf(claimManager.hasOwner(entity) && !claimManager.isTradeOpen(entity),
                        i -> i.addLoreLine("&c👥 " + Message.INFO_TRADE_CLOSED));
        VillagerTextures.setEntityTexture(icon.get(), entity);
        return icon.get();
    }

    private static String getName(@Nullable String customName, Villager.Profession profession, boolean adult) {
        if (customName != null) {
            return "\"" + customName + "\"";
        }
        if (!adult) {
            return Message.BABY_VILLAGER.toString();
        }
        if (profession == Villager.Profession.NONE) {
            return Message.VILLAGER.toString();
        }
        String professionName = Message.get("profession." + profession.toString().toLowerCase());
        return professionName + " " + Message.VILLAGER;
    }
}
