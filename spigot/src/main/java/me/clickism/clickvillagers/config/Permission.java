/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.config;

import me.clickism.clickvillagers.message.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum Permission {
    PICKUP,
    PLACE,
    HOPPER,
    ANCHOR,
    CLAIM,
    PARTNER,
    BYPASS_CLAIMS,
    BYPASS_LIMITS,
    RELOAD;

    private static final String PLUGIN_PREFIX = "clickvillagers";
    private final String permission;

    Permission() {
        String name = name().replace('_', '-').toLowerCase();
        permission = PLUGIN_PREFIX + "." + name;
    }

    public boolean has(CommandSender sender) {
        return sender.hasPermission(permission);
    }

    public boolean lacks(CommandSender sender) {
        return !has(sender);
    }

    public boolean lacksAndNotify(CommandSender sender) {
        if (lacks(sender)) {
            Message.NO_PERMISSION.send(sender);
            return true;
        }
        return false;
    }
}
