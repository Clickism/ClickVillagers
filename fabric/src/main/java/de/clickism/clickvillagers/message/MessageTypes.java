/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.message;

import de.clickism.linen.core.message.MessageType;

public interface MessageTypes {
    MessageType PICK_UP = MessageType.icon("↑").iconColor("dark_green").messageColor("green");
    MessageType ANCHOR_ADD = MessageType.icon("⚓").iconColor("dark_green").messageColor("green");
    MessageType ANCHOR_REMOVE = MessageType.icon("⚓").iconColor("gold").messageColor("yellow");
//    MessageType HOPPER_PLACE = MessageType.icon("📥").iconColor("dark_green").messageColor("green");
//    MessageType HOPPER_BREAK = MessageType.icon("📥").iconColor("gold").messageColor("yellow");
    MessageType CONFIG = MessageType.icon("⚒").iconColor("gold").messageColor("green").sound(MessageType.SUCCESS);
}
