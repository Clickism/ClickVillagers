/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.listener;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class CooldownManager {

    private final long cooldownMillis;
    private final HashMap<UUID, Long> lastUseMap = new HashMap<>();

    public CooldownManager(long cooldownMillis) {
        this.cooldownMillis = cooldownMillis;
    }

    public boolean hasCooldown(Player player) {
        return getRemainingCooldownMillis(player) > 0;
    }

    public long getRemainingCooldownSeconds(Player player) {
        return getRemainingCooldownMillis(player) / 1000;
    }

    public long getRemainingCooldownMillis(Player player) {
        if (cooldownMillis <= 0) return 0;
        long time = System.currentTimeMillis();
        Long lastUse = lastUseMap.get(player.getUniqueId());
        if (lastUse == null) return 0;
        long passedMillis = time - lastUse;
        return cooldownMillis - passedMillis;
    }

    public void giveCooldown(Player player) {
        lastUseMap.put(player.getUniqueId(), System.currentTimeMillis());
    }
}
