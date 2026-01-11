/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public abstract class MessageType {

    public static final MessageType CONFIRM = new MessageType(
            Component.literal("[✔] ").withStyle(ChatFormatting.GREEN),
            Style.EMPTY.withColor(ChatFormatting.GREEN)
    ) {
        @Override
        public void playSound(Player player) {
            VersionHelper.playSound(player, SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.MASTER, 1, 1);
            VersionHelper.playSound(player, SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.MASTER, 1, 2);
        }
    };

    public static final MessageType FAIL = new MessageType(
            Component.literal("[✘] ").withStyle(ChatFormatting.RED),
            Style.EMPTY.withColor(ChatFormatting.RED)
    ) {
        @Override
        public void playSound(Player player) {
            VersionHelper.playSound(player, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.MASTER, 1, .5f);
        }
    };

    public static final MessageType WARN = new MessageType(
            Component.literal("[⚠] ").withStyle(ChatFormatting.YELLOW),
            Style.EMPTY.withColor(ChatFormatting.YELLOW)
    ) {
        @Override
        public void playSound(Player player) {
            VersionHelper.playSound(player, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.MASTER, 1, 1f);
        }
    };

    public static final MessageType PICKUP_MESSAGE = silent(
            Component.literal("[↑] ").withStyle(ChatFormatting.GREEN),
            Component.literal("< ").withStyle(ChatFormatting.DARK_GRAY)
                    .append(Component.literal("↑ ").withStyle(ChatFormatting.DARK_GREEN)),
            Component.literal(" >").withStyle(ChatFormatting.DARK_GRAY),
            Style.EMPTY.withColor(ChatFormatting.GREEN)
    );

    public static final MessageType ANCHOR_ADD = silent(
            Component.literal("[⚓] ").withStyle(ChatFormatting.DARK_GREEN),
            Component.literal("< ").withStyle(ChatFormatting.DARK_GRAY)
                    .append(Component.literal("⚓ ").withStyle(ChatFormatting.DARK_GREEN)),
            Component.literal(" >").withStyle(ChatFormatting.DARK_GRAY),
            Style.EMPTY.withColor(ChatFormatting.GREEN)
    );

    public static final MessageType ANCHOR_REMOVE = silent(
            Component.literal("[⚓] ").withStyle(ChatFormatting.GOLD),
            Component.literal("< ").withStyle(ChatFormatting.DARK_GRAY)
                    .append(Component.literal("⚓ ").withStyle(ChatFormatting.GOLD)),
            Component.literal(" >").withStyle(ChatFormatting.DARK_GRAY),
            Style.EMPTY.withColor(ChatFormatting.YELLOW)
    );

    public static final MessageType CONFIG = new MessageType(
            Component.literal("[⚒] ").withStyle(ChatFormatting.GOLD),
            Style.EMPTY.withColor(ChatFormatting.GREEN)
    ) {
        @Override
        public void playSound(Player player) {
            MessageType.CONFIRM.playSound(player);
        }
    };

    private final Component prefix;

    private final Component actionbarPrefix;
    private final Component actionbarSuffix;
    private final Style actionbarStyle;

    public MessageType(Component prefix, Style actionbarStyle) {
        this(prefix, Component.literal("< ").withStyle(ChatFormatting.DARK_GRAY),
                Component.literal(" >").withStyle(ChatFormatting.DARK_GRAY), actionbarStyle);
    }

    public MessageType(Component prefix, Component actionbarPrefix, Component actionbarSuffix, Style actionbarStyle) {
        this.prefix = prefix;
        this.actionbarPrefix = actionbarPrefix;
        this.actionbarSuffix = actionbarSuffix;
        this.actionbarStyle = actionbarStyle;
    }

    public abstract void playSound(Player player);

    public void send(Player player, Component message) {
        send(player, message, false, false);
    }

    public void send(CommandSourceStack source, Component message) {
        var player = source.getPlayer();
        if (player != null) {
            send(player, message, false, false);
            return;
        }
        source.sendSystemMessage(message);
    }

    public void sendSilently(Player player, Component message) {
        send(player, message, true, false);
    }

    public void sendActionbar(Player player, Component message) {
        send(player, message, false, true);
    }

    public void sendActionbarSilently(Player player, Component message) {
        send(player, message, true, true);
    }

    public void send(Player player, Component message, boolean silent, boolean actionbar) {
        MutableComponent text;
        if (actionbar) {
            text = actionbarPrefix.copy().append(message.copy().setStyle(actionbarStyle)).append(actionbarSuffix);
        } else {
            text = prefix.copy().append(message.copy().setStyle(prefix.getStyle()));
        }
        player.displayClientMessage(text, actionbar);
        if (silent) return;
        playSound(player);
    }
    
    public static MessageType silent(Component prefix, Component actionbarPrefix, Component actionbarSuffix, Style actionbarStyle) {
        return new MessageType(prefix, actionbarPrefix, actionbarSuffix, actionbarStyle) {
            @Override 
            public void playSound(Player player) {}
        };
    }
}
