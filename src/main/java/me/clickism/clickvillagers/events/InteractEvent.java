package me.clickism.clickvillagers.events;

import me.clickism.clickvillagers.Utils;
import me.clickism.clickvillagers.config.Messages;
import me.clickism.clickvillagers.config.Settings;
import me.clickism.clickvillagers.managers.VillagerData;
import me.clickism.clickvillagers.managers.VillagerManager;
import me.clickism.clickvillagers.menu.ClaimVillagerMenu;
import me.clickism.clickvillagers.menu.EditVillagerMenu;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
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
            LivingEntity entity = (LivingEntity) e.getRightClicked();
            Player player = e.getPlayer();
            if (player.isSneaking()) {
                if (player.getInventory().getItemInMainHand().getType() == Material.SHEARS) {
                    //Anchored villager
                    e.setCancelled(true);
                    if (!Settings.get("enable-anchor")) {
                        player.sendMessage(Messages.get("anchor-disabled"));
                        Utils.playFailSound(player);
                        return;
                    }
                    if (!player.hasPermission("clickvillagers.anchor")) {
                        player.sendMessage(Messages.get("no-permission"));
                        Utils.playFailSound(player);
                        return;
                    }
                    if (VillagerData.isClaimed(entity)) {
                        if (!VillagerData.getOwner(entity).equals(e.getPlayer().getName()) && !e.getPlayer().hasPermission("clickvillagers.bypass-claims")) {
                            player.sendMessage(Messages.get("belongs-to") + VillagerData.getOwner(entity));
                            Utils.playFailSound(player);
                            return;
                        }
                    }
                    if (entity.getPotionEffect(PotionEffectType.SLOW) == null) {
                        //Anchor villager
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 255, true, false));
                        player.sendMessage(Messages.get("add-anchor"));
                        player.playSound(e.getPlayer().getLocation(), Sound.BLOCK_BEEHIVE_SHEAR, 1f, 1f);
                    } else {
                        //Remove anchor
                        entity.removePotionEffect(PotionEffectType.SLOW);
                        player.sendMessage(Messages.get("remove-anchor"));
                        Utils.playFailSound(player);
                    }

                } else if (Tag.ITEMS_SHOVELS.getValues().contains(e.getPlayer().getInventory().getItemInMainHand().getType())) {
                    //Claim villager
                    ClickEvent.setLastClickedVillager(player, entity);
                    if (!Settings.get("enable-claims")) {
                        e.setCancelled(true);
                        player.sendMessage(Messages.get("claims-disabled"));
                        Utils.playFailSound(player);
                        return;
                    }
                    if (VillagerData.isClaimed(entity)) {
                        if (VillagerData.getOwner(entity).equals(player.getName()) || player.hasPermission("clickvillagers.bypass-claims")) {
                            //Edit with bypass or shovel
                            e.setCancelled(true);
                            ClickEvent.setLastClickedVillager(player, entity);
                            player.openInventory(EditVillagerMenu.get(entity));
                            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, .3f, 1f);
                        }
                    } else {
                        e.setCancelled(true);
                        ClickEvent.setLastClickedVillager(player, entity);
                        player.openInventory(ClaimVillagerMenu.get());
                        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, .3f, 1f);
                    }
                } else if (VillagerData.isClaimed(entity)) {
                    // Open right click edit
                    e.setCancelled(true);
                    if (VillagerData.getOwner(entity).equals(player.getName())) {
                        ClickEvent.setLastClickedVillager(player, entity);
                        player.openInventory(EditVillagerMenu.get(entity));
                        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, .3f, 1f);
                    } else {
                        player.sendMessage(Messages.get("belongs-to") + VillagerData.getOwner(entity));
                        Utils.playFailSound(player);
                    }
                } else {
                    //Pick villager up
                    e.setCancelled(true);
                    if (!player.hasPermission("clickvillagers.pickup")) {
                        player.sendMessage(Messages.get("no-permission"));
                        Utils.playFailSound(player);
                        return;
                    }
                    if (VillagerData.isClaimed(entity)) {
                        if (!VillagerData.getOwner(entity).equals(player.getName()) && !player.hasPermission("clickvillagers.bypass-claims")) {
                            player.sendMessage(Messages.get("belongs-to") + VillagerData.getOwner(entity));
                            Utils.playFailSound(player);
                            return;
                        }
                    }
                    ItemStack head = VillagerManager.turnVillagerIntoHead(entity);
                    if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                        player.getInventory().setItem(e.getPlayer().getInventory().getHeldItemSlot(), head);
                    } else {
                        player.getInventory().addItem(head).forEach((i, item) -> {
                            player.getWorld().dropItem(player.getLocation(), item);
                        });
                    }
                    player.sendMessage(Messages.get("picked-villager"));
                    Utils.playConfirmSound(player);
                }
            } else {
                //Check if tradable
                if (VillagerData.isClaimed(entity)) {
                    if (!VillagerData.getOwner(entity).equals(player.getName()) && !player.hasPermission("clickvillagers.bypass-claims")) {
                        if (!VillagerData.isTradable(entity)) {
                            e.setCancelled(true);
                            player.sendMessage(Messages.get("belongs-to") + VillagerData.getOwner(entity));
                            Utils.playFailSound(player);
                        }
                    }
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
                if (VillagerData.isClaimed(e.getPlayer().getInventory().getItemInMainHand())) {
                    if (!VillagerData.getOwner(e.getPlayer().getInventory().getItemInMainHand()).equals(e.getPlayer().getName()) && !e.getPlayer().hasPermission("clickvillagers.bypass-claims")) {
                        e.getPlayer().sendMessage(Messages.get("belongs-to") + VillagerData.getOwner(e.getPlayer().getInventory().getItemInMainHand()));
                        Utils.playFailSound(e.getPlayer());
                        return;
                    }
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
