/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.gui;

import me.clickism.clickgui.menu.Button;
import me.clickism.clickgui.menu.Icon;
import me.clickism.clickgui.menu.Menu;
import me.clickism.clickgui.menu.MenuType;
import me.clickism.clickvillagers.config.Permission;
import me.clickism.clickvillagers.message.Message;
import me.clickism.clickvillagers.message.MessageType;
import me.clickism.clickvillagers.util.Utils;
import me.clickism.clickvillagers.villager.ClaimManager;
import me.clickism.clickvillagers.villager.PartnerManager;
import me.clickism.clickvillagers.villager.PickupManager;
import me.clickism.clickvillagers.villager.VillagerTextures;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;

import static me.clickism.clickvillagers.message.Message.*;
import static me.clickism.clickvillagers.ClickVillagersConfig.*;

public class VillagerEditMenu extends Menu {
    public VillagerEditMenu(Player viewer, LivingEntity villager, ClaimManager claimManager,
                            PickupManager pickupManager, PartnerManager partnerManager,
                            ChatInputListener chatInputListener) {
        super(viewer, MenuType.MENU_9X3);
        UUID owner = claimManager.getOwnerUUID(villager);
        String ownerName = (owner == null) ? null : Bukkit.getOfflinePlayer(owner).getName();
        setTitle("&2⚒ " + localize(TITLE_EDIT, ownerName == null ? "?" : ownerName));
        setBackground(new VillagerBackground());
        addButton(10, Button.withIcon(BUTTON_PICK_UP_VILLAGER
                        .toIcon(VillagerTextures.getDefaultVillagerItem(villager)))
                .setOnClick((player, view, slot) -> {
                    view.close();
                    if (!hasPermission(player, villager, claimManager)) return;
                    Utils.setHandOrGive(player, pickupManager.toItemStack(villager));
                    PICK_UP_VILLAGER.sendActionbarSilently(player);
                    pickupManager.sendPickupEffect(villager);
                }));
        addButton(12, addPartnerLore(
                Button.withIcon(BUTTON_PARTNER.toIcon(Material.WRITABLE_BOOK))
                        .setOnClick((player, view, slot) -> {
                            view.close();
                            if (Permission.PARTNER.lacksAndNotify(player)) return;
                            Message.ENTER_PARTNER.send(player);
                            chatInputListener.addChatCallback(
                                    player,
                                    input -> handlePartnerInput(input, player, partnerManager),
                                    () -> Message.ENTER_PARTNER_TIMEOUT.send(player),
                                    20 * 10
                            );
                        }),
                partnerManager.getPartners(player.getUniqueId())
        ));
        addButton(13, getTradeButton(villager, claimManager));
        addButton(14, Button.withIcon(BUTTON_REDIRECT_CHANGE_BIOME_MENU
                        .toIcon(Material.BRUSH))
                .setOnClick((player, view, slot) -> {
                    if (!hasPermission(player, villager, claimManager)) return;
                    MessageType.CONFIRM.playSound(player);
                    view.open(new VillagerBiomeChangeMenu(player, villager, view));
                }));
        addButton(16, Button.withIcon(BUTTON_UNCLAIM_VILLAGER.toIcon(Material.BARRIER))
                .setOnClick((player, view, slot) -> {
                    view.close();
                    if (!hasPermission(player, villager, claimManager)) return;
                    claimManager.removeOwner(villager);
                    Message.UNCLAIM_VILLAGER.sendSilently(player);
                    MessageType.FAIL.playSound(player);
                }));
    }

    protected Button getTradeButton(LivingEntity entity, ClaimManager claimManager) {
        Supplier<Icon> iconSupplier = () -> claimManager.isTradeOpen(entity)
                ? BUTTON_TRADE_OPEN.toIcon(Material.EMERALD)
                : BUTTON_TRADE_CLOSED.toIcon(Material.REDSTONE);
        return Button.withIcon(Icon.of(iconSupplier)).setOnClick((player, view, slot) -> {
            boolean tradeOpen = claimManager.isTradeOpen(entity);
            claimManager.setTradeOpen(entity, !tradeOpen);
            if (tradeOpen) {
                MessageType.WARN.playSound(player);
            } else {
                MessageType.CONFIRM.playSound(player);
            }
            view.refresh(slot);
        });
    }

    protected void handlePartnerInput(String input, Player player, PartnerManager partnerManager) {
        UUID uuid = player.getUniqueId();
        if (partnerManager.isPartner(uuid, input)) {
            partnerManager.removePartner(uuid, input);
            PARTNER_REMOVE.send(player, input);
            return;
        }
        if (!isValidPartner(player, input)) {
            Message.INVALID_PARTNER.send(player);
            Message.ENTER_PARTNER_TIMEOUT.sendSilently(player);
            return;
        }
        if (partnerManager.getPartners(uuid).size() > CONFIG.get(PARTNER_LIMIT_PER_PLAYER)
            && Permission.BYPASS_LIMITS.lacks(player)) {
            Message.PARTNER_LIMIT_REACHED.send(player);
            return;
        }
        partnerManager.addPartner(uuid, input);
        PARTNER_ADD.send(player, input);
    }

    @SuppressWarnings("deprecation")
    protected boolean isValidPartner(Player player, String input) {
        if (input.equals(player.getName())) {
            return false;
        }
        if (!CONFIG.get(VALIDATE_PARTNER_NAMES)) {
            return input.length() >= 3 && !input.contains(" ");
        }
        return Bukkit.getOfflinePlayer(input).hasPlayedBefore();
    }

    private Button addPartnerLore(Button button, Collection<String> partners) {
        for (String partner : partners) {
            button.addLoreLine("&7→ &e" + partner);
        }
        return button;
    }

    protected static boolean hasPermission(Player player, LivingEntity villager, ClaimManager claimManager) {
        return claimManager.isOwner(villager, player) || Permission.BYPASS_CLAIMS.has(player);
    }
}
