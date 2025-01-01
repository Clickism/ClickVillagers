package me.clickism.clickvillagers.config;

import me.clickism.clickvillagers.message.Message;
import org.bukkit.entity.Player;

public enum Permission {
    PICKUP,
    HOPPER,
    ANCHOR,
    CLAIM,
    PARTNER,
    BYPASS_CLAIMS,
    BYPASS_LIMITS;

    private static final String PLUGIN_PREFIX = "clickvillagers";
    private final String permission;

    Permission() {
        String name = name().replace('_', '-').toLowerCase();
        permission = PLUGIN_PREFIX + "." + name;
    }

    public boolean has(Player player) {
        return player.hasPermission(permission);
    }

    public boolean lacks(Player player) {
        return !has(player);
    }

    public boolean lacksAndNotify(Player player) {
        if (lacks(player)) {
            Message.NO_PERMISSION.send(player);
            return true;
        }
        return false;
    }
}
