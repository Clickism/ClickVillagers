/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.command;

import de.clickism.clickvillagers.ClickVillagers;
import de.clickism.clickvillagers.message.Message;
import de.clickism.clickvillagers.message.MessageType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

import static de.clickism.clickvillagers.ClickVillagersConfig.CONFIG;

public class ClickVillagersCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!command.getLabel().equalsIgnoreCase("clickvillagers")) return false;
        if (args.length != 1) {
            sendUsage(sender);
            return false;
        }
        if (Permission.RELOAD.lacksAndNotify(sender)) return false;
        if (args[0].equalsIgnoreCase("config_path")) {
            sendConfigPath(sender);
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            reloadConfig(sender);
            return true;
        }
        sendUsage(sender);
        return false;
    }

    private void sendConfigPath(CommandSender sender) {
        File file = CONFIG.file();
        if (file == null) {
            MessageType.FAIL.send(sender, "Config file not found.");
            return;
        }
        MessageType.CONFIRM.send(sender, "Config path: &l" + file.getAbsolutePath());
    }

    private void reloadConfig(CommandSender sender) {
        try {
            CONFIG.load();
            Message.RELOAD_SUCCESS.send(sender);
        } catch (Exception exception) {
            ClickVillagers.LOGGER.log(Level.SEVERE, "Failed to reload config/messages: ", exception);
            Message.RELOAD_FAIL.send(sender);
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!command.getLabel().equalsIgnoreCase("clickvillagers")) return null;
        if (args.length != 1) {
            return List.of();
        }
        return List.of("reload", "config_path");
    }

    private void sendUsage(CommandSender sender) {
        Message.USAGE.send(sender, "/clickvillagers <reload|config_path>");
    }
}