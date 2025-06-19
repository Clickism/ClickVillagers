/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers;

import de.clickism.clickvillagers.callback.*;
import de.clickism.clickvillagers.config.Config;
import de.clickism.clickvillagers.config.Settings;
import de.clickism.clickvillagers.util.UpdateChecker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.MinecraftVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ClickVillagers implements ModInitializer {
    public static final String MOD_ID = "clickvillagers";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static String newerVersion = null;

    @Override
    public void onInitialize() {
        try {
            new Config("ClickVillagers.json");
        } catch (IOException e) {
            LOGGER.error("Failed to load config file", e);
        }
        CooldownManager cooldownManager = new CooldownManager(Settings.COOLDOWN::getLong);
        UseEntityCallback.EVENT.register(new VillagerUseEntityCallback(cooldownManager));
        UseEntityCallback.EVENT.register(new VehicleUseEntityCallback());
        UseBlockCallback.EVENT.register(new VillagerUseBlockCallback());
        if (Settings.CHECK_UPDATE.isEnabled()) {
            checkUpdates();
            ServerPlayConnectionEvents.JOIN.register(new UpdateNotifier(() -> newerVersion));
        }
    }

    private void checkUpdates() {
        String modVersion = FabricLoader.getInstance().getModContainer(MOD_ID)
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse(null);
        //? if >= 1.21.6 {
        String minecraftVersion = MinecraftVersion.CURRENT.name();
        //?} else
        /*String minecraftVersion = MinecraftVersion.CURRENT.getName();*/
        new UpdateChecker(MOD_ID, "fabric", minecraftVersion).checkVersion(version -> {
            if (modVersion == null || UpdateChecker.getRawVersion(modVersion).equals(version)) {
                return;
            }
            newerVersion = version;
            LOGGER.info("Newer version available: {}", version);
        });
    }
}