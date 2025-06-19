/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.legacy;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class LegacyMessagesCompatibility {

    private static final String LOG_PREFIX = "[LegacyCompatibility] ";

    public static void removeLegacyMessageFile(JavaPlugin plugin) {
        try {
            File file = new File(plugin.getDataFolder(), "messages.yml");
            if (!file.exists()) return;
            if (!file.delete()) {
                throw new Exception("Couldn't delete file.");
            }
            plugin.getLogger().info(LOG_PREFIX + "Removed legacy messages.yml file.");
        } catch (Exception exception) {
            plugin.getLogger().warning(LOG_PREFIX + "Failed to remove legacy messages.yml file: " + exception.getMessage());
        }
    }
}
