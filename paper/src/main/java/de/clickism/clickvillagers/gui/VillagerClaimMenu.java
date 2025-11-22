/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.gui;

import de.clickism.clickgui.menu.Button;
import de.clickism.clickgui.menu.Menu;
import de.clickism.clickgui.menu.MenuType;
import de.clickism.clickvillagers.listener.CooldownManager;
import de.clickism.clickvillagers.message.Message;
import de.clickism.clickvillagers.villager.ClaimManager;
import de.clickism.clickvillagers.villager.PartnerManager;
import de.clickism.clickvillagers.villager.PickupManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class VillagerClaimMenu extends Menu {
    public VillagerClaimMenu(Player viewer, LivingEntity villager, ClaimManager claimManager,
                             PickupManager pickupManager, PartnerManager partnerManager,
                             ChatInputListener chatInputListener, CooldownManager cooldownManager) {
        super(viewer, MenuType.MENU_9X3);
        setTitle("&8&l🔒 " + Message.TITLE_CLAIM_VILLAGER);
        setBackground(new VillagerBackground());
        addButton(13, Button.withIcon(Message.BUTTON_CLAIM_VILLAGER
                        .toIcon(Material.GOLDEN_SHOVEL))
                .hideAllAttributes()
                .addEnchantmentGlint()
                .setOnClick((player, view, slot) -> {
                    claimManager.setOwner(villager, player);
                    cooldownManager.giveCooldown(player);
                    player.playSound(player, Sound.BLOCK_ANVIL_DESTROY, 1, 1);
                    Message.CLAIM_VILLAGER.sendSilently(player);
                    view.open(new VillagerEditMenu(player, villager, claimManager, pickupManager,
                            partnerManager, chatInputListener));
                }));
    }
}
