/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ChatInputListener implements Listener {

    private final JavaPlugin plugin;

    public ChatInputListener(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private final Map<Player, Consumer<String>> callbackMap = new ConcurrentHashMap<>();

    public void addChatCallback(Player player, Consumer<String> onInput, Runnable onCancel, long timeoutTicks) {
        callbackMap.put(player, onInput);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!onInput.equals(callbackMap.get(player))) return;
            callbackMap.remove(player);
            onCancel.run();
        }, timeoutTicks);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Consumer<String> callback = callbackMap.get(player);
        if (callback == null) return;
        callback.accept(event.getMessage());
        callbackMap.remove(player);
        event.setMessage("");
        event.setCancelled(true);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        callbackMap.remove(event.getPlayer());
    }
}
