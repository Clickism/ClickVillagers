/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.anchor;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class AnchorHandler {
    
    private static final int ANCHOR_DURATION_THRESHOLD = Integer.MAX_VALUE / 4;
    
    public static boolean isAnchored(LivingEntity entity) {
        return entity.getActiveEffects().stream().anyMatch(AnchorHandler::isAnchorEffect);
    }
    
    public static void addAnchorEffect(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, Integer.MAX_VALUE,
                255, true, false, false));
    }
    
    public static void removeAnchorEffect(LivingEntity entity) {
        entity.removeEffect(MobEffects.SLOWNESS);
    }
    
    protected static boolean isAnchorEffect(MobEffectInstance effect) {
        return effect.getEffect().equals(MobEffects.SLOWNESS)
               && effect.getDuration() > ANCHOR_DURATION_THRESHOLD;
    }
}
