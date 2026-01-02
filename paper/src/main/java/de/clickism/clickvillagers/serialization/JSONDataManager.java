/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * A config manager that saves and loads config in JSON format.
 */
public class JSONDataManager extends DataManager {
    /**
     * Gson instance.
     */
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private final boolean backupOnSave;
    private JsonObject root;

    /**
     * Create a new JSONDataManager.
     *
     * @param plugin    plugin
     * @param directory directory of the file
     * @param fileName  fileName, i.E: "config.json"
     * @throws IOException if an I/O error occurs
     */
    public JSONDataManager(JavaPlugin plugin, @NotNull File directory, String fileName, boolean backupOnSave) throws IOException {
        super(plugin, directory, fileName);
        this.backupOnSave = backupOnSave;
    }

    /**
     * Saves the given json object to the file.
     *
     * @param json the json object to save
     */
    public void save(JsonObject json) {
        if (backupOnSave) {
            backup();
        }
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(json, writer);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save file " + file.getPath() + ": " + e.getMessage());
        }
    }

    private void backup() {
        File backupFile = new File(file.getPath() + ".old");
        try {
            Files.copy(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to create backup file " + backupFile.getPath() + ": " + e.getMessage());
        }
    }

    @Override
    public void load() {
        try {
            JsonObject root = GSON.fromJson(new FileReader(file), JsonObject.class);
            if (root != null) {
                this.root = root;
            }
        } catch (FileNotFoundException e) {
            plugin.getLogger().warning("File not found: " + file.getPath());
        }
    }

    /**
     * Get the root json object.
     *
     * @return the root json object
     */
    @NotNull
    public JsonObject getRoot() {
        if (root == null) {
            root = new JsonObject();
        }
        return root;
    }
}
