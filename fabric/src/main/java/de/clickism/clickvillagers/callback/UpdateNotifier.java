/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.callback;

import de.clickism.clickvillagers.util.MessageType;
import de.clickism.clickvillagers.util.VersionHelper;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class UpdateNotifier implements ServerPlayConnectionEvents.Join {

    private final Supplier<String> newerVersionSupplier;
    // Warn players only once per session
    private final Set<UUID> notifiedPlayers = new HashSet<>();

    public UpdateNotifier(Supplier<String> newerVersionSupplier) {
        this.newerVersionSupplier = newerVersionSupplier;
    }

    @Override
    public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        String newerVersion = newerVersionSupplier.get();
        if (newerVersion == null) return;
        ServerPlayerEntity player = handler.player;
        if (notifiedPlayers.contains(player.getUuid())) return;
        notifiedPlayers.add(player.getUuid());
        if (!VersionHelper.isOp(player)) return;
        MessageType.WARN.send(player, Text.literal("ClickVillagers: Newer version available: ")
                .append(Text.of(newerVersion)));
    }
}
