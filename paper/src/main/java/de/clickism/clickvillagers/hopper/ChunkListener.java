/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.hopper;

import de.clickism.clickvillagers.listener.AutoRegistered;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.Plugin;

public class ChunkListener implements Listener {
    private final HopperStorage storage;

    @AutoRegistered
    public ChunkListener(Plugin plugin, HopperStorage storage) {
        this.storage = storage;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void onChunkLoad(ChunkLoadEvent event) {
        storage.loadHoppersInChunk(event.getChunk());
    }

    @EventHandler
    private void onChunkUnload(ChunkUnloadEvent event) {
        storage.unloadHoppersInChunk(event.getChunk());
    }

    @EventHandler
    private void onWorldLoad(WorldLoadEvent event) {
        storage.loadHoppersInWorld(event.getWorld());
    }

    @EventHandler
    private void onWorldUnload(WorldUnloadEvent event) {
        storage.unloadHoppersInWorld(event.getWorld());
    }

}
