/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.gui;

import de.clickism.linen.core.Linen;
import de.clickism.fgui.api.elements.GuiElementBuilder;
import de.clickism.clickvillagers.callback.CooldownManager;
import de.clickism.clickvillagers.util.VersionHelper;
import de.clickism.clickvillagers.villager.VillagerHandler;
import de.clickism.linen.core.message.MessageType;
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
                    MessageType.SUCCESS.sendSilently(
                            Linen.player(player),
                            "You claimed this villager. <white><underlined>Shift + Right Click</underlined></white> on the villager to edit it."
                    );
                    VersionHelper.playSound(player, SoundEvents.BLOCK_ANVIL_DESTROY, SoundCategory.MASTER, 1, 1);
                    villagerHandler.setOwner(player.getUuid());
                    cooldownManager.giveCooldown(player);
                    new VillagerEditGui(player, villagerHandler).open();
                })
                .build());
    }
}
