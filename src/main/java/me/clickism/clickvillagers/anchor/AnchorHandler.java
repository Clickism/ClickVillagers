package me.clickism.clickvillagers.anchor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class AnchorHandler {
    
    private static final int ANCHOR_DURATION_THRESHOLD = Integer.MAX_VALUE / 4;
    
    public static boolean isAnchored(LivingEntity entity) {
        return entity.getStatusEffects().stream().anyMatch(AnchorHandler::isAnchorEffect);
    }
    
    public static void addAnchorEffect(LivingEntity entity) {
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, Integer.MAX_VALUE, 
                255, true, false, false));
    }
    
    public static void removeAnchorEffect(LivingEntity entity) {
        entity.removeStatusEffect(StatusEffects.SLOWNESS);
    }
    
    protected static boolean isAnchorEffect(StatusEffectInstance effect) {
        return effect.getEffectType().equals(StatusEffects.SLOWNESS) 
               && effect.getDuration() > ANCHOR_DURATION_THRESHOLD;
    }
}
