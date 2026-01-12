/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.event;

import de.clickism.clickvillagers.util.MessageType;
import de.clickism.clickvillagers.util.VersionHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class UpdateNotifier {

    private final Supplier<String> newerVersionSupplier;
    // Warn players only once per session
    private final Set<UUID> notifiedPlayers = new HashSet<>();

    public UpdateNotifier(Supplier<String> newerVersionSupplier) {
        this.newerVersionSupplier = newerVersionSupplier;
    }

    public void onJoin(ServerPlayer player) {
        String newerVersion = newerVersionSupplier.get();
        if (newerVersion == null) return;
        if (notifiedPlayers.contains(player.getUUID())) return;
        notifiedPlayers.add(player.getUUID());
        if (!VersionHelper.isOpOrInSinglePlayer(player.createCommandSourceStack())) return;
        MessageType.WARN.send(player, Component.literal("ClickVillagers: Newer version available: ")
                .append(Component.nullToEmpty(newerVersion)));
    }
}
