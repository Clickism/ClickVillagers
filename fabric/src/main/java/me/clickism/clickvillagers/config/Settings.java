package me.clickism.clickvillagers.config;

import com.google.gson.JsonElement;
import me.clickism.clickvillagers.ClickVillagers;

import java.util.function.Function;

public enum Settings {
    CONFIG_VERSION(0),

    CLAIMED_DAMAGE(false),
    CLAIMED_IMMUNE_KILL_COMMAND(true),

    ENABLE_HOPPERS(true),

    IGNORE_BABY_VILLAGERS(true),
    IGNORE_CLAIMED_VILLAGERS(true),

    PARTNER_LIMIT_PER_PLAYER(10);

    private final Object defaultValue;
    private final String path;

    Settings(Object defaultValue) {
        this.defaultValue = defaultValue;
        this.path = name().toLowerCase().replace("_", "-");
    }

    public String getPath() {
        return path;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    private <T> T get(Function<JsonElement, T> getter) {
        T value = null;
        try {
            value = getter.apply(Config.INSTANCE.get(path));
        } catch (Exception ignored) {
            ClickVillagers.LOGGER.warn("Invalid value for \"{}\" in config.yml. Default value {} is used instead.", path, defaultValue);
            value = (T) defaultValue;
        }
        return value;
    }

    public int getInt() {
        return get(JsonElement::getAsInt);
    }

    public float getFloat() {
        return get(JsonElement::getAsFloat);
    }

    public double getDouble() {
        return get(JsonElement::getAsDouble);
    }

    public String getString() {
        return get(JsonElement::getAsString);
    }

    public boolean isEnabled() {
        return get(JsonElement::getAsBoolean);
    }

    public boolean isDisabled() {
        return !isEnabled();
    }
}
