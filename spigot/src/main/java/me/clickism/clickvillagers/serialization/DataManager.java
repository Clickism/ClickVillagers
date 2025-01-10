package me.clickism.clickvillagers.serialization;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * DataManager interface.
 */
public abstract class DataManager {
    /**
     * The plugin used.
     */
    protected final JavaPlugin plugin;
    /**
     * The file used to save config.
     */
    protected final File file;

    /**
     * Create a new DataManager instance.
     *
     * @param plugin    the plugin
     * @param directory the directory of the file
     * @param fileName  the file name, i.E: "config.yml"
     * @throws IOException if an I/O error occurs
     */
    public DataManager(JavaPlugin plugin, @NotNull File directory, String fileName) throws IOException {
        this.plugin = plugin;
        createDirectoryIfNotExists(directory);
        file = new File(directory, fileName);

        if (file.exists()) {
            load();
        }
    }

    /**
     * Loads the configuration from the file.
     */
    public abstract void load();

    /**
     * Get the trimmed path of a file relative to the plugin's config folder.
     * i.E: "plugins/MyPlugin/config/config.yml" -> "config/config.yml"
     *
     * @param plugin the plugin
     * @param file   the file
     * @return the trimmed path
     */
    protected static String getTrimmedPath(JavaPlugin plugin, File file) {
        String dataFolderPath = plugin.getDataFolder().getPath();
        String filePath = file.getPath();
        if (filePath.startsWith(dataFolderPath)) {
            return filePath.substring(dataFolderPath.length() + 1); //Trim plugin directory
        }
        return filePath;
    }

    /**
     * Create a directory if it does not exist.
     *
     * @param directory the directory to create
     * @throws IOException if the directory could not be created
     */
    protected static void createDirectoryIfNotExists(File directory) throws IOException {
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Failed to create directory " + directory.getPath());
        }
    }
}
