/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.util;

import me.clickism.clickvillagers.message.Message;
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

    public void send(Player player) {
        message.getTypeOrDefault().send(player, toString());
    }

    public void sendSilently(Player player) {
        message.getTypeOrDefault().sendSilently(player, toString());
    }

    public void sendActionbar(Player player) {
        message.getTypeOrDefault().sendActionbar(player, toString());
    }

    public void sendActionbarSilently(Player player) {
        message.getTypeOrDefault().sendActionbarSilently(player, toString());
    }

    public static MessageParameterizer of(Message message) {
        return new MessageParameterizer(message);
    }
}
