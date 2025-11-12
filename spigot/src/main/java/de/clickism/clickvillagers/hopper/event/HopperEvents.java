package de.clickism.clickvillagers.hopper.event;

import de.clickism.clickvillagers.command.Permission;
import de.clickism.clickvillagers.hopper.util.HopperItemFactory;
import de.clickism.clickvillagers.hopper.ticking.HopperStorage;
import de.clickism.clickvillagers.hopper.config.HopperConfig;
import de.clickism.clickvillagers.hopper.util.HopperDisplayUtil;
import de.clickism.clickvillagers.hopper.util.HopperUtil;
import de.clickism.clickvillagers.message.Message;
import io.papermc.lib.PaperLib;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.persistence.PersistentDataContainer;

public class HopperEvents implements Listener {

    private final HopperStorage storage;
    private final HopperConfig config;

    public HopperEvents(HopperStorage storage, HopperConfig config) {
        this.storage = storage;
        this.config = config;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();

        // Only process custom villager hopper items
        if (!HopperUtil.isVillagerHopperItem(e.getItemInHand())) return;

        // Deny placement if the player lacks the permission
        if (Permission.HOPPER.lacksAndNotify(player)) {
            e.setCancelled(true);
            return;
        }

        Block block = e.getBlockPlaced();

        // Enforce per-chunk villager hopper limits (unless bypass permission)
        if (storage.isHopperLimitReached(block.getChunk(), config.limitPerChunk, player)) {
            Message.HOPPER_LIMIT_REACHED.send(player, config.limitPerChunk);
            e.setCancelled(true);
            return;
        }

        // Get the placed hopper state safely and check again if it's a hopper
        if (!(PaperLib.getBlockState(block, false).getState() instanceof Hopper hopper)) return;

        // Set the PDC and add a DisplayEntity if enabled
        HopperDisplayUtil.applyMark(hopper, config);

        // Add hopper to storage
        storage.add(block.getChunk(), block);

        // Play feedback sound to confirm placement
        HopperUtil.playPlaceSound(block, player);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        if (block.getType() != Material.HOPPER) return;

        // Safely retrieve the hopper tile entity
        if (!(PaperLib.getBlockState(block, false).getState() instanceof Hopper hopper)) return;
        PersistentDataContainer data = hopper.getPersistentDataContainer();

        // Ignore normal hoppers, only handle custom ones
        if (!HopperUtil.isVillagerHopper(data)) return;

        // Remove any custom visual mark or display data
        HopperDisplayUtil.removeDisplayIfExists(data);

        // Prevent default drop (normal hopper item)
        e.setDropItems(false);

        World world = block.getWorld();
        Location loc = block.getLocation();
        Player player = e.getPlayer();

        // Drop the custom villager hopper item if not in creative
        if (player.getGameMode() != GameMode.CREATIVE) {
            world.dropItemNaturally(loc, HopperItemFactory.getHopperItem());
        }

        // Drop any items contained within the hopper's inventory
        // TODO: check if this can be improved
        hopper.getInventory().forEach(item -> {
            if (item != null) world.dropItemNaturally(loc, item);
        });

        // Remove from storage
        storage.remove(block.getChunk(), block);

        // Play a breaking sound effect
        HopperUtil.playBreakSound(block, player);
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
