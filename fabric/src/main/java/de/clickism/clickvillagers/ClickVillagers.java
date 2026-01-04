/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers;

import de.clickism.clickvillagers.callback.*;
import de.clickism.clickvillagers.message.MessageTypes;
import de.clickism.clickvillagers.util.VersionHelper;
import de.clickism.configured.fabriccommandadapter.FabricCommandAdapter;
import de.clickism.configured.fabriccommandadapter.command.GetCommand;
import de.clickism.configured.fabriccommandadapter.command.PathCommand;
import de.clickism.configured.fabriccommandadapter.command.ReloadCommand;
import de.clickism.configured.fabriccommandadapter.command.SetCommand;
import de.clickism.linen.core.Linen;
import de.clickism.modrinthupdatechecker.ModrinthUpdateChecker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.MinecraftVersion;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.clickism.clickvillagers.ClickVillagersConfig.*;

public class ClickVillagers implements ModInitializer {
    public static final String MOD_ID = "clickvillagers";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static String newerVersion = null;

    @Override
    public void onInitialize() {
        // Linen
        Linen.initialize();
        // Load Config
        CONFIG.load();
        CooldownManager cooldownManager = new CooldownManager(COOLDOWN::get);
        // Register Callbacks
        UseEntityCallback.EVENT.register(new UseVillagerEntityCallback(cooldownManager));
        UseEntityCallback.EVENT.register(new UseVehicleEntityCallback());
        UseBlockCallback.EVENT.register(new UseVillagerBlockCallback());
        if (CHECK_UPDATES.get()) {
            checkUpdates();
            ServerPlayConnectionEvents.JOIN.register(new UpdateNotifier(() -> newerVersion));
        }

        // Register Commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("clickvillagers")
                    .requires(VersionHelper::isOp)
                    .then(FabricCommandAdapter.ofConfig(CONFIG)
                            .add(new SetCommand((sender, key, value) -> {
                                MessageTypes.CONFIG.send(Linen.commandSender(sender), "Config option <bold>\"" + key + "\"</bold> set to <bold>" + value + "</bold>.");
                            }))
                            .add(new GetCommand((sender, key, value) -> {
                                MessageTypes.CONFIG.send(Linen.commandSender(sender), "Config option \"<bold>" + key + "\"</bold> has value <bold>" + value + "</bold>.");
                            }))
                            .add(new ReloadCommand(sender -> {
                                MessageTypes.CONFIG.send(Linen.commandSender(sender), "Reloaded the config file.");
                            }))
                            .add(new PathCommand((sender, path) -> {
                                MessageTypes.CONFIG.send(Linen.commandSender(sender), "The config file is located at: <white>" + path + "</white>.");
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
        String minecraftVersion = MinecraftVersion.create().name();
        //?} elif >= 1.21.6 {
        /*String minecraftVersion = MinecraftVersion.CURRENT.name();
         *///?} else
        /*String minecraftVersion = MinecraftVersion.CURRENT.getName();*/
        new ModrinthUpdateChecker(MOD_ID, "fabric", minecraftVersion).checkVersion(version -> {
            if (modVersion == null || ModrinthUpdateChecker.getRawVersion(modVersion).equals(version)) {
                return;
            }
            newerVersion = version;
            LOGGER.info("Newer version available: {}", version);
        });
    }
}