package me.clickism.clickvillagers.villager;

import me.clickism.clickvillagers.listener.AutoRegistered;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AnchorManager implements Listener {

    private static final int ANCHOR_DURATION_THRESHOLD = Integer.MAX_VALUE / 4;

    @AutoRegistered
    public AnchorManager(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    private void onTransform(EntityTransformEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() != EntityType.VILLAGER && entity.getType() != EntityType.ZOMBIE_VILLAGER) return;
        Entity transformed = event.getTransformedEntity();
        if (transformed.getType() != EntityType.VILLAGER && transformed.getType() != EntityType.ZOMBIE_VILLAGER) return;
        if (isAnchored((LivingEntity) entity)) {
            addAnchorEffect((LivingEntity) transformed);
        }
    }

    public boolean isAnchored(LivingEntity entity) {
        return entity.getActivePotionEffects().stream().anyMatch(this::isAnchorEffect);
    }

    public void addAnchorEffect(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE,
                255, true, false, false));
    }

    public void removeAnchorEffect(LivingEntity entity) {
        entity.removePotionEffect(PotionEffectType.SLOW);
    }

    protected boolean isAnchorEffect(PotionEffect effect) {
        return effect.getType().equals(PotionEffectType.SLOW)
               && effect.getDuration() > ANCHOR_DURATION_THRESHOLD;
    }
}
