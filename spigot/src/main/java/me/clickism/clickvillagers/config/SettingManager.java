/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.config;

import me.clickism.clickvillagers.serialization.YAMLDataManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Set;

public class SettingManager {

    private static final int VERSION = 2;
    private static final boolean DEBUG_OVERRIDE_SETTINGS = false;

    private static final String FILE_NAME = "config.yml";

    private final JavaPlugin plugin;
    private final YAMLDataManager dataManager;

    public SettingManager(JavaPlugin plugin) throws IOException {
        this.plugin = plugin;
        dataManager = new YAMLDataManager(plugin, plugin.getDataFolder(), FILE_NAME);
        Object version = get("config-version");
        if (version == null || (int) version != VERSION || DEBUG_OVERRIDE_SETTINGS) {
            // Replace config and inject old values
            injectValues();
        }
    }

    public YAMLDataManager getDataManager() {
        return dataManager;
    }

    @Nullable
    public Object get(String path) {
        return dataManager.getConfig().get(path);
    }

    public void set(String path, Object object) {
        dataManager.getConfig().set(path, object);
    }

    public void injectValues() {
        plugin.saveResource(FILE_NAME, true);
        FileConfiguration oldConfig = dataManager.getConfig();
        dataManager.load(); // Reload config
        Set<String> keys = dataManager.getConfig().getKeys(true);
        keys.forEach(key -> {
            Object value = oldConfig.get(key);
            if (value != null) {
                dataManager.getConfig().set(key, value);
            }
        });
        // Override version
        dataManager.getConfig().set("config-version", VERSION);
        dataManager.saveConfig();
    }
}
