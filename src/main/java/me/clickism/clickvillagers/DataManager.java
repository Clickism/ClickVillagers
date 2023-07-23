package me.clickism.clickvillagers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class DataManager {

    private final ClickVillagers plugin;
    private FileConfiguration dataConfig = null;
    private File configFile = null;

    public DataManager(ClickVillagers shop) {
        this.plugin = shop;
        saveDefaultConfig();
    }

    public void reloadConfig() {
        if (configFile == null)
            configFile = new File(plugin.getDataFolder(), "data.yml");

        this.dataConfig = YamlConfiguration.loadConfiguration(configFile);

        InputStream defaultStream = plugin.getResource("data.yml");

        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            dataConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getConfig() {
        if (dataConfig == null)
            reloadConfig();

        return dataConfig;
    }

    public void saveConfig() {
        if (dataConfig == null || configFile == null)
            return;

        try {
            getConfig().save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't save config to " + configFile, e);
        }
    }

    public void saveDefaultConfig() {
        if (configFile == null)
            configFile = new File(plugin.getDataFolder(), "data.yml");

        if (!configFile.exists()) {
            plugin.saveResource("data.yml", false);
        }
    }
}
