/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.message;

import de.clickism.clickgui.menu.Icon;
import de.clickism.clickvillagers.ClickVillagers;
import de.clickism.configured.localization.Localization;
import de.clickism.configured.localization.LocalizationKey;
import de.clickism.configured.localization.Parameters;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Message implements LocalizationKey {
    // GENERAL
    @Parameters("version")
    UPDATE(MessageType.WARN),
    NO_PERMISSION(MessageType.FAIL),
    ANCHOR_ADD(MessageType.ANCHOR_ADD),
    ANCHOR_REMOVE(MessageType.ANCHOR_REMOVE),
    VILLAGER_HOPPER_PLACE(MessageType.HOPPER_PLACE),
    VILLAGER_HOPPER_BREAK(MessageType.HOPPER_BREAK),
    PICK_UP_VILLAGER(MessageType.PICK_UP),
    ENTER_PARTNER(MessageType.WARN),
    ENTER_PARTNER_TIMEOUT(MessageType.FAIL),
    CLAIM_VILLAGER(MessageType.CONFIRM),
    UNCLAIM_VILLAGER(MessageType.WARN),
    INVALID_PARTNER(MessageType.FAIL),
    @Parameters("partner")
    PARTNER_ADD(MessageType.CONFIRM),
    @Parameters("partner")
    PARTNER_REMOVE(MessageType.WARN),
    @Parameters("limit")
    PARTNER_LIMIT_REACHED(MessageType.FAIL),
    @Parameters("limit")
    HOPPER_LIMIT_REACHED(MessageType.FAIL),
    @Parameters("owner")
    BELONGS_TO(MessageType.FAIL),
    @Parameters("biome")
    BIOME_CHANGED(MessageType.CONFIRM),

    WRITE_ERROR(MessageType.FAIL),
    READ_ERROR(MessageType.FAIL),

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
    USAGE(MessageType.FAIL),

    @Parameters("seconds")
    PICK_UP_COOLDOWN(MessageType.FAIL),
    CLAIM_COOLDOWN(MessageType.FAIL),

    @Parameters({"option", "value"})
    CONFIG_SET(MessageType.CONFIG),
    @Parameters({"option", "value"})
    CONFIG_GET(MessageType.CONFIG),
    @Parameters("path")
    CONFIG_PATH(MessageType.CONFIG),
    CONFIG_RELOAD(MessageType.CONFIG);

    public static final Localization LOCALIZATION =
            Localization.of(lang -> "plugins/ClickVillagers/lang/" + lang + ".json")
                    .resourceProvider(ClickVillagers.class, lang -> "/lang/" + lang + ".json")
                    .fallbackLanguage("en_US")
                    .version(6);

    private final @Nullable MessageType type;

    Message() {
        this(null);
    }

    Message(@Nullable MessageType type) {
        this.type = type;
    }

    public static String localize(String key, Object... params) {
        return LOCALIZATION.get(LocalizationKey.of(key), params);
    }

    public String localized(Object... params) {
        return LOCALIZATION.get(this, params);
    }

    public void send(CommandSender sender, Object... params) {
        getTypeOrDefault().send(sender, localized(params));
    }

    public void sendSilently(CommandSender sender, Object... params) {
        getTypeOrDefault().sendSilently(sender, localized(params));
    }

    public void sendActionbar(CommandSender sender, Object... params) {
        getTypeOrDefault().sendActionbar(sender, localized(params));
    }

    public void sendActionbarSilently(CommandSender sender, Object... params) {
        getTypeOrDefault().sendActionbarSilently(sender, localized(params));
    }

    public List<String> getLore() {
        String pathToLore = name().toLowerCase() + ".lore";
        String text = localize(pathToLore);
        return Arrays.stream(text.split("\n"))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private MessageType getTypeOrDefault() {
        return type != null ? type : MessageType.FAIL;
    }

    public Icon toIcon(ItemStack itemStack) {
        return Icon.of(itemStack)
                .setName(localized())
                .setLore(getLore());
    }

    public Icon toIcon(Material material) {
        return toIcon(new ItemStack(material));
    }

    @Override
    public String toString() {
        return localized();
    }
}
