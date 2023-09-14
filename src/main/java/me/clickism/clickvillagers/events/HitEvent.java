package me.clickism.clickvillagers.events;

import me.clickism.clickvillagers.config.Messages;
import me.clickism.clickvillagers.managers.VillagerData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class HitEvent implements Listener {

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && (e.getEntity() instanceof Villager || e.getEntity() instanceof ZombieVillager)) {
            Player player = (Player) e.getDamager();
            LivingEntity entity = (LivingEntity) e.getEntity();
//            if (((Player) e.getDamager()).isSneaking()) {
//                ClickEvent.setLastClickedVillager(player, entity);
//                if (!Settings.get("enable-claims")) {
//                    e.setCancelled(true);
//                    player.sendMessage(Messages.get("claims-disabled"));
//                    Utils.playFailSound(player);
//                    return;
//                }
//                if (VillagerData.isClaimed(entity)) {
//                    if (VillagerData.getOwner(entity).equals(player.getName()) || player.hasPermission("clickvillagers.bypass-claims")) {
//                        e.setCancelled(true);
//                        player.openInventory(EditVillagerMenu.get(entity));
//                        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, .3f, 1f);
//                    } else {
//                        player.sendMessage(Messages.get("belongs-to") + VillagerData.getOwner(entity));
//                        Utils.playFailSound(player);
//                    }
//                } else {
//                    e.setCancelled(true);
//                    player.openInventory(ClaimVillagerMenu.get(entity));
//                    player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, .3f, 1f);
//                }
//            } else if (VillagerData.isClaimed(entity)) {
//                if (VillagerData.getOwner(entity).equals(player.getName())) {
//                    e.setCancelled(true);
//                    player.sendMessage(Messages.get("shift-edit"));
//                } else {
//                    player.sendMessage(Messages.get("belongs-to") + VillagerData.getOwner(entity));
//                }
//            }
            if (VillagerData.isClaimed(entity)) {
                if (VillagerData.getOwner(entity).equals(player.getName())) {
                    e.setCancelled(true);
                    player.sendMessage(Messages.get("shift-edit"));
                } else {
                    player.sendMessage(Messages.get("belongs-to") + VillagerData.getOwner(entity));
                }
            }
        }
    }
}
