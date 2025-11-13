package de.clickism.clickvillagers.hopper.event;

import de.clickism.clickvillagers.hopper.ticking.HopperStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkListener implements Listener {
    private final HopperStorage storage;

    public ChunkListener(HopperStorage storage) {
        this.storage = storage;
    }

    @EventHandler
    private void onChunkLoad(ChunkLoadEvent event) {
        storage.loadHoppersInChunk(event.getChunk());
    }

    @EventHandler
    private void onChunkUnload(ChunkUnloadEvent event) {
        storage.unloadHoppersInChunk(event.getChunk());
    }

}
