/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.listener;

import de.clickism.clickvillagers.ClickVillagers;
import de.clickism.clickvillagers.villager.PickupManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

import static de.clickism.clickvillagers.ClickVillagersConfig.*;

public class DispenserListener implements Listener {

    private final PickupManager pickupManager;

    @AutoRegistered
    public DispenserListener(JavaPlugin plugin, PickupManager pickupManager) {
        this.pickupManager = pickupManager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void onRedstone(BlockRedstoneEvent event) {
        if (!CONFIG.get(ENABLE_DISPENSERS)) return;
        Block block = event.getBlock();
        // Only trigger on rising edge
        if (!(event.getOldCurrent() == 0 && event.getNewCurrent() > 0)) return;

        List<Block> dispensers = new ArrayList<>();

        for (BlockFace face : BlockFace.values()) {
            Block relative = block.getRelative(face);
            if (relative.getType() == Material.DISPENSER) {
                dispensers.add(relative);
            }
        }

        if (dispensers.isEmpty()) return;

        Bukkit.getScheduler().runTaskLater(ClickVillagers.INSTANCE, () -> {
            for (Block dispenser : dispensers) {
                if (!dispenser.isBlockIndirectlyPowered()) return;
                if (dispenser.getType() != Material.DISPENSER) return;
                dispenseMob(dispenser);
            }
        }, 1L);
    }

    private void dispenseMob(Block block) {
        Dispenser dispenser = (Dispenser) block.getState();
        Inventory inventory = dispenser.getSnapshotInventory();
        List<ItemStack> mobs = new ArrayList<>();
        for (ItemStack item : inventory) {
            if (item == null) continue;
            if (!pickupManager.isVillager(item)) continue;
            mobs.add(item);
        }
        if (mobs.isEmpty()) return;
        ItemStack item = mobs.get((int) (Math.random() * mobs.size()));
        try {
            BlockFace facing = ((Directional) dispenser.getBlockData()).getFacing();
            Location location = block.getRelative(facing).getLocation().add(.5, 0, .5);
            pickupManager.spawnFromItemStack(item, location);
            item.setAmount(item.getAmount() - 1);
            if (item.getAmount() <= 0) {
                item.setType(Material.AIR);
            }
            dispenser.update(true, false);
        } catch (IllegalArgumentException exception) {
            ClickVillagers.LOGGER.severe("Failed to spawn villager from NBT data: " + exception.getMessage());
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onDispense(BlockDispenseEvent event) {
        if (!CONFIG.get(ENABLE_DISPENSERS)) return;
        ItemStack item = event.getItem();
        if (!pickupManager.isVillager(item)) return;
        // Handled in redstone event
        event.setCancelled(true);
    }
}
