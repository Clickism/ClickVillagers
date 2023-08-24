package me.clickism.clickvillagers.config;

import me.clickism.clickvillagers.ClickVillagers;
import me.clickism.clickvillagers.Utils;
import me.clickism.clickvillagers.managers.data.MessageManager;

public class Messages {

    static MessageManager messages;

    public static void initializeConfig() {
        messages = ClickVillagers.getMessageManager();
    }

    public static String get(String path) {
        return Utils.colorize((String) messages.getConfig().get("messages." + Settings.getLanguage() + "." + path));
    }
}
