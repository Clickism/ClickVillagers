/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.listener;

import de.clickism.clickvillagers.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class JoinListener implements Listener {

    private final JavaPlugin plugin;
    private final Supplier<String> newerVersionSupplier;
    // Notify players only once per session
    private final Set<UUID> notifiedPlayers = new HashSet<>();

    @AutoRegistered
    public JoinListener(JavaPlugin plugin, Supplier<String> newerVersionSupplier) {
        this.plugin = plugin;
        this.newerVersionSupplier = newerVersionSupplier;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.isOp()) return;
        String newerVersion = newerVersionSupplier.get();
        if (newerVersion == null) return;
        if (notifiedPlayers.contains(player.getUniqueId())) return;
        notifiedPlayers.add(player.getUniqueId());
        // Delay the message
        Bukkit.getScheduler().runTaskLater(plugin,
                () -> Message.UPDATE.send(player, newerVersion), 10L);
    }
}
