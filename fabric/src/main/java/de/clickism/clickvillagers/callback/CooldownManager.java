/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.callback;

import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Supplier;

public class CooldownManager {

    private final Supplier<Long> cooldownMillisSupplier;
    private final HashMap<UUID, Long> lastUseMap = new HashMap<>();

    public CooldownManager(Supplier<Long> cooldownMillisSupplier) {
        this.cooldownMillisSupplier = cooldownMillisSupplier;
    }

    public boolean hasCooldown(Player player) {
        return getRemainingCooldownMillis(player) > 0;
    }

    public long getRemainingCooldownSeconds(Player player) {
        return (long) Math.ceil((double) getRemainingCooldownMillis(player) / 1000);
    }

    public long getRemainingCooldownMillis(Player player) {
        long cooldownMillis = cooldownMillisSupplier.get();
        if (cooldownMillis <= 0) return 0;
        long time = System.currentTimeMillis();
        Long lastUse = lastUseMap.get(player.getUUID());
        if (lastUse == null) return 0;
        long passedMillis = time - lastUse;
        return cooldownMillis - passedMillis;
    }

    public void giveCooldown(Player player) {
        lastUseMap.put(player.getUUID(), System.currentTimeMillis());
    }
}
