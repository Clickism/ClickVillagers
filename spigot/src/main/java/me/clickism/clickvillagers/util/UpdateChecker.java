package me.clickism.clickvillagers.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {

    private static final String API_URL = "https://api.spigotmc.org/legacy/update.php?resource=";
    private final String resourceId;
    private final JavaPlugin plugin;

    public UpdateChecker(JavaPlugin plugin, String resourceId) {
        this.resourceId = resourceId;
        this.plugin = plugin;
    }

    public void checkVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (
                    InputStream is = new URL(API_URL + this.resourceId).openStream();
                    Scanner scanner = new Scanner(is)
            ) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException e) {
                plugin.getLogger().info("Unable to check for updates.");
            }
        });
    }
}
