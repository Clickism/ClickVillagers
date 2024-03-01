package me.clickism.clickvillagers.events;

import me.clickism.clickvillagers.*;
import me.clickism.clickvillagers.config.Messages;
import me.clickism.clickvillagers.config.Settings;
import me.clickism.clickvillagers.managers.HopperManager;
import me.clickism.clickvillagers.managers.VillagerData;
import me.clickism.clickvillagers.managers.VillagerManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Hopper;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockEvent implements Listener {

    static ClickVillagers plugin;
    public static void setPlugin(ClickVillagers pl) {
        plugin = pl;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        if (e.getBlockPlaced().getType() == Material.PLAYER_HEAD || e.getBlockPlaced().getType() == Material.PLAYER_WALL_HEAD) {
            //Place villager back
            if (!VillagerManager.isVillagerHead(e.getItemInHand())) return;
            if (!e.getPlayer().hasPermission("clickvillagers.pickup")) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(Messages.get("no-permission"));
                Utils.playFailSound(e.getPlayer());
                return;
            }
            if (VillagerData.isClaimed(e.getItemInHand())) {
                if (!VillagerData.getOwner(e.getItemInHand()).equals(e.getPlayer().getName()) && !e.getPlayer().hasPermission("clickvillagers.bypass-claims")) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(Messages.get("belongs-to") + VillagerData.getOwner(e.getItemInHand()));
                    Utils.playFailSound(e.getPlayer());
                    return;
                }
            }
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
                Utils.playFailSound(e.getPlayer());
            }
        } else if (e.getBlockPlaced().getType() == Material.HOPPER) {
            //Hopper
            if (!e.getItemInHand().hasItemMeta()) return;
            if (e.getItemInHand().getItemMeta().hasEnchant(Enchantment.DURABILITY)) {
                //Create Villager Hopper
                if (!Settings.get("enable-villager-hoppers")) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(Messages.get("hopper-disabled"));
                    Utils.playFailSound(e.getPlayer());
                    return;
                }
                if (!e.getPlayer().hasPermission("clickvillagers.hopper")) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(Messages.get("no-permission"));
                    Utils.playFailSound(e.getPlayer());
                    return;
                }
                HopperManager.addVillagerHopper(e.getBlockPlaced().getLocation());
                e.getPlayer().sendMessage(Messages.get("place-hopper"));
                Utils.playConfirmSound(e.getPlayer());
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() == Material.HOPPER) {
            if (HopperManager.isVillagerHopper(e.getBlock().getLocation())) {
                //Remove Villager Hopper
                e.setDropItems(false);
                for (ItemStack content : ((Hopper) e.getBlock().getState()).getInventory().getContents()) {
                    if (content != null) {
                        e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), content);
                    }
                }
                if (e.getPlayer().getGameMode() != GameMode.CREATIVE)
                    e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), HopperManager.getVillagerHopper());
                HopperManager.removeVillagerHopper(e.getBlock().getLocation());
                e.getPlayer().sendMessage(Messages.get("break-hopper"));
                Utils.playFailSound(e.getPlayer());
            }
        }
    }
}
