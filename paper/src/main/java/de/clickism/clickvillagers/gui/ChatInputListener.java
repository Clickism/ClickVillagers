/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.gui;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ChatInputListener implements Listener {

    private final JavaPlugin plugin;
    private final Map<UUID, Consumer<String>> callbackMap = new ConcurrentHashMap<>();

    public ChatInputListener(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void addChatCallback(Player player, Consumer<String> onInput, Runnable onCancel, long timeoutTicks) {
        UUID uuid = player.getUniqueId();
        callbackMap.put(uuid, onInput);
        player.getScheduler().runDelayed(plugin,
                task -> {
                    var current = callbackMap.get(uuid);
                    if (onInput != current) return;
                    callbackMap.remove(uuid);
                    onCancel.run();
                },
                null,
                timeoutTicks
        );
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        var callback = callbackMap.get(uuid);
        if (callback == null) return;

        String message = PlainTextComponentSerializer.plainText().serialize(event.message());

        event.message(Component.empty());
        event.setCancelled(true);

        // Run on player region
        player.getScheduler().run(plugin, task -> {
            var current = callbackMap.remove(uuid);
            if (current == null) return;
            current.accept(message);
        }, null);

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        callbackMap.remove(uuid);
    }
}
