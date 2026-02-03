/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.hopper;

import de.clickism.clickvillagers.ClickVillagers;
import de.clickism.clickvillagers.hopper.util.HopperUtil;
import de.clickism.clickvillagers.villager.ClaimManager;
import de.clickism.clickvillagers.villager.PickupManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Set;

import static de.clickism.clickvillagers.ClickVillagersConfig.*;
import static de.clickism.clickvillagers.hopper.util.HopperUtil.getEligibleVillagers;

public class HopperTicker {
    private final PickupManager pickupManager;
    private final ClaimManager claimManager;
    private final HopperStorage storage;

    private boolean ignoreBabies;
    private boolean ignoreClaimed;

    public HopperTicker(PickupManager pickupManager, ClaimManager claimManager, HopperStorage storage) {
        this.pickupManager = pickupManager;
        this.claimManager = claimManager;
        this.storage = storage;
    }

    private static int getEmptySlots(Inventory inventory) {
        int emptySlots = 0;
        for (ItemStack item : inventory) {
            if (item == null || item.getType() == Material.AIR) {
                emptySlots++;
            }
        }
        return emptySlots;
    }

    /**
     * Ticks all registered villager hoppers in loaded chunks.
     * <p>
     * Loads config settings before ticking.
     */
    public void tickAll() {
        ignoreBabies = IGNORE_BABY_VILLAGERS.get();
        ignoreClaimed = IGNORE_CLAIMED_VILLAGERS.get();

        storage.forEachChunk((world, chunkKey, hoppers) -> {
            int chunkX = chunkKey.x();
            int chunkZ = chunkKey.z();

            Bukkit.getRegionScheduler().run(
                    ClickVillagers.INSTANCE,
                    world,
                    chunkX,
                    chunkZ,
                    task -> {
                        tickChunk(world, chunkX, chunkZ, hoppers);
                    }
            );
        });
    }

    private void tickChunk(World world, int chunkX, int chunkZ, Set<BlockVector> vectors) {
        if (!world.isChunkLoaded(chunkX, chunkZ)) return; // Should never happen... but it happened
        Location hopperLoc = new Location(world, 0, 0, 0);

        for (Iterator<BlockVector> setIterator = vectors.iterator(); setIterator.hasNext(); ) {
            BlockVector vector = setIterator.next();

            hopperLoc.set(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
            tickHopper(setIterator, hopperLoc);
        }
    }

    private void tickHopper(Iterator<BlockVector> setIterator, Location hopperLoc) {
        Hopper hopper = getVillagerHopper(hopperLoc);

        // Hopper doesn't exist anymore for some reason, remove it
        // This should never happen
        if (hopper == null) {
            Location center = hopperLoc.clone().add(0.5, 1, 0.5);
            World world = center.getWorld();
            if (world == null) return;
            // Tick hopper
            if (HOPPER_BLOCK_DISPLAY.get()) {
                for (BlockDisplay display : center.getNearbyEntitiesByType(BlockDisplay.class, 0.2, 0.2, 0.2)) {
                    // Ignore non-emerald displays
                    if (display.getBlock().getMaterial() != Material.EMERALD_BLOCK) continue;
                    display.remove();
                }
            }
            setIterator.remove();
            ClickVillagers.LOGGER.warning("Removed invalid hopper at " + hopperLoc);
            return;
        }

        var villagers = getEligibleVillagers(hopperLoc, claimManager, ignoreBabies, ignoreClaimed);
        if (villagers.isEmpty()) return;

        Inventory inventory = hopper.getInventory();
        int emptySlots = getEmptySlots(inventory);
        for (LivingEntity villager : villagers) {
            if (emptySlots <= 0) break;
            try {
                ItemStack item = pickupManager.toItemStack(villager);
                inventory.addItem(item);
            } catch (Exception exception) {
                ClickVillagers.LOGGER.severe("Failed to write villager data: " + exception.getMessage());
            }
        }
    }

    private @Nullable Hopper getVillagerHopper(Location location) {
        Block block = location.getBlock();
        if (block.getType() != Material.HOPPER) return null;
        if (!(block.getState(false) instanceof Hopper hopper)) return null;
        if (!HopperUtil.hasVillagerHopperData(hopper.getPersistentDataContainer())) return null;
        return hopper;
    }

}
