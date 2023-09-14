package me.clickism.clickvillagers.managers.data;

import me.clickism.clickvillagers.ClickVillagers;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class MessageManager {

    private final ClickVillagers plugin;
    private FileConfiguration dataConfig = null;
    private File messageFile = null;

    public MessageManager(ClickVillagers clickVillagers) {
        this.plugin = clickVillagers;
        saveDefaultConfig();
    }

    public void reloadConfig() {
        if (messageFile == null) {
            messageFile = new File(plugin.getDataFolder(), "messages.yml");
        }

        this.dataConfig = YamlConfiguration.loadConfiguration(messageFile);

        InputStream defaultStream = plugin.getResource("messages.yml");

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
        if (dataConfig == null || messageFile == null)
            return;

        try {
            getConfig().save(messageFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't save config to " + messageFile, e);
        }
    }

    public void saveDefaultConfig() {
        if (messageFile == null)
            messageFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!messageFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
    }

    public void overrideConfig() {
        messageFile.delete();
        saveDefaultConfig();
    }
}
