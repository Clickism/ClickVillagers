package me.clickism.clickvillagers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.VillagerEntity;

public class AnchorHandler {
    
    public static boolean isAnchored(LivingEntity entity) {
        return entity.getStatusEffects().stream().anyMatch(AnchorHandler::isAnchorEffect);
    }
    
    public static void addAnchorEffect(LivingEntity entity) {
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, Integer.MAX_VALUE, 
                StatusEffectInstance.MAX_AMPLIFIER, true, false, false));
    }
    
    public static void removeAnchorEffect(LivingEntity entity) {
        entity.removeStatusEffect(StatusEffects.SLOWNESS);
    }
    
    protected static boolean isAnchorEffect(StatusEffectInstance effect) {
        return effect.getEffectType().equals(StatusEffects.SLOWNESS) 
               && effect.getAmplifier() == StatusEffectInstance.MAX_AMPLIFIER;
    }
}
