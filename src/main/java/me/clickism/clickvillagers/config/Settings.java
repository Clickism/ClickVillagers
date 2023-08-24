package me.clickism.clickvillagers.config;

import me.clickism.clickvillagers.ClickVillagers;
import me.clickism.clickvillagers.managers.data.ConfigManager;

public class Settings {

    static ConfigManager config;

    public static void initializeConfig() {
        config = ClickVillagers.getConfigManager();
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
}
