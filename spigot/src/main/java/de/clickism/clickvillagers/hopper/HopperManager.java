/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.hopper;

import de.clickism.clickvillagers.ClickVillagers;
import de.clickism.clickvillagers.hopper.config.HopperConfig;
import de.clickism.clickvillagers.hopper.event.ChunkListener;
import de.clickism.clickvillagers.hopper.event.HopperEvents;
import de.clickism.clickvillagers.hopper.ticking.HopperStorage;
import de.clickism.clickvillagers.hopper.ticking.HopperTicker;
import de.clickism.clickvillagers.hopper.util.HopperItemFactory;
import de.clickism.clickvillagers.villager.ClaimManager;
import de.clickism.clickvillagers.villager.PickupManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitTask;

public class HopperManager {

    private final ClickVillagers plugin;

    private final HopperConfig hopperConfig;
    private final HopperStorage storage;
    private final HopperTicker ticker;
    private final HopperEvents events;
    private final ChunkListener chunkListener;

    private BukkitTask tickerTask;
    private boolean eventsRegistered = false;

    public HopperManager(ClickVillagers plugin, PickupManager pickupManager, ClaimManager claimManager) {
        this.plugin = plugin;

        // Initialize once
        this.hopperConfig = new HopperConfig();
        this.storage = new HopperStorage();
        this.ticker = new HopperTicker(pickupManager, hopperConfig, claimManager, storage);
        this.events = new HopperEvents(storage, hopperConfig);
        this.chunkListener = new ChunkListener(storage);

        // Always register onPlace and onBreak in case the hopper feature was disabled after
        // Hoppers were already placed
        Bukkit.getPluginManager().registerEvents(events, plugin);

        // Apply initial configuration
        reloadConfig();
    }

    public void reloadConfig() {
        this.hopperConfig.reloadConfig();

        disableTasks();
        unregisterEvents();

        // Toggle recipe
        if (hopperConfig.recipeEnabled) {
            HopperItemFactory.registerRecipe();
        } else {
            HopperItemFactory.unregisterRecipe();
        }

        // Enable ticking logic and events only if configured
        if (hopperConfig.tickingEnabled) {
            tickerTask = Bukkit.getScheduler().runTaskTimer(
                    plugin, ticker::tickAll, hopperConfig.tickRate, hopperConfig.tickRate
            );
            Bukkit.getPluginManager().registerEvents(chunkListener, plugin);
            eventsRegistered = true;
        }
    }

    private void disableTasks() {
        if (tickerTask != null) {
            tickerTask.cancel();
        }
    }

    private void unregisterEvents() {
        if (eventsRegistered) {
            HandlerList.unregisterAll(chunkListener);
            eventsRegistered = false;
        }
    }

    public int getActiveHopperCount() {
        return this.storage.getTotalCount();
    }

    public HopperConfig getHopperConfig() {
        return hopperConfig;
    }
}
