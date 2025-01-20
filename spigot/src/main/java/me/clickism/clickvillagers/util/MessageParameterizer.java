/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.util;

import me.clickism.clickvillagers.message.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MessageParameterizer extends Parameterizer {

    protected Message message;

    public MessageParameterizer(Message message) {
        super(message.toString());
        this.message = message;
    }

    @Override
    public MessageParameterizer put(String key, @NotNull Object value) {
        return (MessageParameterizer) super.put(key, value);
    }

    @Override
    public MessageParameterizer putAll(Parameterizer parameterizer) {
        return (MessageParameterizer) super.putAll(parameterizer);
    }

    @Override
    public MessageParameterizer disableColorizeParameters() {
        return (MessageParameterizer) super.disableColorizeParameters();
    }

    public String replace(Message message) {
        return replace(message.toString());
    }

    public void send(CommandSender sender) {
        message.getTypeOrDefault().send(sender, toString());
    }

    public void sendSilently(CommandSender sender) {
        message.getTypeOrDefault().sendSilently(sender, toString());
    }

    public void sendActionbar(CommandSender sender) {
        message.getTypeOrDefault().sendActionbar(sender, toString());
    }

    public void sendActionbarSilently(CommandSender sender) {
        message.getTypeOrDefault().sendActionbarSilently(sender, toString());
    }

    public static MessageParameterizer of(Message message) {
        return new MessageParameterizer(message);
    }
}
