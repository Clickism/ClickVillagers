package me.clickism.clickvillagers.config;

import me.clickism.clickvillagers.ClickVillagers;
import me.clickism.clickvillagers.managers.data.ConfigManager;
import org.bukkit.configuration.MemorySection;

import java.util.Map;

public class Settings {

    static ConfigManager config;

    public static void initializeConfig() {
        config = ClickVillagers.getConfigManager();
        refreshConfig();
    }

    public static boolean get(String path) {
        return (boolean) config.getConfig().get(path);
    }
    public static String getLanguage() {
        return (String) config.getConfig().get("language");
    }
    public static int getInt(String path) {
        return (int) config.getConfig().get(path);
    }
    public static void refreshConfig() {
        Map<String, Object> values = config.getConfig().getValues(true);
        config.overrideConfig();
        config.reloadConfig();
        values.forEach((path, val) -> {
            if (!(val instanceof MemorySection)) config.getConfig().set(path, val);
        });
        config.saveConfig();
    }
}
