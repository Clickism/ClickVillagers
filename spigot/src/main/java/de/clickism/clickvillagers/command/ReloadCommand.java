/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.command;

import de.clickism.clickvillagers.ClickVillagers;
import de.clickism.clickvillagers.ClickVillagersConfig;
import de.clickism.clickvillagers.hopper.config.HopperConfig;
import de.clickism.clickvillagers.message.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.logging.Level;

public class ReloadCommand implements CommandExecutor, TabCompleter {
    private final HopperConfig config;

    public ReloadCommand(HopperConfig config) {
        this.config = config;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!command.getLabel().equalsIgnoreCase("clickvillagers")) return false;
        if (args.length != 1) {
            sendUsage(sender);
            return false;
        }
        if (!args[0].equalsIgnoreCase("reload")) {
            sendUsage(sender);
            return false;
        }
        if (Permission.RELOAD.lacksAndNotify(sender)) return false;
        try {
            ClickVillagersConfig.CONFIG.load();
            config.reloadConfig();
            Message.RELOAD_SUCCESS.send(sender);
        } catch (Exception exception) {
            ClickVillagers.LOGGER.log(Level.SEVERE, "Failed to reload config/messages: ", exception);
            Message.RELOAD_FAIL.send(sender);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!command.getLabel().equalsIgnoreCase("clickvillagers")) return null;
        if (args.length != 1) {
            return List.of();
        }
        return List.of("reload");
    }

    private void sendUsage(CommandSender sender) {
        Message.USAGE.send(sender, "/clickvillagers <reload>");
    }
}
