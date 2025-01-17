/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.message;

import me.clickism.clickvillagers.ClickVillagers;
import me.clickism.clickvillagers.util.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

/**
 * Represents a type of message that can be sent to players.
 * Contains a prefix and a sound to play when the message is sent.
 */
public abstract class MessageType {
    public static final MessageType CONFIRM = new MessageType("&a[✔] ", "&8< &a%s &8>") {
        @Override
        public void playSound(Player player) {
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, 1f, 1f);
            Bukkit.getScheduler().runTaskLater(ClickVillagers.INSTANCE, task -> {
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, 1f, 2f);
            }, 2L);
        }
    };
    public static final MessageType FAIL = new MessageType("&c[❌] ", "&8< &c%s &8>") {
        @Override
        public void playSound(Player player) {
            player.playSound(player, Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1f, .5f);
        }
    };
    public static final MessageType WARN = new MessageType("&e[⚠] ", "&8< &e%s &8>") {
        @Override
        public void playSound(Player player) {
            player.playSound(player, Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1f, 1f);
        }
    };

    public static final MessageType PICK_UP = silent("&2[↑] &a", "&8< &2↑ &a%s &8>");
    public static final MessageType ANCHOR_ADD = silent("&2[⚓] &e", "&8< &2⚓ &a%s &8>");
    public static final MessageType ANCHOR_REMOVE = silent("&2[⚓] &e", "&8< &6⚓ &e%s &8>");
    public static final MessageType HOPPER_PLACE = silent("&2[📥] &a", "&8< &2📥 &a%s &8>");
    public static final MessageType HOPPER_BREAK = silent("&2[📥] &a", "&8< &6📥 &e%s &8>");

    /**
     * An enum that represents where to send a message.
     */
    protected enum SendType {
        /**
         * Send the message to the chat.
         */
        CHAT,
        /**
         * Send the message to the action bar.
         */
        ACTION_BAR
    }

    private final String prefix;
    private final String titleFormat;
    private final String subtitleFormat;

    /**
     * Create a new message type with the given prefix.
     *
     * @param prefix      the prefix of the message
     * @param titleFormat the format of the title, subtitle and action bar message.
     */
    public MessageType(String prefix, String titleFormat) {
        this(prefix, titleFormat, titleFormat);
    }

    /**
     * Create a new message type with the given prefix and subtitle format.
     *
     * @param prefix         the prefix of the message
     * @param titleFormat    the format of the title and action bar message.
     * @param subtitleFormat the format of the subtitle message.
     */
    public MessageType(String prefix, String titleFormat, String subtitleFormat) {
        this.prefix = Utils.colorize(prefix);
        this.titleFormat = Utils.colorize(titleFormat);
        this.subtitleFormat = Utils.colorize(subtitleFormat);
    }

    /**
     * Play the sound for the message to the player.
     *
     * @param player the player to play the sound to
     */
    public abstract void playSound(Player player);

    /**
     * Send the message to the sender.
     *
     * @param sender  the sender to send the message to
     * @param message the message to send
     */
    public void send(CommandSender sender, String message) {
        send(sender, message, SendType.CHAT, false);
    }

    /**
     * Send the message to the sender without playing the sound.
     *
     * @param sender  the sender to send the message to
     * @param message the message to send
     */
    public void sendSilently(CommandSender sender, String message) {
        send(sender, message, SendType.CHAT, true);
    }

    /**
     * Send the message to the sender.
     *
     * @param sender  the sender to send the message to
     * @param message the message to send
     */
    public void sendActionbar(CommandSender sender, String message) {
        send(sender, message, SendType.ACTION_BAR, false);
    }

    /**
     * Send the message to the sender without playing the sound.
     *
     * @param sender  the sender to send the message to
     * @param message the message to send
     */
    public void sendActionbarSilently(CommandSender sender, String message) {
        send(sender, message, SendType.ACTION_BAR, true);
    }

    /**
     * Send the message to the sender.
     *
     * @param sender   the sender to send the message to
     * @param message  the message to send
     * @param sendType where to send the message
     * @param silent   whether to play the sound
     */
    protected void send(CommandSender sender, String message, SendType sendType, boolean silent) {
        switch (sendType) {
            case CHAT -> sender.sendMessage(Utils.colorize(prefix + message));
            case ACTION_BAR -> {
                if (!(sender instanceof Player player)) break;
                String formatted = String.format(titleFormat, Utils.colorize(message));
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(formatted));
            }
        }
        if (!silent && sender instanceof Player) {
            playSound((Player) sender);
        }
    }

    /**
     * Send a title to the player.
     *
     * @param player   the player to send the title to
     * @param title    title
     * @param subtitle subtitle
     */
    public void sendTitle(Player player, String title, String subtitle) {
        sendTitle(player, title, subtitle, 5, 60, 10, false);
    }

    /**
     * Send a title to the player.
     *
     * @param player   the player to send the title to
     * @param title    title
     * @param subtitle subtitle
     * @param fadeIn   fade in
     * @param stay     stay
     * @param fadeOut  fade out
     */
    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        sendTitle(player, title, subtitle, fadeIn, stay, fadeOut, false);
    }

    /**
     * Send a title to the player that will show instantly and fade out in 10 ticks.
     *
     * @param player   the player to send the title to
     * @param title    title
     * @param subtitle subtitle
     * @param stay     the time in ticks for the title to stay
     */
    public void sendTitleInstant(Player player, String title, String subtitle, int stay) {
        sendTitle(player, title, subtitle, 0, stay, 10, false);
    }

    /**
     * Send a smooth title to the player silently. The title will stay for 3 seconds.
     *
     * @param player   the player to send the title to
     * @param title    title
     * @param subtitle subtitle
     */
    public void sendTitleSilently(Player player, String title, String subtitle) {
        sendTitle(player, title, subtitle, 5, 60, 10, true);
    }

    /**
     * Send a title to the player silently.
     *
     * @param player   the player to send the title to
     * @param title    title
     * @param subtitle subtitle
     * @param fadeIn   fade in
     * @param stay     stay
     * @param fadeOut  fade out
     */
    public void sendTitleSilently(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        sendTitle(player, title, subtitle, fadeIn, stay, fadeOut, true);
    }

    /**
     * Send a title to the player that will show instantly and fade out in 10 ticks silently.
     *
     * @param player   the player to send the title to
     * @param title    the title to send
     * @param subtitle the subtitle to send
     * @param stay     the time in ticks for the title to stay
     */
    public void sendTitleInstantSilently(Player player, String title, String subtitle, int stay) {
        sendTitle(player, title, subtitle, 0, stay, 10, true);
    }

    /**
     * Send a title to the player.
     *
     * @param player   the player to send the title to
     * @param title    title
     * @param subtitle subtitle
     * @param fadeIn   fade in
     * @param stay     stay
     * @param fadeOut  fade out
     * @param silent   whether to play the sound
     */
    protected void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut, boolean silent) {
        String titleMessage = title.isEmpty() ? "" : String.format(titleFormat, Utils.colorize(title));
        String subtitleMessage = subtitle.isEmpty() ? "" : String.format(subtitleFormat, Utils.colorize(subtitle));
        player.sendTitle(titleMessage, subtitleMessage, fadeIn, stay, fadeOut);
        if (!silent) {
            playSound(player);
        }
    }

    /**
     * Send the message to all online players.
     *
     * @param message the message to send
     */
    public void sendAll(String message) {
        Bukkit.getOnlinePlayers().forEach(player -> send(player, message));
        log(message);
    }

    /**
     * Send the message to all online players without playing the sound.
     *
     * @param message the message to send
     */
    public void sendAllSilently(String message) {
        Bukkit.getOnlinePlayers().forEach(player -> sendSilently(player, message));
        log(message);
    }

    /**
     * Send the actionbar message to all online players.
     *
     * @param message the message to send
     */
    public void sendAllActionbar(String message) {
        Bukkit.getOnlinePlayers().forEach(player -> sendActionbar(player, message));
        logActionbar(message);
    }

    /**
     * Send the actionbar message to all online players without playing the sound.
     *
     * @param message the message to send
     */
    public void sendAllActionbarSilently(String message) {
        Bukkit.getOnlinePlayers().forEach(player -> sendActionbarSilently(player, message));
        logActionbar(message);
    }

    /**
     * Send the title to all online players.
     *
     * @param title    title
     * @param subtitle subtitle
     */
    public void sendAllTitle(String title, String subtitle) {
        Bukkit.getOnlinePlayers().forEach(player -> sendTitle(player, title, subtitle));
        logTitle(title, subtitle);
    }

    /**
     * Send the title to all online players without playing the sound.
     *
     * @param title    title
     * @param subtitle subtitle
     */
    public void sendAllTitleSilently(String title, String subtitle) {
        Bukkit.getOnlinePlayers().forEach(player -> sendTitleSilently(player, title, subtitle));
        logTitle(title, subtitle);
    }

    /**
     * Send the title to all online players that will show instantly and fade out in 10 ticks.
     *
     * @param title    title
     * @param subtitle subtitle
     * @param stay     stay
     */
    public void sendAllTitleInstant(String title, String subtitle, int stay) {
        Bukkit.getOnlinePlayers().forEach(player -> sendTitleInstant(player, title, subtitle, stay));
        logTitle(title, subtitle);
    }

    /**
     * Send the title to all online players that will show instantly and fade out in 10 ticks silently.
     *
     * @param title    title
     * @param subtitle subtitle
     * @param stay     stay
     */
    public void sendAllTitleInstantSilently(String title, String subtitle, int stay) {
        Bukkit.getOnlinePlayers().forEach(player -> sendTitleInstantSilently(player, title, subtitle, stay));
        logTitle(title, subtitle);
    }

    /**
     * Send the title to all online players.
     *
     * @param title    title
     * @param subtitle subtitle
     * @param fadeIn   fade in
     * @param stay     stay
     * @param fadeOut  fade out
     */
    public void sendAllTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        Bukkit.getOnlinePlayers().forEach(player -> sendTitle(player, title, subtitle, fadeIn, stay, fadeOut));
        logTitle(title, subtitle);
    }

    /**
     * Send the title to all online players without playing the sound.
     *
     * @param title    title
     * @param subtitle subtitle
     * @param fadeIn   fade in
     * @param stay     stay
     * @param fadeOut  fade out
     */
    public void sendAllTitleSilently(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        Bukkit.getOnlinePlayers().forEach(player -> sendTitleSilently(player, title, subtitle, fadeIn, stay, fadeOut));
        logTitle(title, subtitle);
    }

    private void logTitle(String title, String subtitle) {
        if (title.isEmpty()) {
            log("Subtitle: " + subtitle);
            return;
        }
        if (subtitle.isEmpty()) {
            log("Title: " + title);
            return;
        }
        log("Title: " + title + " Subtitle: " + subtitle);
    }

    private void logActionbar(String message) {
        log("Actionbar: " + message);
    }

    private void log(String message) {
        String colorized = Utils.colorize(prefix + message);
        Bukkit.getLogger().log(Level.INFO, ChatColor.stripColor(colorized));
    }

    public static MessageType silent(String prefix, String titleFormat) {
        return silent(prefix, titleFormat, titleFormat);
    }

    public static MessageType silent(String prefix, String titleFormat, String subtitleFormat) {
        return new MessageType(prefix, titleFormat, subtitleFormat) {
            @Override
            public void playSound(Player player) {}
        };
    }
}
