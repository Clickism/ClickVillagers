/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.clickism.clickvillagers.ClickVillagers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Config {

    public static Config INSTANCE;

    public static int CONFIG_VERSION = 3;

    private static final File CONFIG_DIRECTORY = new File("config");
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private final File file;
    private JsonObject root = new JsonObject();

    public Config(String fileName) throws IOException {
        if (INSTANCE != null) {
            throw new IllegalStateException("Config already exists");
        }
        if (!CONFIG_DIRECTORY.exists()) {
            CONFIG_DIRECTORY.mkdirs();
        }
        this.file = new File(CONFIG_DIRECTORY, fileName);
        INSTANCE = this;
        if (!file.createNewFile()) {
            load();
            if (Settings.CONFIG_VERSION.getInt() != CONFIG_VERSION) {
                injectDefaultValues();
            }
            return;
        }
        injectDefaultValues();
    }

    private void injectDefaultValues() {
        for (Settings setting : Settings.values()) {
            if (root.has(setting.getPath())) continue;
            root.add(setting.getPath(), GSON.toJsonTree(setting.getDefaultValue()));
        }
        set(Settings.CONFIG_VERSION, CONFIG_VERSION);
        save();
    }

    public void set(Settings setting, Object value) {
        set(setting.getPath(), value);
    }

    public void set(String path, Object value) {
        root.add(path, GSON.toJsonTree(value));
    }

    public JsonElement get(Settings setting) {
        return get(setting.getPath());
    }

    public JsonElement get(String path) {
        return root.get(path);
    }

    private void load() {
        try (FileReader reader = new FileReader(file)) {
            root = GSON.fromJson(reader, JsonObject.class);
            if (root == null) {
                root = new JsonObject();
            }
        } catch (IOException e) {
            ClickVillagers.LOGGER.info("Failed to load file: {}", file.getPath());
        }
    }

    private void save() {
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(root, writer);
        } catch (IOException e) {
            ClickVillagers.LOGGER.info("Failed to save file: {}", file.getPath());
        }
    }
}
