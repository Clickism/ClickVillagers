/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.message;

import me.clickism.clickgui.menu.Button;
import me.clickism.clickgui.menu.Icon;
import me.clickism.clickvillagers.config.Setting;
import me.clickism.clickvillagers.util.MessageParameterizer;
import me.clickism.clickvillagers.util.Parameterizer;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public enum Message {

    // GENERAL
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
    @WithParameters("partner")
    PARTNER_ADD(MessageType.CONFIRM),
    @WithParameters("partner")
    PARTNER_REMOVE(MessageType.WARN),
    @WithParameters("limit")
    PARTNER_LIMIT_REACHED(MessageType.FAIL),
    @WithParameters("limit")
    HOPPER_LIMIT_REACHED(MessageType.FAIL),
    @WithParameters("owner")
    BELONGS_TO(MessageType.FAIL),
    @WithParameters("biome")
    BIOME_CHANGED(MessageType.CONFIRM),

    WRITE_ERROR(MessageType.FAIL),
    READ_ERROR(MessageType.FAIL),

    INFO_OWNER,
    INFO_ANCHORED,
    INFO_TRADE_CLOSED,
    INFO_TRADES,

    // ITEMS
    VILLAGER,
    BABY_VILLAGER,

    VILLAGER_HOPPER,

    // TITLES
    TITLE_CLAIM_VILLAGER,
    @WithParameters("owner")
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

    // COMMANDS
    @WithParameters("usage")
    USAGE(MessageType.FAIL),
    RELOAD_SUCCESS(MessageType.CONFIRM),
    RELOAD_FAIL(MessageType.FAIL),

    @WithParameters("seconds")
    COOLDOWN(MessageType.FAIL);

    private static final MessageType MISSING = MessageType.silent("&2[?] &c", "&8< &2? &c%s &8>");

    @Nullable
    private static MessageManager messageManager;

    private final String path;
    private final MessageType type;

    Message() {
        this(null);
    }

    Message(MessageType type) {
        this.type = type;
        this.path = name().toLowerCase();
    }

    public void send(CommandSender player) {
        getTypeOrDefault().send(player, toString());
    }

    public void sendSilently(CommandSender player) {
        getTypeOrDefault().sendSilently(player, toString());
    }

    public void sendActionbar(CommandSender player) {
        getTypeOrDefault().sendActionbar(player, toString());
    }

    public void sendActionbarSilently(CommandSender player) {
        getTypeOrDefault().sendActionbarSilently(player, toString());
    }

    @Override
    public String toString() {
        return get(path);
    }

    public Icon toIcon(Material material) {
        return Icon.of(material)
                .setName(toString())
                .setLore(getLore());
    }

    public Icon toIcon(Material material, Parameterizer parameterizer) {
        return Icon.of(material)
                .setName(parameterizer.replace(toString()))
                .setLore(getParameterizedLore(parameterizer));
    }

    public Button toButton(Icon icon) {
        return Button.withIcon(icon)
                .setName(toString())
                .setLore(getLore());
    }

    public Button toButton(Icon icon, Parameterizer parameterizer) {
        return Button.withIcon(icon)
                .setName(parameterizer.replace(toString()))
                .setLore(getParameterizedLore(parameterizer));
    }

    public List<String> getLore() {
        if (messageManager == null) return List.of(path);
        return messageManager.getLore(path);
    }

    public List<String> getParameterizedLore(Parameterizer parameterizer) {
        return getLore().stream()
                .map(parameterizer::replace)
                .collect(Collectors.toList());
    }

    public MessageType getTypeOrDefault() {
        return type != null ? type : MISSING;
    }

    public MessageParameterizer parameterizer() {
        return new MessageParameterizer(this);
    }

    public static void initialize(JavaPlugin plugin) throws IOException {
        if (messageManager != null) return;
        Setting.initialize(plugin);
        messageManager = new MessageManager(plugin, Setting.LANGUAGE.getString());
    }
    
    @NotNull
    public static String get(String key) {
        if (messageManager == null) return key;
        return messageManager.getOrPath(key);
    }
}
