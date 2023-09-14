package me.clickism.clickvillagers.config;

import me.clickism.clickvillagers.ClickVillagers;
import me.clickism.clickvillagers.Utils;
import me.clickism.clickvillagers.managers.data.MessageManager;
import org.bukkit.configuration.MemorySection;

import java.util.Map;

public class Messages {

    static MessageManager messages;

    public static void initializeConfig() {
        messages = ClickVillagers.getMessageManager();
        refreshConfig();
    }

    public static String get(String path) {
        return Utils.colorize((String) messages.getConfig().get("messages." + Settings.getLanguage() + "." + path));
    }

    public static void refreshConfig() {
        Map<String, Object> values = messages.getConfig().getValues(true);
        messages.overrideConfig();
        messages.reloadConfig();
        values.forEach((path, val) -> {
            if (!(val instanceof MemorySection)) messages.getConfig().set(path, val);
        });
        messages.saveConfig();
    }
}
