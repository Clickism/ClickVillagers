package me.clickism.clickvillagers.events;

import me.clickism.clickvillagers.ClickVillagers;
import me.clickism.clickvillagers.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class InteractEvent implements Listener {

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof Villager) {
            if (e.getPlayer().isSneaking()) {
                if (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.SHEARS) {
                    //Anchored villager
                    e.setCancelled(true);
                    Villager villager = (Villager) e.getRightClicked();
                    if (villager.getPotionEffect(PotionEffectType.SLOW) == null) {
                        //Make anchored
                        villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 255, true, false));
//                        villager.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200, true, false));
                        e.getPlayer().sendMessage(ChatColor.GOLD + ">> " + ChatColor.GREEN + "You anchored the villager.");
                        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_BEEHIVE_SHEAR, 1f, 1f);
                    } else {
                        //Make moving
                        villager.removePotionEffect(PotionEffectType.SLOW);
//                        villager.removePotionEffect(PotionEffectType.JUMP);
                        e.getPlayer().sendMessage(ChatColor.GOLD + ">> " + ChatColor.RED + "You removed the villager's anchor.");
                        Utils.playFailSound(e.getPlayer());
                    }

                } else {
                    //Pick villager up
                    e.setCancelled(true);
                    ItemStack head = ClickVillagers.getVillagerHead((Villager) e.getRightClicked());
                    if (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) {
                        e.getPlayer().getInventory().setItem(e.getPlayer().getInventory().getHeldItemSlot(), head);
                    } else {
                        e.getPlayer().getInventory().addItem(head).forEach((i, item) -> {
                            e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation(), item);
                        });
                    }
                    e.getPlayer().sendMessage(ChatColor.GOLD + ">> " + ChatColor.GREEN + "You picked a villager up.");
                    Utils.playConfirmSound(e.getPlayer());
                }
            }
        }
    }
}
