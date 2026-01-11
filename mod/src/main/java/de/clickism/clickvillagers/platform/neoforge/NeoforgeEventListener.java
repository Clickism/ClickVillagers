/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.platform.neoforge;
//? if neoforge {

/*import de.clickism.clickvillagers.event.PickupVillagerListener;
import de.clickism.clickvillagers.event.PlaceVillagerInVehicleListener;
import de.clickism.clickvillagers.event.PlaceVillagerListener;
import de.clickism.clickvillagers.event.UpdateNotifier;
import de.clickism.clickvillagers.util.CooldownManager;
import de.clickism.clickvillagers.util.MessageType;
import de.clickism.clickvillagers.util.VersionHelper;
import de.clickism.configured.neoforgecommandadapter.NeoforgeCommandAdapter;
import de.clickism.configured.neoforgecommandadapter.command.GetCommand;
import de.clickism.configured.neoforgecommandadapter.command.PathCommand;
import de.clickism.configured.neoforgecommandadapter.command.ReloadCommand;
import de.clickism.configured.neoforgecommandadapter.command.SetCommand;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import static de.clickism.clickvillagers.ClickVillagersConfig.CONFIG;
import static de.clickism.clickvillagers.ClickVillagersConfig.COOLDOWN;

public class NeoforgeEventListener {
    private final PickupVillagerListener pickupVillagerListener = new PickupVillagerListener(new CooldownManager(COOLDOWN::get));
    private final PlaceVillagerListener placeVillagerListener = new PlaceVillagerListener();
    private final PlaceVillagerInVehicleListener placeVillagerInVehicleListener = new PlaceVillagerInVehicleListener();

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        placeVillagerListener.event(event.getEntity(), event.getLevel(), event.getHand(), event.getHitVec());
    }

    @SubscribeEvent
    public void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        pickupVillagerListener.event(event.getEntity(), event.getLevel(), event.getHand(), event.getTarget());
        placeVillagerInVehicleListener.event(event.getEntity(), event.getLevel(), event.getHand(), event.getTarget());
    }

    public record JoinListener(UpdateNotifier notifier) {
        @SubscribeEvent
        public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player)) return;
            notifier.onJoin(player);
        }
    }

    public record ConfigCommandRegisterListener() {
        @SubscribeEvent
        public void onRegisterCommands(RegisterCommandsEvent event) {
            event.getDispatcher().register(Commands.literal("clickvillagers")
                    .requires(VersionHelper::isOpOrInSinglePlayer)
                    .then(NeoforgeCommandAdapter.ofConfig(CONFIG)
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
                            .buildRoot())
            );
        }
    }
}
*///?}