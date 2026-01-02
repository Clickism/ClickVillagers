/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.hopper;

import de.clickism.clickvillagers.command.Permission;
import de.clickism.clickvillagers.hopper.util.HopperDisplayUtil;
import de.clickism.clickvillagers.hopper.util.HopperUtil;
import de.clickism.clickvillagers.listener.AutoRegistered;
import de.clickism.clickvillagers.message.Message;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import static de.clickism.clickvillagers.ClickVillagersConfig.HOPPER_LIMIT_PER_CHUNK;

public class BlockListener implements Listener {

    private final HopperStorage storage;

    @AutoRegistered
    public BlockListener(Plugin plugin, HopperStorage storage) {
        this.storage = storage;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!HopperUtil.isVillagerHopperItem(item)) return;
        if (Permission.HOPPER.lacksAndNotify(player)) {
            event.setCancelled(true);
            return;
        }
        // Villager hopper placed
        Block block = event.getBlockPlaced();
        // Enforce per-chunk villager hopper limits
        int limit = HOPPER_LIMIT_PER_CHUNK.get();
        if (storage.isHopperLimitReachedOrBypassed(block.getChunk(), player, limit)) {
            Message.HOPPER_LIMIT_REACHED.send(player, limit);
            event.setCancelled(true);
            return;
        }

        if (!(block.getState(false) instanceof Hopper hopper)) return;
        // Mark hopper
        HopperUtil.markHopper(hopper);
        storage.add(block.getChunk(), block);
        // Send message
        HopperUtil.sendHopperBreakMessage(player, block);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.HOPPER) return;
        if (!(block.getState(false) instanceof Hopper hopper)) return;
        // Ignore normal hoppers, only handle custom ones
        if (!HopperUtil.hasVillagerHopperData(hopper.getPersistentDataContainer())) return;
        HopperDisplayUtil.removeDisplayIfExists(hopper);
        // Prevent default drop (normal hopper item)
        event.setDropItems(false);

        World world = block.getWorld();
        Location loc = block.getLocation();
        Player player = event.getPlayer();
        // Drop the custom villager hopper item if not in creative
        if (player.getGameMode() != GameMode.CREATIVE) {
            world.dropItemNaturally(loc, HopperManager.HOPPER_ITEM);
        }
        // Drop any items contained within the hopper's inventory
        // TODO: check if this can be improved
        hopper.getInventory().forEach(item -> {
            if (item != null) {
                world.dropItemNaturally(loc, item);
            }
        });
        // Remove from storage
        storage.removeHopper(block.getChunk(), block);
        // Send message
        HopperUtil.sendHopperBreakMessage(player, block);
    }
}
