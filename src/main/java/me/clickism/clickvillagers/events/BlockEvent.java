package me.clickism.clickvillagers.events;

import me.clickism.clickvillagers.ClickVillagers;
import me.clickism.clickvillagers.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockEvent implements Listener {

    static ClickVillagers plugin;
    public static void setPlugin(ClickVillagers pl) {
        plugin = pl;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (e.getBlockPlaced().getType() == Material.PLAYER_HEAD || e.getBlockPlaced().getType() == Material.PLAYER_WALL_HEAD) {
            Villager villager = ClickVillagers.getVillagerFromHead(e.getItemInHand());
            if (villager != null) {
                e.getBlockPlaced().setType(Material.AIR);
                villager.teleport(e.getBlockPlaced().getLocation().clone().add(.5,0,.5));
                villager.setInvisible(false);
                villager.setGravity(true);
                villager.setAI(true);
                Bukkit.getScheduler().runTaskLater(plugin, task -> {
                    villager.setInvulnerable(false);
                },2L);
                if (e.getPlayer().getGameMode() == GameMode.CREATIVE) {
                    e.getPlayer().getInventory().removeItem(e.getItemInHand());
                }
            }
        } else if (e.getBlockPlaced().getType() == Material.HOPPER) {
            if (e.getItemInHand().isSimilar(ClickVillagers.getVillagerHopper())) {
                //Create Villager Hopper
                ClickVillagers.addVillagerHopper(e.getBlockPlaced().getLocation());
                e.getPlayer().sendMessage(ChatColor.GOLD + ">> " + ChatColor.GREEN + "You placed a villager hopper.");
                Utils.playConfirmSound(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() == Material.HOPPER) {
            if (ClickVillagers.isVillagerHopper(e.getBlock().getLocation())) {
                //Remove Villager Hopper
                e.setCancelled(true);
                e.getBlock().setType(Material.AIR);
                e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), ClickVillagers.getVillagerHopper());
                ClickVillagers.removeVillagerHopper(e.getBlock().getLocation());
                e.getPlayer().sendMessage(ChatColor.GOLD + ">> " + ChatColor.RED + "You destroyed a villager hopper.");
                Utils.playFailSound(e.getPlayer());
            }
        }
    }
}
