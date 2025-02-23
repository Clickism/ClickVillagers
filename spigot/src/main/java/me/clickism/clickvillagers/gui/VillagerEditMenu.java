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
import me.clickism.clickvillagers.config.Setting;
import me.clickism.clickvillagers.listener.CooldownManager;
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

public class VillagerEditMenu extends Menu {
    public VillagerEditMenu(Player viewer, LivingEntity villager, ClaimManager claimManager,
                            PickupManager pickupManager, PartnerManager partnerManager,
                            ChatInputListener chatInputListener, CooldownManager cooldownManager) {
        super(viewer, MenuType.MENU_9X3);
        UUID owner = claimManager.getOwnerUUID(villager);
        String ownerName = (owner == null) ? null : Bukkit.getOfflinePlayer(owner).getName();
        setTitle("&2⚒ " + Message.TITLE_EDIT.parameterizer()
                .put("owner", ownerName == null ? "?" : ownerName));
        setBackground(new VillagerBackground());
        addButton(10, Message.BUTTON_PICK_UP_VILLAGER.toButton(Icon.of(VillagerTextures.getDefaultVillagerItem(villager)))
                .setOnClick((player, view, slot) -> {
                    view.close();
                    if (!hasPermission(player, villager, claimManager)) return;
                    if (cooldownManager.hasCooldown(player)) {
                        Message.COOLDOWN.parameterizer()
                                .put("seconds", cooldownManager.getRemainingCooldownSeconds(player))
                                .send(player);
                        return;
                    }
                    cooldownManager.giveCooldown(player);
                    Utils.setHandOrGive(player, pickupManager.toItemStack(villager));
                    Message.PICK_UP_VILLAGER.sendActionbarSilently(player);
                    pickupManager.sendPickupEffect(villager);
                }));
        addButton(12, addPartnerLore(
                Message.BUTTON_PARTNER.toButton(Icon.of(Material.WRITABLE_BOOK))
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
        addButton(14, Message.BUTTON_REDIRECT_CHANGE_BIOME_MENU.toButton(Icon.of(Material.BRUSH))
                .setOnClick((player, view, slot) -> {
                    if (!hasPermission(player, villager, claimManager)) return;
                    MessageType.CONFIRM.playSound(player);
                    view.open(new VillagerBiomeChangeMenu(player, villager, view));
                }));
        addButton(16, Message.BUTTON_UNCLAIM_VILLAGER.toButton(Icon.of(Material.BARRIER))
                .setOnClick((player, view, slot) -> {
                    view.close();
                    if (!hasPermission(player, villager, claimManager)) return;
                    claimManager.removeOwner(villager);
                    Message.UNCLAIM_VILLAGER.sendSilently(player);
                    MessageType.FAIL.playSound(player);
                }));
    }

    protected Button getTradeButton(LivingEntity entity, ClaimManager claimManager) {
        Supplier<Icon> iconSupplier = () -> ((claimManager.isTradeOpen(entity))
                ? Message.BUTTON_TRADE_OPEN.toButton(Icon.of(Material.EMERALD))
                : Message.BUTTON_TRADE_CLOSED.toButton(Icon.of(Material.REDSTONE)))
                .getIcon();
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
            Message.PARTNER_REMOVE.parameterizer()
                    .put("partner", input)
                    .send(player);
            return;
        }
        if (!isValidPartner(player, input)) {
            Message.INVALID_PARTNER.send(player);
            Message.ENTER_PARTNER_TIMEOUT.sendSilently(player);
            return;
        }
        if (partnerManager.getPartners(uuid).size() > Setting.PARTNER_LIMIT_PER_PLAYER.getInt()
            && Permission.BYPASS_LIMITS.lacks(player)) {
            Message.PARTNER_LIMIT_REACHED.send(player);
            return;
        }
        partnerManager.addPartner(uuid, input);
        Message.PARTNER_ADD.parameterizer()
                .put("partner", input)
                .send(player);
    }

    @SuppressWarnings("deprecation")
    protected boolean isValidPartner(Player player, String input) {
        if (input.equals(player.getName())) {
            return false;
        }
        if (Setting.VALIDATE_PARTNER_NAMES.isDisabled()) {
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
