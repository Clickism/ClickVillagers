package de.clickism.clickvillagers.hopper.event;

import de.clickism.clickvillagers.command.Permission;
import de.clickism.clickvillagers.hopper.util.HopperItemFactory;
import de.clickism.clickvillagers.hopper.ticking.HopperStorage;
import de.clickism.clickvillagers.hopper.config.HopperConfig;
import de.clickism.clickvillagers.hopper.util.HopperDisplayUtil;
import de.clickism.clickvillagers.hopper.util.HopperUtil;
import io.papermc.lib.PaperLib;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class HopperEvents implements Listener {

    private final HopperStorage storage;
    private final HopperConfig config;

    public HopperEvents(HopperStorage storage, HopperConfig config) {
        this.storage = storage;
        this.config = config;
    }

    // TODO: Improve this
    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getItemInHand();

        if (item.getType() != Material.HOPPER) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (!meta.getPersistentDataContainer().has(HopperItemFactory.VILLAGER_HOPPER_KEY, PersistentDataType.BOOLEAN))
            return;

        if (Permission.HOPPER.lacksAndNotify(player)) {
            e.setCancelled(true);
            return;
        }

        Block block = e.getBlockPlaced();
        if (!(block.getState() instanceof Hopper hopper)) return;

        HopperDisplayUtil.applyMark(hopper, config);
        storage.add(block.getChunk(), block);
        HopperUtil.playPlaceSound(block, player);
    }

    // TODO: Improve this
    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        if (!(PaperLib.getBlockState(block, false).getState() instanceof Hopper hopper)) return;

        var data = hopper.getPersistentDataContainer();
        if (!data.has(HopperItemFactory.VILLAGER_HOPPER_KEY, PersistentDataType.BOOLEAN)) return;

        HopperDisplayUtil.removeDisplayIfExists(data);

        e.setDropItems(false);
        World world = block.getWorld();
        Location loc = block.getLocation();

        Player player = e.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE) {
            world.dropItemNaturally(loc, HopperItemFactory.getHopperItem());
        }

        hopper.getInventory().forEach(i -> {
            if (i != null) world.dropItemNaturally(loc, i);
        });
        storage.remove(block.getChunk(), block);
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
