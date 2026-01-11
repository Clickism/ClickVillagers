/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers;

import de.clickism.clickvillagers.callback.*;
import de.clickism.clickvillagers.util.MessageType;
import de.clickism.clickvillagers.util.VersionHelper;
import de.clickism.configured.fabriccommandadapter.FabricCommandAdapter;
import de.clickism.configured.fabriccommandadapter.command.GetCommand;
import de.clickism.configured.fabriccommandadapter.command.PathCommand;
import de.clickism.configured.fabriccommandadapter.command.ReloadCommand;
import de.clickism.configured.fabriccommandadapter.command.SetCommand;
import de.clickism.modrinthupdatechecker.ModrinthUpdateChecker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.DetectedVersion;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.clickism.clickvillagers.ClickVillagersConfig.*;

public class ClickVillagers implements ModInitializer {
    public static final String MOD_ID = "clickvillagers";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static String newerVersion = null;

    @Override
    public void onInitialize() {
        CONFIG.load();
        CooldownManager cooldownManager = new CooldownManager(() -> COOLDOWN.get());
        UseEntityCallback.EVENT.register(new UseVillagerEntityCallback(cooldownManager));
        UseEntityCallback.EVENT.register(new UseVehicleEntityCallback());
        UseBlockCallback.EVENT.register(new UseVillagerBlockCallback());
        if (CHECK_UPDATES.get()) {
            checkUpdates();
            ServerPlayConnectionEvents.JOIN.register(new UpdateNotifier(() -> newerVersion));
        }

        // Register Commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("clickvillagers")
                    .requires(VersionHelper::isOp)
                    .then(FabricCommandAdapter.ofConfig(CONFIG)
                            .add(new SetCommand((sender, key, value) -> {
                                MessageType.CONFIG.send(sender, Component.literal("§aConfig option \"§l" + key + "§a\" set to §l" + value + "."));
                            }))
                            .add(new GetCommand((sender, key, value) -> {
                                MessageType.CONFIG.send(sender, Component.literal("§aConfig option \"§l" + key + "§a\" has value §l" + value + "."));
                            }))
                            .add(new ReloadCommand(sender -> {
                                MessageType.CONFIG.send(sender, Component.literal("§aReloaded the config file."));
                            }))
                            .add(new PathCommand((sender, path) -> {
                                MessageType.CONFIG.send(sender, Component.literal("§aThe config file is located at: §f" + path));
                            }))
                            .buildRoot()
                    )
            );
        });
    }

    private void checkUpdates() {
        String modVersion = FabricLoader.getInstance().getModContainer(MOD_ID)
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse(null);
        //? if >=1.21.9 {
        String minecraftVersion = DetectedVersion.tryDetectVersion().name();
        //?} elif >= 1.21.6 {
        /*String minecraftVersion = DetectedVersion.BUILT_IN.name();
         *///?} else
        //String minecraftVersion = DetectedVersion.BUILT_IN.getName();
        new ModrinthUpdateChecker(MOD_ID, "fabric", minecraftVersion).checkVersion(version -> {
            if (modVersion == null || ModrinthUpdateChecker.getRawVersion(modVersion).equals(version)) {
                return;
            }
            newerVersion = version;
            LOGGER.info("Newer version available: {}", version);
        });
    }
}