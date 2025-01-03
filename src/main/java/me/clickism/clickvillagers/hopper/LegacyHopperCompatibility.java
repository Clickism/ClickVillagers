package me.clickism.clickvillagers.hopper;

import me.clickism.clickvillagers.ClickVillagers;
import me.clickism.clickvillagers.serialization.YAMLDataManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Hopper;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LegacyHopperCompatibility {

    private static final String LOG_PREFIX = "[LegacyCompatibility] ";
    
    public static void startConversionIfLegacy(JavaPlugin plugin) {
        File legacyDataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!legacyDataFile.exists()) return;
        YAMLDataManager dataManager;
        try {
            dataManager = new YAMLDataManager(plugin, plugin.getDataFolder(), "data.yml");
        } catch (Exception exception) {
            ClickVillagers.LOGGER.log(Level.WARNING, LOG_PREFIX + "Failed to load legacy data file.", exception);
            return;
        }
        convertHoppers(plugin, loadMap(dataManager));
        try {
            legacyDataFile.delete();
            ClickVillagers.LOGGER.log(Level.INFO, LOG_PREFIX + "Deleted legacy data file.");
        } catch (Exception exception) {
            ClickVillagers.LOGGER.log(Level.WARNING, LOG_PREFIX + "Failed to delete legacy data file.", exception);
        }
    }

    private static void convertHoppers(JavaPlugin plugin, Map<Location, UUID> hopperMap) {
        Logger logger = plugin.getLogger();
        logger.info(LOG_PREFIX + "Converting legacy villager hoppers...");
        hopperMap.forEach(LegacyHopperCompatibility::convertHopper);
        hopperMap.clear();
        logger.info(LOG_PREFIX + "Legacy villager hopper conversion complete.");
    }

    private static void convertHopper(Location location, UUID displayUUID) {
        try {
            Hopper hopper = (Hopper) location.getBlock().getState(); 
            HopperManager.markHopper(hopper, displayUUID);
            ClickVillagers.LOGGER.info(LOG_PREFIX + "Converted legacy villager hopper at: " + formatLocation(location));
        } catch (Exception exception) {
            ClickVillagers.LOGGER.warning(LOG_PREFIX + "Failed to convert legacy villager hopper at: " + formatLocation(location));
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<Location, UUID> loadMap(YAMLDataManager dataManager) {
        Map<Location, UUID> hopperMap = new HashMap<>();
        List<Location> hopperLocations = (List<Location>) dataManager.getConfig().get("hoppers_list");
        if (hopperLocations == null) return hopperMap;
        hopperLocations.forEach(location -> {
            String uuidString = dataManager.getConfig().getString("villager_hoppers." + location + ".display");
            if (uuidString == null) return;
            UUID uuid = UUID.fromString(uuidString);
            hopperMap.put(location, uuid);
        });
        return hopperMap;
    }

    private static String formatLocation(Location location) {
        World world = location.getWorld();
        String worldName = world != null ? world.getName() : "null";
        return worldName + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }
}
