/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public abstract class MessageType {

    public static final MessageType CONFIRM = new MessageType(
            Text.literal("[✔] ").formatted(Formatting.GREEN),
            Style.EMPTY.withColor(Formatting.GREEN)
    ) {
        @Override
        public void playSound(PlayerEntity player) {
            VersionHelper.playSound(player, SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(), SoundCategory.MASTER, 1, 1);
            VersionHelper.playSound(player, SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(), SoundCategory.MASTER, 1, 2);
        }
    };

    public static final MessageType FAIL = new MessageType(
            Text.literal("[✘] ").formatted(Formatting.RED),
            Style.EMPTY.withColor(Formatting.RED)
    ) {
        @Override
        public void playSound(PlayerEntity player) {
            VersionHelper.playSound(player, SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.MASTER, 1, .5f);
        }
    };

    public static final MessageType WARN = new MessageType(
            Text.literal("[⚠] ").formatted(Formatting.YELLOW),
            Style.EMPTY.withColor(Formatting.YELLOW)
    ) {
        @Override
        public void playSound(PlayerEntity player) {
            VersionHelper.playSound(player, SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.MASTER, 1, 1f);
        }
    };

    public static final MessageType PICKUP_MESSAGE = silent(
            Text.literal("[↑] ").formatted(Formatting.GREEN),
            Text.literal("< ").formatted(Formatting.DARK_GRAY)
                    .append(Text.literal("↑ ").formatted(Formatting.DARK_GREEN)),
            Text.literal(" >").formatted(Formatting.DARK_GRAY),
            Style.EMPTY.withColor(Formatting.GREEN)
    );

    public static final MessageType ANCHOR_ADD = silent(
            Text.literal("[⚓] ").formatted(Formatting.DARK_GREEN),
            Text.literal("< ").formatted(Formatting.DARK_GRAY)
                    .append(Text.literal("⚓ ").formatted(Formatting.DARK_GREEN)),
            Text.literal(" >").formatted(Formatting.DARK_GRAY),
            Style.EMPTY.withColor(Formatting.GREEN)
    );

    public static final MessageType ANCHOR_REMOVE = silent(
            Text.literal("[⚓] ").formatted(Formatting.GOLD),
            Text.literal("< ").formatted(Formatting.DARK_GRAY)
                    .append(Text.literal("⚓ ").formatted(Formatting.GOLD)),
            Text.literal(" >").formatted(Formatting.DARK_GRAY),
            Style.EMPTY.withColor(Formatting.YELLOW)
    );

    public static final MessageType CONFIG = new MessageType(
            Text.literal("[⚒] ").formatted(Formatting.GOLD),
            Style.EMPTY.withColor(Formatting.GREEN)
    ) {
        @Override
        public void playSound(PlayerEntity player) {
            MessageType.CONFIRM.playSound(player);
        }
    };

    private final Text prefix;

    private final Text actionbarPrefix;
    private final Text actionbarSuffix;
    private final Style actionbarStyle;

    public MessageType(Text prefix, Style actionbarStyle) {
        this(prefix, Text.literal("< ").formatted(Formatting.DARK_GRAY),
                Text.literal(" >").formatted(Formatting.DARK_GRAY), actionbarStyle);
    }

    public MessageType(Text prefix, Text actionbarPrefix, Text actionbarSuffix, Style actionbarStyle) {
        this.prefix = prefix;
        this.actionbarPrefix = actionbarPrefix;
        this.actionbarSuffix = actionbarSuffix;
        this.actionbarStyle = actionbarStyle;
    }

    public abstract void playSound(PlayerEntity player);

    public void send(PlayerEntity player, Text message) {
        send(player, message, false, false);
    }

    public void send(ServerCommandSource source, Text message) {
        var player = source.getPlayer();
        if (player != null) {
            send(player, message, false, false);
            return;
        }
        source.sendMessage(message);
    }

    public void sendSilently(PlayerEntity player, Text message) {
        send(player, message, true, false);
    }

    public void sendActionbar(PlayerEntity player, Text message) {
        send(player, message, false, true);
    }

    public void sendActionbarSilently(PlayerEntity player, Text message) {
        send(player, message, true, true);
    }

    public void send(PlayerEntity player, Text message, boolean silent, boolean actionbar) {
        MutableText text;
        if (actionbar) {
            text = actionbarPrefix.copy().append(message.copy().setStyle(actionbarStyle)).append(actionbarSuffix);
        } else {
            text = prefix.copy().append(message.copy().setStyle(prefix.getStyle()));
        }
        player.sendMessage(text, actionbar);
        if (silent) return;
        playSound(player);
    }
    
    public static MessageType silent(Text prefix, Text actionbarPrefix, Text actionbarSuffix, Style actionbarStyle) {
        return new MessageType(prefix, actionbarPrefix, actionbarSuffix, actionbarStyle) {
            @Override 
            public void playSound(PlayerEntity player) {}
        };
    }
}
