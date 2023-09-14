package me.clickism.clickvillagers.events;

import me.clickism.clickvillagers.config.Settings;
import me.clickism.clickvillagers.managers.VillagerData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageEvent implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Villager || e.getEntity() instanceof ZombieVillager) {
            if (Settings.get("claimed-villagers-damage")) return;
            if (VillagerData.isClaimed((LivingEntity) e.getEntity())) {
                e.setCancelled(true);
            }
        }
    }
}
