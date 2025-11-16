/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.hopper;

import de.clickism.clickvillagers.ClickVillagers;
import de.clickism.clickvillagers.command.Permission;
import de.clickism.clickvillagers.message.Message;
import de.clickism.clickvillagers.villager.ClaimManager;
import de.clickism.clickvillagers.villager.PickupManager;
import de.clickism.clickgui.menu.Icon;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Hopper;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.*;

import static de.clickism.clickvillagers.ClickVillagersConfig.*;

public class HopperManager implements Listener {
    public static final Set<BlockVector> ALL_HOPPERS_STATS = new HashSet<>();
    private static final Transformation FRAME_TRANSFORMATION = new Transformation(
            new Vector3f(-.525f, -.235f, -.525f),
            new AxisAngle4f(),
            new Vector3f(1.05f, .1f, 1.05f),
            new AxisAngle4f()
    );

    public static final NamespacedKey VILLAGER_HOPPER_KEY = new NamespacedKey(ClickVillagers.INSTANCE, "villager_hopper");
    public static final NamespacedKey DISPLAY_UUID_KEY = new NamespacedKey(ClickVillagers.INSTANCE, "display_uuid");

    private final PickupManager pickupManager;
    private final ClaimManager claimManager;

    private final ItemStack villagerHopper;

    private final Map<Chunk, Set<BlockVector>> loadedHopperChunks = new HashMap<>();

    public HopperManager(JavaPlugin plugin, PickupManager pickupManager, ClaimManager claimManager) {
        this.pickupManager = pickupManager;
        this.claimManager = claimManager;
        this.villagerHopper = createHopperItem();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        if (CONFIG.get(HOPPER_RECIPE)) {
            registerHopperRecipe(plugin);
        }
        if (CONFIG.get(TICK_HOPPERS)) {
            startTickingHoppers(plugin);
        }
    }

    /**
     * (Re)Load villager hoppers in the given chunk.
     *
     * @param chunk The chunk to load hoppers from.
     */
    public void loadHoppersInChunk(Chunk chunk) {
        Set<BlockVector> hopperLocations = new HashSet<>();
        for (BlockState tileEntity : chunk.getTileEntities()) {
            if (tileEntity.getType() != Material.HOPPER) return;
            Hopper hopper = (Hopper) tileEntity;
            if (!hopper.getPersistentDataContainer().has(VILLAGER_HOPPER_KEY, PersistentDataType.BOOLEAN)) return;
            hopperLocations.add(hopper.getBlock().getLocation().toVector().toBlockVector());
        }
        loadedHopperChunks.put(chunk, hopperLocations);
        ALL_HOPPERS_STATS.addAll(hopperLocations);
    }

    /**
     * Unload villager hoppers in the given chunk.
     *
     * @param chunk The chunk to unload hoppers from.
     */
    public void unloadHoppersInChunk(Chunk chunk) {
        loadedHopperChunks.remove(chunk);
    }

    private void registerHopperRecipe(JavaPlugin plugin) {
        ShapelessRecipe hopperRecipe = new ShapelessRecipe(new NamespacedKey(plugin, "villager_hopper"), villagerHopper);
        hopperRecipe.addIngredient(Material.HOPPER);
        hopperRecipe.addIngredient(Material.EMERALD);
        Bukkit.addRecipe(hopperRecipe);
    }

