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
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class DispenserListener implements Listener {

    private static final BukkitScheduler SCHEDULER = Bukkit.getScheduler();
    private final PickupManager pickupManager;

    @AutoRegistered
    public DispenserListener(JavaPlugin plugin, PickupManager pickupManager) {
        this.pickupManager = pickupManager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    // Dispensers only work on 1.21 and above
    @EventHandler(ignoreCancelled = true)
    private void onDispense(BlockDispenseEvent event) {
        ItemStack item = event.getItem();
        if (!pickupManager.isVillager(item)) return;
        Block block = event.getBlock();
        BlockState state = block.getState();
        if (!(state instanceof Dispenser dispenser) /*&& !(state instanceof Dropper)*/) return;
        event.setCancelled(true);
        if (event instanceof BlockDispenseArmorEvent) {
            // Skip armor dispense events
            return;
        }
        Directional directional = (Directional) state.getBlockData();
        Location location = block.getRelative(directional.getFacing()).getLocation().add(.5, 0, .5);
        try {
            pickupManager.spawnFromItemStack(item, location);
            SCHEDULER.runTask(ClickVillagers.INSTANCE, () -> {
                dispenser.getInventory().removeItem(item);
                dispenser.update();
            });
        } catch (IllegalArgumentException exception) {
            ClickVillagers.LOGGER.severe("Failed to spawn villager from NBT data: " + exception.getMessage());
        }
    }
}
