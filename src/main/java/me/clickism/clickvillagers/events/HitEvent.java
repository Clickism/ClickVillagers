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
