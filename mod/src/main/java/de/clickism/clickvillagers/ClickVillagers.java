/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers;

import de.clickism.modrinthupdatechecker.ModrinthUpdateChecker;
import net.minecraft.DetectedVersion;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.clickism.clickvillagers.ClickVillagersConfig.*;

public class ClickVillagers {
    public static final String MOD_ID = "clickvillagers";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static String newerVersion = null;

    public static void initialize() {
        // Load config after events to ensure listeners are registered
        CONFIG.load();
    }

    public static void checkUpdates(String modVersion, String loader) {
        //? if >=1.21.9 {
        String minecraftVersion = DetectedVersion.tryDetectVersion().name();
        //?} elif >= 1.21.6 {
        /*String minecraftVersion = DetectedVersion.BUILT_IN.name();
         *///?} else
        //String minecraftVersion = DetectedVersion.BUILT_IN.getName();
        new ModrinthUpdateChecker(MOD_ID, loader, minecraftVersion).checkVersion(version -> {
            if (modVersion == null || ModrinthUpdateChecker.getRawVersion(modVersion).equals(version)) {
                return;
            }
            newerVersion = version;
            LOGGER.info("Newer version available: {}", version);
        });
    }

    public static @Nullable String newerVersion() {
        return newerVersion;
    }
}