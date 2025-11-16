/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.serialization;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

/**
 * Simple YAML config manager.
 */
public class YAMLDataManager extends DataManager {

    private FileConfiguration config;

    /**
     * Creates a new YAMLDataManager instance.
     *
     * @param plugin    plugin
     * @param directory directory of the file
     * @param fileName  fileName, i.E: "config.yml"
     * @throws IOException if an I/O error occurs
     */
    public YAMLDataManager(JavaPlugin plugin, @NotNull File directory, String fileName) throws IOException {
        super(plugin, directory, fileName);
        saveDefaultConfig();
    }

    @Override
    public void load() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Saves the configuration to the file.
     */
    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException exception) {
            plugin.getLogger().log(Level.SEVERE, "\"" + file.getName() + "\" config couldn't be saved.", exception);
        }
    }

    private void saveDefaultConfig() throws IOException {
        String path = getTrimmedPath(plugin, file);
        saveResourceIfNotExists(path);
        config = YamlConfiguration.loadConfiguration(file);
        InputStream defaultStream = plugin.getResource(path);

        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            config.setDefaults(defaultConfig);
            saveConfig();
            defaultStream.close();
        }
    }

    private void saveResourceIfNotExists(String path) throws IOException {
        if (file.exists()) return;
        if (plugin.getResource(path) != null) {
            try {
                plugin.saveResource(path, false);
                return;
            } catch (Exception exception) {
                plugin.getLogger().log(Level.SEVERE, "Couldn't save default resource \"" + path + "\".", exception);
            }
        }
        file.createNewFile();
    }

    /**
     * Get the file configuration.
     * This is NOT guaranteed to return the same instance every time.
     *
     * @return file configuration
     */
    public FileConfiguration getConfig() {
        return config;
    }
}
