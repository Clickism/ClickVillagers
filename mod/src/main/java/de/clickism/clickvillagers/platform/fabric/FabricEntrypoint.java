/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.platform.fabric;
//? if fabric {

import de.clickism.clickvillagers.ClickVillagers;
import de.clickism.clickvillagers.event.PickupVillagerListener;
import de.clickism.clickvillagers.event.PlaceVillagerInVehicleListener;
import de.clickism.clickvillagers.event.PlaceVillagerListener;
import de.clickism.clickvillagers.event.UpdateNotifier;
import de.clickism.clickvillagers.util.CooldownManager;
import de.clickism.clickvillagers.util.MessageType;
import de.clickism.clickvillagers.util.VersionHelper;
import de.clickism.configured.fabriccommandadapter.FabricCommandAdapter;
import de.clickism.configured.fabriccommandadapter.command.GetCommand;
import de.clickism.configured.fabriccommandadapter.command.PathCommand;
import de.clickism.configured.fabriccommandadapter.command.ReloadCommand;
import de.clickism.configured.fabriccommandadapter.command.SetCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import static de.clickism.clickvillagers.ClickVillagersConfig.*;

public class FabricEntrypoint implements ModInitializer {
    @Override
    public void onInitialize() {
        CooldownManager cooldownManager = new CooldownManager(COOLDOWN::get);
        var pickupMobListener = new PickupVillagerListener(cooldownManager);
        var placeMobInVehicleListener = new PlaceVillagerInVehicleListener();
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) ->
                pickupMobListener.event(player, level, hand, entity));
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) ->
                placeMobInVehicleListener.event(player, level, hand, entity));
        UseBlockCallback.EVENT.register(new PlaceVillagerListener()::event);

        ClickVillagers.initialize();

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

        // Check for updates
        if (CHECK_UPDATES.get()) {
            String modVersion = FabricLoader.getInstance().getModContainer(ClickVillagers.MOD_ID)
                    .map(container -> container.getMetadata().getVersion().getFriendlyString())
                    .orElse(null);
            ClickVillagers.checkUpdates(modVersion, "fabric");
            var notifier = new UpdateNotifier(ClickVillagers::newerVersion);
            ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
                notifier.onJoin(handler.player);
            });
        }
    }
}
//?}
