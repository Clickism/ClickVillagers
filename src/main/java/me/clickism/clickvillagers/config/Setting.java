package me.clickism.clickvillagers.config;

import me.clickism.clickvillagers.ClickVillagers;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public enum Setting {
    CONFIG_VERSION(0),

    LANGUAGE("en_US"),

    CHECK_UPDATE(true),

    CLAIMED_VILLAGERS_TAKE_DAMAGE(false),
    CLAIMED_VILLAGERS_IMMUNE_KILL_COMMAND(true),

    TICK_VILLAGER_HOPPERS(true),
    VILLAGER_HOPPER_TICK_RATE(20),
    VILLAGER_HOPPER_LIMIT_PER_CHUNK(-1),

    IGNORE_BABY_VILLAGERS(true),
    IGNORE_CLAIMED_VILLAGERS(true),
    VILLAGER_HOPPER_RECIPE(true),
    VILLAGER_HOPPER_BLOCK_DISPLAY(true),

    PARTNER_LIMIT_PER_PLAYER(10),

    VALIDATE_PARTNER_NAMES(true);

    private static SettingManager settingManager;

    private final Object defaultValue;
    private final String path;

    Setting(Object defaultValue) {
        this.defaultValue = defaultValue;
        this.path = name().toLowerCase().replace("_", "-");
    }

    public void set(Object object) {
        if (settingManager == null) {
            ClickVillagers.LOGGER.warning("Couldn't save setting " + name() + " to config. SettingManager is null.");
            return;
        }
        settingManager.set(path, object);
    }

    private <T> T get(Class<T> type) {
        Object value = null;
        if (settingManager != null) {
            value = settingManager.get(path);
        }
        if (!type.isInstance(value)) {
            ClickVillagers.LOGGER.warning("Invalid value for \"" + path + "\" in config.yml. Default value " +
                                          defaultValue + " is used instead.");
            value = defaultValue;
        }
        return type.cast(value);
    }

    public int getInt() {
        return get(Integer.class);
    }

    public String getString() {
        return get(String.class);
    }

    public boolean isEnabled() {
        return get(Boolean.class);
    }

    public boolean isDisabled() {
        return !isEnabled();
    }

    public static void initialize(JavaPlugin plugin) throws IOException {
        if (settingManager != null) return;
        settingManager = new SettingManager(plugin);
    }

    public static void saveSettings() {
        if (settingManager == null) {
            ClickVillagers.LOGGER.warning("Couldn't save settings config. SettingManager is null.");
            return;
        }
        settingManager.getDataManager().saveConfig();
    }
}
