package de.clickism.clickvillagers.hopper.ticking;

import de.clickism.clickvillagers.ClickVillagers;
import de.clickism.clickvillagers.hopper.config.HopperConfig;
import de.clickism.clickvillagers.hopper.util.HopperUtil;
import de.clickism.clickvillagers.villager.ClaimManager;
import de.clickism.clickvillagers.villager.PickupManager;
import io.papermc.lib.PaperLib;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;

import java.util.*;

import static de.clickism.clickvillagers.hopper.util.HopperUtil.*;

public class HopperTicker {
    private final PickupManager pickupManager;
    private final HopperConfig hopperConfig;
    private final ClaimManager claimManager;
    private final HopperStorage storage;

    public HopperTicker(PickupManager pickupManager, HopperConfig hopperConfig, ClaimManager claimManager, HopperStorage storage) {
        this.pickupManager = pickupManager;
        this.hopperConfig = hopperConfig;
        this.claimManager = claimManager;
        this.storage = storage;
    }

    public void tickAll() {
        List<Chunk> toRemove = new ArrayList<>();

        storage.getAll().forEach((chunk, set) -> {
            tickChunk(chunk, set);
            if (set.isEmpty()) {
                toRemove.add(chunk);
            }
        });

        toRemove.forEach(storage::remove);
    }


    private void tickChunk(Chunk chunk, Set<BlockVector> vectors) {
        if (!chunk.isLoaded()) return; // Should never happen
        World world = chunk.getWorld();
        Location hopperLoc = new Location(world, 0, 0, 0);

        for (Iterator<BlockVector> setIterator = vectors.iterator(); setIterator.hasNext(); ) {
            BlockVector vector = setIterator.next();

            hopperLoc.set(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
            tickHopper(setIterator, hopperLoc);
        }
    }

    private void tickHopper(Iterator<BlockVector> setIterator, Location hopperLoc) {
        Hopper hopper = getValidVillagerHopper(hopperLoc);

        // Hopper doesn't exist anymore for some reason, remove it
        // This should never happen
        if (hopper == null) {
            ClickVillagers.LOGGER.warning("Removed invalid hopper at " + hopperLoc);
            setIterator.remove();
            return;
        }

        List<LivingEntity> villagers = getEligibleVillagers(hopperLoc, hopperConfig, claimManager);
        if (villagers.isEmpty()) return;

        Inventory inv = hopper.getInventory();
        for (LivingEntity entity : villagers) {
            try {
                ItemStack item = pickupManager.createVillagerItem(entity);
                Map<Integer, ItemStack> leftovers = inv.addItem(item);

                if (leftovers.isEmpty()) {
                    entity.remove(); // only now, after success
                } else {
                    // No space left in the hopper
                    break;
                }
            } catch (Exception ex) {
                ClickVillagers.LOGGER.warning("Failed to write villager data: " + ex.getMessage());
            }
        }
    }

    private Hopper getValidVillagerHopper(Location loc) {
        Block block = loc.getBlock();
        if (block.getType() != Material.HOPPER) return null;
        if (!(PaperLib.getBlockState(block, false).getState() instanceof Hopper hopper)) return null;
        if (!HopperUtil.isVillagerHopper(hopper.getPersistentDataContainer())) return null;
        return hopper;
    }

}
