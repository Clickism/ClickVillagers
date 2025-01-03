package me.clickism.clickvillagers.message;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.clickism.clickvillagers.config.Setting;
import me.clickism.clickvillagers.serialization.JSONDataManager;
import me.clickism.clickvillagers.util.Utils;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessageManager {
    private static final boolean DEBUG_OVERRIDE_MESSAGES = false;

    private static final int VERSION = 1;

    private static final String DIRECTORY_NAME = "lang";
    private static final List<String> SUPPORTED_LANGUAGES = List.of(
            "en_US", "de_DE"
    );

    private final JavaPlugin plugin;
    private final JSONDataManager dataManager;

    public MessageManager(JavaPlugin plugin, String languageCode) throws IOException {
        // Save default languages
        this.plugin = plugin;
        File directory = new File(plugin.getDataFolder(), DIRECTORY_NAME);
        String fileName = languageCode + ".json";
        if (!new File(directory, fileName).exists() && !SUPPORTED_LANGUAGES.contains(languageCode)) {
            String defaultLanguage = SUPPORTED_LANGUAGES.get(0);
            plugin.getLogger().warning("Language \"" + languageCode + "\" not found. Reverting to \"" + defaultLanguage + "\".");
            Setting.LANGUAGE.set(defaultLanguage);
            Setting.saveSettings();
            fileName = defaultLanguage + ".json";
        }
        dataManager = new JSONDataManager(plugin, directory, fileName);
        checkAndUpdateLanguageFiles();
    }

    private void checkAndUpdateLanguageFiles() {
        int version = getVersion();
        boolean versionMismatch = version != VERSION || DEBUG_OVERRIDE_MESSAGES;
        for (String lang : SUPPORTED_LANGUAGES) {
            String fileName = DIRECTORY_NAME + File.separator + lang + ".json";
            boolean fileExists = new File(plugin.getDataFolder(), fileName).exists();
            if (!fileExists || versionMismatch) {
                // Override messages if new version
                plugin.saveResource(DIRECTORY_NAME + File.separator + lang + ".json", true);
                dataManager.load();
            }
            overwriteVersion();
        }
    }

    /**
     * @param path key of message
     * @return colorized string
     */
    @Nullable
    public String get(String path) {
        JsonObject root = dataManager.getRoot();
        if (!root.has(path)) {
            return null;
        }
        String message = dataManager.getRoot().get(path).getAsString();
        return Utils.colorize(message);
    }

    public List<String> getLore(String pathToButton) {
        JsonObject root = dataManager.getRoot();
        String pathToLore = pathToButton + ".lore";
        if (!root.has(pathToLore)) {
            return List.of();
        }
        JsonArray array = root.getAsJsonArray(pathToLore);
        List<String> lore = new ArrayList<>();
        for (JsonElement jsonElement : array) {
            lore.add(Utils.colorize(jsonElement.getAsString()));
        }
        return lore;
    }

    private int getVersion() {
        JsonObject root = dataManager.getRoot();
        if (!root.has("version")) {
            return 0;
        }
        return root.get("version").getAsInt();
    }

    private void overwriteVersion() {
        JsonObject root = dataManager.getRoot();
        root.addProperty("version", VERSION);
        dataManager.save(root);
    }
}
