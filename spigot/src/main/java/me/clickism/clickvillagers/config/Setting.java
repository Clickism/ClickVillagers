/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.config;

import me.clickism.clickvillagers.ClickVillagers;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public enum Setting {
    CONFIG_VERSION(0),

    LANGUAGE("en_US"),

    CHECK_UPDATE(true),

    CLAIMED_DAMAGE(false),
    CLAIMED_IMMUNE_KILL_COMMAND(true),

    TICK_HOPPERS(true),
    HOPPER_TICK_RATE(20),
    HOPPER_LIMIT_PER_CHUNK(-1),

    IGNORE_BABY_VILLAGERS(true),
    IGNORE_CLAIMED_VILLAGERS(true),
    HOPPER_RECIPE(true),
    HOPPER_BLOCK_DISPLAY(true),
    HOPPER_BLOCK_DISPLAY_VIEW_RANGE(1.0f),

    PARTNER_LIMIT_PER_PLAYER(10),

    VALIDATE_PARTNER_NAMES(true),

    COOLDOWN(0),

    SHOW_TRADES(true),
    FORMAT_TRADES(true);

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

    public float getFloat() {
        return (float) getDouble();
    }

    public double getDouble() {
        return get(Double.class);
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

    public static void reloadSettings() throws IOException {
        JavaPlugin plugin = settingManager.getPlugin();
        settingManager = null;
        initialize(plugin);
    }

    public static int getCustomModelData(Villager.Profession profession, boolean baby, boolean zombie) {
        String key = getCustomModelDataKey(profession, baby, zombie);
        Object value = settingManager.get("custom-model-datas." + key);
        if (value instanceof Integer data) {
            return data;
        }
        return 0;
    }

    private static String getCustomModelDataKey(Villager.Profession profession, boolean baby, boolean zombie) {
        if (baby) {
            return "baby";
        }
        if (zombie) {
            return "zombie";
        }
        return profession.toString().toLowerCase();
    }
}
