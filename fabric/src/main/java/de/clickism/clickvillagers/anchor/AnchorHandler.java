/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.anchor;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class AnchorHandler {
    
    private static final int ANCHOR_DURATION_THRESHOLD = Integer.MAX_VALUE / 4;

    //? if >=1.21.5 {
    private static final Holder<MobEffect> SLOWNESS = MobEffects.SLOWNESS;
    //?} else
    //private static final Holder<MobEffect> SLOWNESS = MobEffects.MOVEMENT_SLOWDOWN;
    
    public static boolean isAnchored(LivingEntity entity) {
        return entity.getActiveEffects().stream().anyMatch(AnchorHandler::isAnchorEffect);
    }
    
    public static void addAnchorEffect(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(SLOWNESS, Integer.MAX_VALUE,
                255, true, false, false));
    }
    
    public static void removeAnchorEffect(LivingEntity entity) {
        entity.removeEffect(SLOWNESS);
    }
    
    protected static boolean isAnchorEffect(MobEffectInstance effect) {
        return effect.getEffect().equals(SLOWNESS)
               && effect.getDuration() > ANCHOR_DURATION_THRESHOLD;
    }
}
