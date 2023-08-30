package me.clickism.clickvillagers.events;

import me.clickism.clickvillagers.Utils;
import me.clickism.clickvillagers.config.Messages;
import me.clickism.clickvillagers.config.Settings;
import me.clickism.clickvillagers.managers.VillagerManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class InteractEvent implements Listener {

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        if (e.getHand().equals(EquipmentSlot.OFF_HAND)) return;
        if (e.getRightClicked() instanceof Villager || e.getRightClicked() instanceof ZombieVillager) {
            if (e.getPlayer().isSneaking()) {
                if (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.SHEARS) {
                    //Anchored villager
                    e.setCancelled(true);
                    if (!Settings.get("enable-anchor")) {
                        e.getPlayer().sendMessage(Messages.get("anchor-disabled"));
                        Utils.playFailSound(e.getPlayer());
                        return;
                    }
                    if (!e.getPlayer().hasPermission("clickvillagers.anchor")) {
                        e.getPlayer().sendMessage(Messages.get("no-permission"));
                        Utils.playFailSound(e.getPlayer());
                        return;
                    }
                    LivingEntity entity = (LivingEntity) e.getRightClicked();
                    if (entity.getPotionEffect(PotionEffectType.SLOW) == null) {
                        //Anchor villager
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 255, true, false));
                        e.getPlayer().sendMessage(Messages.get("add-anchor"));
                        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_BEEHIVE_SHEAR, 1f, 1f);
                    } else {
                        //Remove anchor
                        entity.removePotionEffect(PotionEffectType.SLOW);
                        e.getPlayer().sendMessage(Messages.get("remove-anchor"));
                        Utils.playFailSound(e.getPlayer());
                    }

                } else {
                    //Pick villager up
                    e.setCancelled(true);
                    if (!e.getPlayer().hasPermission("clickvillagers.pickup")) {
                        e.getPlayer().sendMessage(Messages.get("no-permission"));
                        Utils.playFailSound(e.getPlayer());
                        return;
                    }
                    ItemStack head = VillagerManager.turnVillagerIntoHead((LivingEntity) e.getRightClicked());
                    if (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) {
                        e.getPlayer().getInventory().setItem(e.getPlayer().getInventory().getHeldItemSlot(), head);
                    } else {
                        e.getPlayer().getInventory().addItem(head).forEach((i, item) -> {
                            e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation(), item);
                        });
                    }
                    e.getPlayer().sendMessage(Messages.get("picked-villager"));
                    Utils.playConfirmSound(e.getPlayer());
                }
            }
        } else if (e.getRightClicked() instanceof RideableMinecart || e.getRightClicked() instanceof Boat) {
            // Put villagers into Minecarts/Boats
            if (VillagerManager.isVillagerHead(e.getPlayer().getInventory().getItemInMainHand())) {
                e.setCancelled(true);
                if (!e.getPlayer().hasPermission("clickvillagers.pickup")) {
                    e.getPlayer().sendMessage(Messages.get("no-permission"));
                    Utils.playFailSound(e.getPlayer());
                    return;
                }
                LivingEntity villager = VillagerManager.getVillagerFromHead(e.getPlayer().getInventory().getItemInMainHand());
                if (villager != null) {
                    villager.teleport(e.getRightClicked().getLocation());
                    e.getRightClicked().addPassenger(villager);
                    e.getPlayer().getInventory().removeItem(e.getPlayer().getInventory().getItemInMainHand());
                } else {
                    e.getPlayer().sendMessage(Messages.get("no-data"));
                    Utils.playFailSound(e.getPlayer());
                }
            }
        }
    }
}
