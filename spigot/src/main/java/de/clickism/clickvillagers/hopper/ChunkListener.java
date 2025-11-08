/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.hopper;

import de.clickism.clickvillagers.listener.AutoRegistered;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ChunkListener implements Listener {
    private final HopperManager hopperManager;

    @AutoRegistered
    public ChunkListener(JavaPlugin plugin, HopperManager hopperManager) {
        this.hopperManager = hopperManager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        hopperManager.loadHoppersInChunk(event.getChunk());
        Bukkit.broadcastMessage("Loaded hoppers in chunk " + event.getChunk().getX() + ", " + event.getChunk().getZ());
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        hopperManager.unloadHoppersInChunk(event.getChunk());
        Bukkit.broadcastMessage("Unloaded hoppers in chunk " + event.getChunk().getX() + ", " + event.getChunk().getZ());
    }
}
