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
        storage.getAll().forEach(this::tickChunk);
    }

    private void tickChunk(Chunk chunk, Set<BlockVector> vectors) {
        if (!chunk.isLoaded()) return;
        World world = chunk.getWorld();

        for (BlockVector vector : vectors) {
            Location hopperLoc = new Location(world, vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
            tickHopper(hopperLoc);
        }
    }

    private void tickHopper(Location hopperLoc) {
        Block block = hopperLoc.getBlock();
        if (block.getType() != Material.HOPPER) return;

        List<LivingEntity> villagers = getEligibleVillagers(hopperLoc, hopperConfig, claimManager);
        if (villagers.isEmpty()) return;

        for (LivingEntity entity : villagers) {
            if (!(PaperLib.getBlockState(block, false).getState() instanceof Hopper hopper)) continue;
            if (!HopperUtil.isVillagerHopper(hopper.getPersistentDataContainer())) return;

            Inventory inv = hopper.getInventory();
            if (!hasSpace(inv)) continue;

            try {
                ItemStack item = pickupManager.toItemStack(entity);
                inv.addItem(item);
            } catch (Exception ex) {
                ClickVillagers.LOGGER.warning("Failed to write villager data: " + ex.getMessage());
            }
        }
    }
}
