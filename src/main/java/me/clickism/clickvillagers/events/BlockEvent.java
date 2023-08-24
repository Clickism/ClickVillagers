package me.clickism.clickvillagers.events;

import me.clickism.clickvillagers.*;
import me.clickism.clickvillagers.config.Messages;
import me.clickism.clickvillagers.config.Settings;
import me.clickism.clickvillagers.managers.HopperManager;
import me.clickism.clickvillagers.managers.VillagerManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
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
            LivingEntity villager = VillagerManager.getVillagerFromHead(e.getItemInHand());
            if (villager != null) {
                e.getBlockPlaced().setType(Material.AIR);
                villager.teleport(e.getBlockPlaced().getLocation().clone().add(.5,0,.5));
                if (e.getPlayer().getGameMode() == GameMode.CREATIVE) {
                    e.getPlayer().getInventory().removeItem(e.getItemInHand());
                }
            } else {
                e.setCancelled(true);
                e.getPlayer().sendMessage(Messages.get("no-data"));
            }
        } else if (e.getBlockPlaced().getType() == Material.HOPPER) {
            if (!e.getItemInHand().hasItemMeta()) return;
            if (e.getItemInHand().getItemMeta().hasEnchant(Enchantment.DURABILITY)) {
                if (!Settings.get("enable-villager-hoppers")) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(Messages.get("hopper-disabled"));
                    return;
                }
                //Create Villager Hopper
                HopperManager.addVillagerHopper(e.getBlockPlaced().getLocation());
                e.getPlayer().sendMessage(Messages.get("place-hopper"));
                Utils.playConfirmSound(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() == Material.HOPPER) {
            if (HopperManager.isVillagerHopper(e.getBlock().getLocation())) {
                //Remove Villager Hopper
                e.getBlock().getDrops().clear();
                e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), HopperManager.getVillagerHopper());
                HopperManager.removeVillagerHopper(e.getBlock().getLocation());
                e.getPlayer().sendMessage(Messages.get("break-hopper"));
                Utils.playFailSound(e.getPlayer());
            }
        }
    }
}
