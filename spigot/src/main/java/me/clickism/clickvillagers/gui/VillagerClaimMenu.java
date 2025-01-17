/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.gui;

import me.clickism.clickgui.menu.Icon;
import me.clickism.clickgui.menu.Menu;
import me.clickism.clickgui.menu.MenuType;
import me.clickism.clickvillagers.message.Message;
import me.clickism.clickvillagers.villager.ClaimManager;
import me.clickism.clickvillagers.villager.PartnerManager;
import me.clickism.clickvillagers.villager.PickupManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class VillagerClaimMenu extends Menu {
    public VillagerClaimMenu(Player viewer, LivingEntity villager, ClaimManager claimManager, PickupManager pickupManager,
                             PartnerManager partnerManager, ChatInputListener chatInputListener) {
        super(viewer, MenuType.MENU_9X3);
        setTitle("&8&l🔒 " + Message.TITLE_CLAIM_VILLAGER);
        setBackground(new VillagerBackground());
        addButton(13, Message.BUTTON_CLAIM_VILLAGER.toButton(Icon.of(Material.GOLDEN_SHOVEL))
                .hideAllAttributes()
                .addEnchantmentGlint()
                .setOnClick((player, view, slot) -> {
                    claimManager.setOwner(villager, player);
                    player.playSound(player, Sound.BLOCK_ANVIL_DESTROY, 1, 1);
                    Message.CLAIM_VILLAGER.sendSilently(player);
                    view.open(new VillagerEditMenu(player, villager, claimManager, pickupManager,
                            partnerManager, chatInputListener));
                }));
    }
}
