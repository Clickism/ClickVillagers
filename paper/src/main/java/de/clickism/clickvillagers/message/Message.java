/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.message;

import de.clickism.clickgui.menu.Icon;
import de.clickism.clickvillagers.ClickVillagers;
import de.clickism.configured.localization.Localization;
import de.clickism.configured.localization.Parameters;
import de.clickism.configured.localization.Translatable;
import de.clickism.linen.core.Linen;
import de.clickism.linen.core.util.Colorizer;
import de.clickism.linen.core.message.MessageType;
import de.clickism.linen.core.message.TypedMessage;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Message implements Translatable, TypedMessage {
    // GENERAL
    @Parameters("version")
    UPDATE(MessageType.WARN),
    NO_PERMISSION(MessageType.ERROR),
    ANCHOR_ADD(MessageTypes.ANCHOR_ADD),
    ANCHOR_REMOVE(MessageTypes.ANCHOR_REMOVE),
    VILLAGER_HOPPER_PLACE(MessageTypes.HOPPER_PLACE),
    VILLAGER_HOPPER_BREAK(MessageTypes.HOPPER_BREAK),
    PICK_UP_VILLAGER(MessageTypes.PICK_UP),
    ENTER_PARTNER(MessageType.WARN),
    ENTER_PARTNER_TIMEOUT(MessageType.ERROR),
    CLAIM_VILLAGER(MessageType.SUCCESS),
    UNCLAIM_VILLAGER(MessageType.WARN),
    INVALID_PARTNER(MessageType.ERROR),
    @Parameters("partner")
    PARTNER_ADD(MessageType.SUCCESS),
    @Parameters("partner")
    PARTNER_REMOVE(MessageType.WARN),
    @Parameters("limit")
    PARTNER_LIMIT_REACHED(MessageType.ERROR),
    @Parameters("limit")
    HOPPER_LIMIT_REACHED(MessageType.ERROR),
    @Parameters("owner")
    BELONGS_TO(MessageType.ERROR),
    @Parameters("biome")
    BIOME_CHANGED(MessageType.SUCCESS),

    WRITE_ERROR(MessageType.ERROR),
    READ_ERROR(MessageType.ERROR),

    TRADES_RESET(MessageType.WARN),

    INFO_OWNER,
    INFO_ANCHORED,
    INFO_TRADE_CLOSED,
    INFO_TRADES,

    // ITEMS
    VILLAGER,
    BABY_VILLAGER,
    @Parameters("profession")
    VILLAGER_WITH_PROFESSION,
    VILLAGER_HOPPER,

    // TITLES
    TITLE_CLAIM_VILLAGER,
    @Parameters("owner")
    TITLE_EDIT,
    TITLE_CHANGE_BIOME,

    // BUTTONS
    BUTTON_CLAIM_VILLAGER,
    BUTTON_UNCLAIM_VILLAGER,
    BUTTON_PICK_UP_VILLAGER,
    BUTTON_PARTNER,
    BUTTON_TRADE_OPEN,
    BUTTON_TRADE_CLOSED,
    BUTTON_REDIRECT_CHANGE_BIOME_MENU,
    BUTTON_CHANGE_BIOME,
    BUTTON_BACK,

    BUTTON_RESET_TRADES,

    // COMMANDS
    @Parameters("usage")
    USAGE(MessageType.ERROR),

    @Parameters("seconds")
    PICK_UP_COOLDOWN(MessageType.ERROR),
    CLAIM_COOLDOWN(MessageType.ERROR),

    @Parameters({"option", "value"})
    CONFIG_SET(MessageTypes.CONFIG),
    @Parameters({"option", "value"})
    CONFIG_GET(MessageTypes.CONFIG),
    @Parameters("path")
    CONFIG_PATH(MessageTypes.CONFIG),
    CONFIG_RELOAD(MessageTypes.CONFIG);

    public static final Localization LOCALIZATION =
            Localization.of(lang -> "plugins/ClickVillagers/lang/" + lang + ".json")
                    .resourceProvider(ClickVillagers.class, lang -> "/lang/" + lang + ".json")
                    .fallbackLanguage("en_US")
                    .version(6);

    private final MessageType type;

    Message() {
        this(MessageType.ERROR);
    }

    Message(@Nullable MessageType type) {
        this.type = type;
    }

    // Convenience Platform methods

    public void send(CommandSender sender, Object... params) {
        send(Linen.commandSender(sender), params);
    }

    public void sendSilently(CommandSender sender, Object... params) {
        sendSilently(Linen.commandSender(sender), params);
    }

    public void sendOverlay(CommandSender sender, Object... params) {
        sendOverlay(Linen.commandSender(sender), params);
    }

    public void sendOverlaySilently(CommandSender sender, Object... params) {
        sendOverlaySilently(Linen.commandSender(sender), params);
    }

    public List<String> getLore() {
        String pathToLore = name().toLowerCase() + ".lore";
        String text = LOCALIZATION.get(pathToLore);
        return Arrays.stream(text.split("\n"))
                .map(Colorizer::rich)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Icon toIcon(ItemStack itemStack) {
        return Icon.of(itemStack)
                .setName(Colorizer.rich(get()))
                .setLore(getLore());
    }

    public Icon toIcon(Material material) {
        return toIcon(new ItemStack(material));
    }

    @Override
    public String toString() {
        return get();
    }

    @Override
    public Localization localization() {
        return LOCALIZATION;
    }

    @Override
    public MessageType type() {
        return type;
    }

    @Override
    public String message(Object... params) {
        return get(params);
    }
}
