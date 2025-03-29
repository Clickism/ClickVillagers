/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import me.clickism.clickvillagers.callback.CooldownManager;
import me.clickism.clickvillagers.util.VersionHelper;
import me.clickism.clickvillagers.villager.VillagerHandler;
import me.clickism.clickvillagers.util.MessageType;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class VillagerClaimGui extends VillagerGui {

    public VillagerClaimGui(ServerPlayerEntity player, VillagerHandler<?> villagerHandler,
                            CooldownManager cooldownManager) {
        super(player, villagerHandler);
        setTitle(Text.literal("🔒 Claim Villager").formatted(Formatting.DARK_GRAY, Formatting.BOLD));
        setSlot(13, new GuiElementBuilder(Items.GOLDEN_SHOVEL)
                .setName(Text.literal("🔒 ").formatted(Formatting.GOLD)
                        .append(Text.literal("CLAIM VILLAGER").formatted(Formatting.GOLD, Formatting.BOLD)))
                //? if >=1.20.5 {
                .hideDefaultTooltip()
                //?} else
                /*.hideFlags()*/
                .addLoreLine(Text.literal("Click to claim this villager.").formatted(Formatting.YELLOW))
                .setCallback((index, type, action, gui) -> {
                    MessageType.CONFIRM.sendSilently(player, Text.literal("You claimed this villager. ").formatted(Formatting.GREEN)
                            .append(Text.literal("Shift + Right Click").formatted(Formatting.WHITE, Formatting.UNDERLINE))
                            .append(Text.literal(" on the villager to edit it.").formatted(Formatting.GREEN)));
                    VersionHelper.playSound(player, SoundEvents.BLOCK_ANVIL_DESTROY, SoundCategory.MASTER, 1, 1);
                    villagerHandler.setOwner(player.getUuid());
                    cooldownManager.giveCooldown(player);
                    new VillagerEditGui(player, villagerHandler).open();
                })
                .build());
    }
}
