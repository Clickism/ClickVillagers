package me.clickism.clickvillagers.util;

import net.minecraft.entity.player.PlayerEntity;
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
            player.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(), SoundCategory.MASTER, 1, 1);
            player.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(), SoundCategory.MASTER, 1, 2);
        }
    };

    public static final MessageType FAIL = new MessageType(
            Text.literal("[✘] ").formatted(Formatting.RED),
            Style.EMPTY.withColor(Formatting.RED)
    ) {
        @Override
        public void playSound(PlayerEntity player) {
            player.playSoundToPlayer(SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.MASTER, 1, .5f);
        }
    };

    public static final MessageType WARN = new MessageType(
            Text.literal("[⚠] ").formatted(Formatting.YELLOW),
            Style.EMPTY.withColor(Formatting.YELLOW)
    ) {
        @Override
        public void playSound(PlayerEntity player) {
            player.playSoundToPlayer(SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.MASTER, 1, 1f);
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
}