    @EventHandler(ignoreCancelled = true)
    private void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() != Material.HOPPER) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (!meta.getPersistentDataContainer().has(VILLAGER_HOPPER_KEY, PersistentDataType.BOOLEAN)) return;
        // Villager hopper placed
        Block block = event.getBlockPlaced();
        if (!(block.getState() instanceof Hopper hopper)) return;
        // Check permission
        if (Permission.HOPPER.lacksAndNotify(player)) {
            event.setCancelled(true);
            return;
        }
        // Check limit
        int limit = CONFIG.get(HOPPER_LIMIT_PER_CHUNK);
        if (isHopperLimitReached(block.getChunk(), limit) && Permission.BYPASS_LIMITS.lacks(player)) {
            Message.HOPPER_LIMIT_REACHED.send(player, limit);
            event.setCancelled(true);
            return;
        }
        // Mark hopper as villager hopper
        markHopper(hopper);
        sendHopperPlaceMessage(player, block);
    }

    /**
     * Mark the given hopper as a villager hopper.
     * @param hopper The hopper to mark.
     */
    public void markHopper(Hopper hopper) {
        if (CONFIG.get(HOPPER_BLOCK_DISPLAY)) {
            Block block = hopper.getBlock();
            BlockDisplay display = createBlockDisplay(block);
            markHopper(hopper, display.getUniqueId());
            return;
        }
        markHopper(hopper, null);
    }

    /**
     * Mark the given hopper as a villager hopper.
     * @param hopper The hopper to mark.
     * @param displayUUID The UUID of the block display, or null if no block display is used.
     */
    public void markHopper(Hopper hopper, @Nullable UUID displayUUID) {
        PersistentDataContainer data = hopper.getPersistentDataContainer();
        data.set(VILLAGER_HOPPER_KEY, PersistentDataType.BOOLEAN, true);
        if (displayUUID != null) {
            data.set(DISPLAY_UUID_KEY, PersistentDataType.STRING, displayUUID.toString());
        } else {
            data.remove(DISPLAY_UUID_KEY);
        }
        hopper.setCustomName(ChatColor.DARK_GRAY + "📥 " + ChatColor.BOLD + Message.VILLAGER_HOPPER);
        hopper.update();
        Chunk chunk = hopper.getChunk();
        loadedHopperChunks.computeIfAbsent(chunk, key -> new HashSet<>())
                .add(hopper.getBlock().getLocation().toVector().toBlockVector());
    }

    private boolean isHopperLimitReached(Chunk chunk, int limit) {
        if (limit < 0) return false;
        Set<BlockVector> loadedHoppers = loadedHopperChunks.get(chunk);
        return loadedHoppers != null && loadedHoppers.size() >= limit;
    }

    @EventHandler(ignoreCancelled = true)
    private void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!(block.getState() instanceof Hopper hopper)) return;
        PersistentDataContainer data = hopper.getPersistentDataContainer();
        if (!data.has(VILLAGER_HOPPER_KEY, PersistentDataType.BOOLEAN)) return;
        removeBlockDisplayIfExists(data);
        // Hopper broken
        event.setDropItems(false);
        Player player = event.getPlayer();
        World world = block.getWorld();
        Location location = block.getLocation();
        if (player.getGameMode() != GameMode.CREATIVE) {
            world.dropItemNaturally(location, villagerHopper.clone());
        }
        hopper.getInventory().forEach(item -> {
            if (item == null) return;
            world.dropItemNaturally(location, item);
        });
        sendHopperBreakMessage(player, block);
        // Remove from loaded hoppers
        Chunk chunk = block.getChunk();
        Set<BlockVector> hopperLocations = loadedHopperChunks.get(chunk);
        if (hopperLocations != null) {
            hopperLocations.remove(block.getLocation().toVector().toBlockVector());
            if (hopperLocations.isEmpty()) {
                loadedHopperChunks.remove(chunk);
            }
        }
    }

    private void removeBlockDisplayIfExists(PersistentDataContainer data) {
        String uuidString = data.get(DISPLAY_UUID_KEY, PersistentDataType.STRING);
        if (uuidString == null) return;
        UUID uuid = UUID.fromString(uuidString);
        Entity entity = Bukkit.getEntity(uuid);
        if (entity == null) return;
        entity.remove();
    }

    private void startTickingHoppers(JavaPlugin plugin) {
        boolean ignoreBabies = CONFIG.get(IGNORE_BABY_VILLAGERS);
        boolean ignoreClaimed = CONFIG.get(IGNORE_CLAIMED_VILLAGERS);
        int tickRate = CONFIG.get(HOPPER_TICK_RATE);
        Bukkit.getScheduler().runTaskTimer(plugin, () -> tickHoppers(ignoreBabies, ignoreClaimed), tickRate, tickRate);
    }

    private void tickHoppers(boolean ignoreBabies, boolean ignoreClaimed) {
        loadedHopperChunks.forEach(((chunk, blockVectors) -> {
            tickChunk(chunk, blockVectors, ignoreBabies, ignoreClaimed);
        }));
    }

    private void tickChunk(Chunk chunk, Set<BlockVector> blockVectors, boolean ignoreBabies, boolean ignoreClaimed) {
        World world = chunk.getWorld();
        // Skip if unloaded for some reason
        if (!chunk.isLoaded()) return;
        List<Entity> villagers = getFilteredVillagers(chunk.getEntities(), ignoreBabies, ignoreClaimed);
        for (BlockVector vector : blockVectors) {
            Block block = world.getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
            tickHopper(block, villagers);
        }
    }

    private void tickHopper(Block block, List<Entity> villagers) {
        if (block.getType() != Material.HOPPER) return;
        Block blockAbove = block.getRelative(BlockFace.UP);
        Material material = blockAbove.getType();
        if (material.isOccluding() || material == Material.HOPPER) return;
        Hopper hopper = null;
        for (Entity entity : villagers) {
            if (!isInHopper(entity, block.getLocation())) continue;
            if (hopper == null) {
                hopper = (Hopper) block.getState();
            }
            // Check if hopper
            if (!hopper.getPersistentDataContainer().has(VILLAGER_HOPPER_KEY, PersistentDataType.BOOLEAN)) return;
            Inventory inventory = hopper.getInventory();
            if (!hasSpace(inventory)) return;
            try {
                ItemStack item = pickupManager.toItemStack((LivingEntity) entity);
                inventory.addItem(item);
            } catch (Exception exception) {
                ClickVillagers.LOGGER.severe("Failed to write villager data: " + exception.getMessage());
            }
        }
    }

    private List<Entity> getFilteredVillagers(Entity[] entities, boolean ignoreBabies, boolean ignoreClaimed) {
        return Arrays.stream(entities)
                .filter(entity -> {
                    EntityType type = entity.getType();
                    // Check if entity is a villager
                    if (type != EntityType.VILLAGER && type != EntityType.ZOMBIE_VILLAGER) return false;
                    if (type == EntityType.ZOMBIE_VILLAGER && !CONFIG.get(ALLOW_ZOMBIE_VILLAGERS)) return false;
                    Block block = entity.getLocation().getBlock();
                    // Check if entity is a baby villager
                    if (ignoreBabies && entity instanceof Villager && !((Ageable) entity).isAdult()) return false;
                    // Check if entity is a claimed villager
                    if (ignoreClaimed && claimManager.hasOwner((LivingEntity) entity)) return false;
                    // Check if entity is in a hopper
                    return block.getType() == Material.HOPPER
                           || block.getRelative(BlockFace.DOWN).getType() == Material.HOPPER;
                })
                .toList();
    }

    private static boolean isInHopper(Entity entity, Location hopper) {
        Location location = entity.getLocation();
        int x1 = location.getBlockX();
        int y1 = location.getBlockY();
        int z1 = location.getBlockZ();
        int x2 = hopper.getBlockX();
        int y2 = hopper.getBlockY();
        int z2 = hopper.getBlockZ();
        return x1 == x2 && (y1 == y2 || y1 == y2 + 1) && z1 == z2;
    }

    private static boolean hasSpace(Inventory inventory) {
        for (ItemStack item : inventory) {
            if (item == null || item.getType() == Material.AIR) {
                return true;
            }
        }
        return false;
    }

    private static ItemStack createHopperItem() {
        return Icon.of(Material.HOPPER)
                .setName(ChatColor.GREEN + Message.VILLAGER_HOPPER.toString())
                .setLore(Message.VILLAGER_HOPPER.getLore())
                .addEnchantmentGlint()
                .applyToMeta(meta ->
                        meta.getPersistentDataContainer().set(VILLAGER_HOPPER_KEY, PersistentDataType.BOOLEAN, true)
                ).get();
    }

    private static BlockDisplay createBlockDisplay(Block block) {
        Location location = block.getLocation().clone().add(.5, 1, .5);
        return block.getWorld().spawn(location, BlockDisplay.class, display -> {
            display.setTransformation(FRAME_TRANSFORMATION);
            display.setShadowRadius(0f);
            display.setViewRange(CONFIG.get(HOPPER_BLOCK_DISPLAY_VIEW_RANGE));
            display.setBlock(Material.EMERALD_BLOCK.createBlockData());
        });
    }

    private static void sendHopperPlaceMessage(Player player, Block block) {
        Message.VILLAGER_HOPPER_PLACE.sendActionbarSilently(player);
        Location location = block.getLocation();
        World world = player.getWorld();
        world.playSound(location, Sound.BLOCK_METAL_PLACE, 1, .5f);
        world.playSound(location, Sound.BLOCK_IRON_TRAPDOOR_OPEN, 1, .5f);
    }

    private static void sendHopperBreakMessage(Player player, Block block) {
        Message.VILLAGER_HOPPER_BREAK.sendActionbarSilently(player);
        Location location = block.getLocation();
        World world = player.getWorld();
        world.playSound(location, Sound.BLOCK_METAL_BREAK, 1, .5f);
        world.playSound(location, Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1, .5f);
    }
}
