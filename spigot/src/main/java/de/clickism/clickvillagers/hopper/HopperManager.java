/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.hopper;

import de.clickism.clickvillagers.ClickVillagers;
import de.clickism.clickvillagers.hopper.config.HopperConfig;
import de.clickism.clickvillagers.hopper.event.HopperEvents;
import de.clickism.clickvillagers.hopper.ticking.HopperStorage;
import de.clickism.clickvillagers.hopper.ticking.HopperTicker;
import de.clickism.clickvillagers.hopper.util.HopperItemFactory;
import de.clickism.clickvillagers.villager.ClaimManager;
import de.clickism.clickvillagers.villager.PickupManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public class HopperManager {

    private final ClickVillagers plugin;

    private final HopperConfig hopperConfig;
    private final HopperStorage storage;
    private final HopperTicker ticker;
    private final HopperEvents events;

    private int tickerTaskId = -1;
    private boolean eventsRegistered = false;

    public HopperManager(ClickVillagers plugin, PickupManager pickupManager, ClaimManager claimManager) {
        this.plugin = plugin;

        // Initialize once
        this.hopperConfig = new HopperConfig();
        this.storage = new HopperStorage();
        this.ticker = new HopperTicker(pickupManager, hopperConfig, claimManager, storage);
        this.events = new HopperEvents(storage, hopperConfig);

        // Apply initial configuration
        reloadConfig();
    }

    public void reloadConfig() {
        disableTasks();
        unregisterEvents();

        // Always refresh recipe to avoid duplicates or stale data
        // TODO: Only unregister if the recipe has changed
        HopperItemFactory.unregisterRecipe();
        if (hopperConfig.recipeEnabled) {
            HopperItemFactory.registerRecipe();
        }

        // Enable ticking logic and events only if configured
        if (hopperConfig.tickingEnabled) {
            tickerTaskId = Bukkit.getScheduler().runTaskTimer(
                    plugin, ticker::tickAll, hopperConfig.tickRate, hopperConfig.tickRate
            ).getTaskId();
            Bukkit.getPluginManager().registerEvents(events, plugin);
            eventsRegistered = true;
        }
    }

    private void disableTasks() {
        if (tickerTaskId != -1) {
            Bukkit.getScheduler().cancelTask(tickerTaskId);
            tickerTaskId = -1;
        }
    }

    private void unregisterEvents() {
        if (eventsRegistered) {
            HandlerList.unregisterAll(events);
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
