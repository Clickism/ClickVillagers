/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.hopper;

import de.clickism.clickvillagers.command.Permission;
import de.clickism.clickvillagers.hopper.util.HopperUtil;
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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
     * A unique key representing a chunk's coordinates.
     *
     * @param x the chunk's x-coordinate
     * @param z the chunk's z-coordinate
     */
    public record ChunkKey(int x, int z) {
        public static ChunkKey of(Chunk chunk) {
            return new ChunkKey(chunk.getX(), chunk.getZ());
        }
    }

    /**
     * A map of loaded chunks to their corresponding villager hopper positions.
     */
    private final Map<World, Map<ChunkKey, Set<BlockVector>>> loadedHoppers = new ConcurrentHashMap<>();

    public HopperStorage() {
        // Chunks loaded via ChunkListener
    }

    /**
     * Registers a villager hopper in the specified chunk.
     *
     * @param chunk the chunk the hopper resides in
     * @param block the hopper block to register
     */
    public void add(Chunk chunk, Block block) {
        loadedHoppers.computeIfAbsent(chunk.getWorld(), c -> new ConcurrentHashMap<>())
                .computeIfAbsent(ChunkKey.of(chunk), k -> ConcurrentHashMap.newKeySet())
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
        var map = loadedHoppers.get(chunk.getWorld());
        if (map == null) return;
        var set = map.get(ChunkKey.of(chunk));
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
        var map = loadedHoppers.get(chunk.getWorld());
        if (map == null) return;
        ChunkKey chunkKey = ChunkKey.of(chunk);
        var set = map.get(chunkKey);
        if (set == null || set.isEmpty()) {
            map.remove(chunkKey);
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
        Set<BlockVector> set = ConcurrentHashMap.newKeySet();
        for (BlockState state : chunk.getTileEntities(block -> block.getType() == Material.HOPPER, false)) {
            if (!(state instanceof Hopper hopper)) continue;
            PersistentDataContainer data = hopper.getPersistentDataContainer();
            if (!HopperUtil.hasVillagerHopperData(data)) continue;
            set.add(hopper.getLocation().toVector().toBlockVector());
        }

        loadedHoppers.computeIfAbsent(chunk.getWorld(), c -> new ConcurrentHashMap<>())
                .put(ChunkKey.of(chunk), set);
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
        var map = loadedHoppers.get(chunk.getWorld());
        if (map != null) {
            map.remove(ChunkKey.of(chunk));
        }
    }

    /**
     * Gets the number of villager hoppers tracked in the given chunk.
     *
     * @param chunk the chunk to check
     * @return the number of villager hoppers in the chunk
     */
    public int getHopperCount(Chunk chunk) {
        var map = loadedHoppers.get(chunk.getWorld());
        if (map == null) return 0;
        var set = map.get(ChunkKey.of(chunk));
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
        int total = 0;
        for (var worldEntry : loadedHoppers.entrySet()) {
            for (var chunkEntry : worldEntry.getValue().entrySet()) {
                total += chunkEntry.getValue().size();
            }
        }
        return total;
    }

    /**
     * Iterates over each loaded chunk and its associated villager hopper positions,
     * applying the given consumer function.
     *
     * @param consumer the function to apply for each chunk and its hopper positions
     */
    public void forEachChunk(@NotNull ChunkHopperConsumer consumer) {
        for (var worldEntry : loadedHoppers.entrySet()) {
            World world = worldEntry.getKey();
            for (var chunkEntry : worldEntry.getValue().entrySet()) {
                ChunkKey chunkKey = chunkEntry.getKey();
                Set<BlockVector> set = chunkEntry.getValue();
                consumer.accept(world, chunkKey, set);
            }
        }
    }

    @FunctionalInterface
    public interface ChunkHopperConsumer {
        void accept(World world, ChunkKey chunkKey, Set<BlockVector> hoppers);
    }
}
