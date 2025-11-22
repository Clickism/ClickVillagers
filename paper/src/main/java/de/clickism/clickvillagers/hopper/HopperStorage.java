/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.hopper;

import de.clickism.clickvillagers.command.Permission;
import de.clickism.clickvillagers.hopper.util.HopperUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Manages the storage and tracking of all loaded custom villager hoppers
 * across loaded chunks.
 * <p>
 * This class keeps an in-memory registry of all hopper block locations
 * that are identified as "villager hoppers" (via a persistent data key).
 * It is used by ticking systems to efficiently process active hoppers
 * without scanning the world.
 */
public class HopperStorage {

    /**
     * A map of loaded chunks to their corresponding villager hopper positions.
     */
    private final Map<Chunk, Set<BlockVector>> loadedHoppers = new HashMap<>();

    public HopperStorage() {
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                loadHoppersInChunk(chunk);
            }
        }
    }

    /**
     * Registers a villager hopper in the specified chunk.
     *
     * @param chunk the chunk the hopper resides in
     * @param block the hopper block to register
     */
    public void add(Chunk chunk, Block block) {
        loadedHoppers.computeIfAbsent(chunk, c -> new HashSet<>())
                .add(block.getLocation().toVector().toBlockVector());
    }

    /**
     * Removes a villager hopper from the specified chunk.
     * <p>
     * If no more hoppers remain in the chunk, the chunk entry
     * is removed from the map.
     *
     * @param chunk the chunk the hopper resides in
     * @param block the hopper block to remove
     */
    public void removeHopper(Chunk chunk, Block block) {
        Set<BlockVector> set = loadedHoppers.get(chunk);
        if (set == null) return;
        set.remove(block.getLocation().toVector().toBlockVector());
        removeChunkIfEmpty(chunk);
    }

    /**
     * Removes a chunk entry if no more hoppers remain in the chunk
     *
     * @param chunk the chunk the hopper resides in
     */
    public void removeChunkIfEmpty(Chunk chunk) {
        Set<BlockVector> set = loadedHoppers.get(chunk);
        if (set == null || set.isEmpty()) {
            loadedHoppers.remove(chunk);
        }
    }

    /**
     * Scans a chunk for villager hoppers and loads them into memory.
     * <p>
     * This method should be called when a chunk is loaded to ensure that
     * all relevant hoppers are registered for ticking or processing.
     *
     * @param chunk the chunk to scan
     */
    public void loadHoppersInChunk(Chunk chunk) {
        Set<BlockVector> set = new HashSet<>();
        for (BlockState state : chunk.getTileEntities(block -> block.getType() == Material.HOPPER, false)) {
            if (!(state instanceof Hopper hopper)) continue;
            PersistentDataContainer data = hopper.getPersistentDataContainer();
            if (!HopperUtil.hasVillagerHopperData(data)) continue;
            set.add(hopper.getLocation().toVector().toBlockVector());
        }

        loadedHoppers.put(chunk, set);
    }

    /**
     * Unloads all villager hoppers tracked in the specified chunk.
     * <p>
     * This method should be called when a chunk is unloaded to
     * free memory and prevent invalid references.
     *
     * @param chunk the chunk being unloaded
     */
    public void unloadHoppersInChunk(Chunk chunk) {
        loadedHoppers.remove(chunk);
    }

    /**
     * Gets the number of villager hoppers tracked in the given chunk.
     *
     * @param chunk the chunk to check
     * @return the number of villager hoppers in the chunk
     */
    public int getHopperCount(Chunk chunk) {
        Set<BlockVector> set = loadedHoppers.get(chunk);
        return set == null ? 0 : set.size();
    }

    /**
     * Checks whether the specified chunk has reached or exceeded
     * the hopper limit in the config.
     *
     * @param chunk the chunk to check
     * @return true if the limit is reached and the player doesn't have the bypass permission, false otherwise
     */
    public boolean isHopperLimitReachedOrBypassed(Chunk chunk, Player player, int limit) {
        if (limit < 0) return false;
        return getHopperCount(chunk) >= limit && Permission.BYPASS_LIMITS.lacks(player);
    }

    /**
     * Returns the total number of villager hoppers currently loaded.
     *
     * @return the total count of loaded villager hoppers
     */
    public int getTotalCount() {
        return loadedHoppers.values().stream().mapToInt(Set::size).sum();
    }

    /**
     * Iterates over each loaded chunk and its associated villager hopper positions,
     * applying the given consumer function.
     *
     * @param consumer the function to apply for each chunk and its hopper positions
     */
    public void forEachChunk(@NotNull BiConsumer<Chunk, Set<BlockVector>> consumer) {
        loadedHoppers.forEach(consumer);
    }
}
