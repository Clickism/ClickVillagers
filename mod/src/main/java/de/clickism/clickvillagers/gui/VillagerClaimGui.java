/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.gui;

import de.clickism.fgui.api.elements.GuiElementBuilder;
import de.clickism.clickvillagers.util.CooldownManager;
import de.clickism.clickvillagers.util.VersionHelper;
import de.clickism.clickvillagers.villager.VillagerHandler;
import de.clickism.clickvillagers.util.MessageType;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class VillagerClaimGui extends VillagerGui {

    public VillagerClaimGui(ServerPlayer player, VillagerHandler<?> villagerHandler,
                            CooldownManager cooldownManager) {
        super(player, villagerHandler);
        setTitle(Component.literal("🔒 Claim Villager").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.BOLD));
        setSlot(13, new GuiElementBuilder(Items.GOLDEN_SHOVEL)
                .setName(Component.literal("🔒 ").withStyle(ChatFormatting.GOLD)
                        .append(Component.literal("CLAIM VILLAGER").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)))
                .hideDefaultTooltip()
                .addLoreLine(Component.literal("Click to claim this villager.").withStyle(ChatFormatting.YELLOW))
                .setCallback((index, type, action, gui) -> {
                    MessageType.CONFIRM.sendSilently(player, Component.literal("You claimed this villager. ").withStyle(ChatFormatting.GREEN)
                            .append(Component.literal("Shift + Right Click").withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE))
                            .append(Component.literal(" on the villager to edit it.").withStyle(ChatFormatting.GREEN)));
                    VersionHelper.playSound(player, SoundEvents.ANVIL_DESTROY, SoundSource.MASTER, 1, 1);
                    villagerHandler.setOwner(player.getUUID());
                    cooldownManager.giveCooldown(player);
                    new VillagerEditGui(player, villagerHandler).open();
                })
                .build());
    }
}
