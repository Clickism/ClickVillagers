/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.gui;

import de.clickism.clickvillagers.util.MessageType;
import de.clickism.clickvillagers.util.Utils;
import de.clickism.clickvillagers.util.VersionHelper;
import de.clickism.clickvillagers.villager.PartnerState;
import de.clickism.clickvillagers.villager.PickupHandler;
import de.clickism.clickvillagers.villager.VillagerHandler;
import de.clickism.clickvillagers.villager.VillagerTextures;
import de.clickism.fgui.api.elements.GuiElement;
import de.clickism.fgui.api.elements.GuiElementBuilder;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class VillagerEditGui extends VillagerGui {
    public VillagerEditGui(ServerPlayerEntity player, VillagerHandler<?> villagerHandler) {
        super(player, villagerHandler);
        String ownerName = getOwnerName(VersionHelper.getServer(player), villagerHandler);
        setTitle(Text.literal("⚒ ").formatted(Formatting.DARK_GREEN)
                .append(Text.literal(ownerName).formatted(Formatting.DARK_GREEN, Formatting.BOLD))
                .append(Text.literal("'s Villager").formatted(Formatting.DARK_GREEN)));
        setSlot(14, new GuiElementBuilder(Items.BRUSH)
                .setName(Text.literal("🌲 ").formatted(Formatting.GOLD)
                        .append(Text.literal("CHANGE BIOME").formatted(Formatting.GOLD, Formatting.BOLD)))
                .addLoreLine(Text.literal("Click to change the villager's biome.").formatted(Formatting.YELLOW))
                .setCallback((index, type, action, gui) -> {
                    MessageType.CONFIRM.playSound(player);
                    new VillagerBiomeChangeGui(player, villagerHandler, this).open();
                })
                .build());
        setSlot(10, new GuiElementBuilder(Items.PLAYER_HEAD)
                .setName(Text.literal("↑ ").formatted(Formatting.GOLD)
                        .append(Text.literal("PICK UP VILLAGER").formatted(Formatting.GOLD, Formatting.BOLD)
                                .append(Text.literal(" ↑").formatted(Formatting.GOLD))))
                .addLoreLine(Text.literal("Click to pick up the villager.").formatted(Formatting.YELLOW))
                .setSkullOwner(VillagerTextures.DEFAULT_TEXTURE)
                .setCallback((index, type, action, gui) -> {
                    PickupHandler.notifyPickup(player, villagerHandler.getEntity());
                    Utils.offerToHand(player, PickupHandler.toItemStack(villagerHandler.getEntity()));
                    gui.close();
                })
                .build());
        setSlot(16, new GuiElementBuilder(Items.BARRIER)
                .setName(Text.literal("🔓 ").formatted(Formatting.DARK_RED)
                        .append(Text.literal("UNCLAIM VILLAGER").formatted(Formatting.DARK_RED, Formatting.BOLD)))
                .addLoreLine(Text.literal("Click to unclaim this villager.").formatted(Formatting.RED, Formatting.BOLD))
                .addLoreLine(Text.literal("Unclaimed villagers can be picked up by anyone.").formatted(Formatting.RED))
                .setCallback((index, type, action, gui) -> {
                    MessageType.WARN.send(player, Text.literal("You unclaimed this villager."));
                    villagerHandler.setOwner(null);
                    gui.close();
                })
                .build());
        setSlot(13, getTradeOpenButton(villagerHandler));
        setSlot(12, addTradePartnersLore(new GuiElementBuilder(Items.WRITABLE_BOOK)
                .setName(Text.literal("✍ ").formatted(Formatting.GOLD)
                        .append(Text.literal("ADD TRADING PARTNER").formatted(Formatting.WHITE, Formatting.BOLD)))
                .addLoreLine(Text.literal("Click to add/remove a trading partner.").formatted(Formatting.GRAY))
                .addLoreLine(Text.literal("Trading partners can trade with all of your villagers.").formatted(Formatting.GRAY))
                .setCallback((index, type, action, gui) -> {
                    MessageType.CONFIRM.playSound(player);
                    new VillagerPartnerGui(player, this).open();
                }))
                .build());
    }

    private GuiElement getTradeOpenButton(VillagerHandler<?> villagerHandler) {
        GuiElementBuilder builder = new GuiElementBuilder(Items.EMERALD)
                .setCallback((index, type, action, gui) -> {
                    if (villagerHandler.isTradingOpen()) {
                        villagerHandler.setTradingOpen(false);
                        MessageType.WARN.playSound(player);
                    } else {
                        villagerHandler.setTradingOpen(true);
                        MessageType.CONFIRM.playSound(player);
                    }
                    gui.setSlot(index, getTradeOpenButton(villagerHandler));
                });
        if (villagerHandler.isTradingOpen()) {
            builder
                    .setName(Text.literal("$ ").formatted(Formatting.DARK_GREEN)
                            .append(Text.literal("TRADING OPEN").formatted(Formatting.DARK_GREEN, Formatting.BOLD)))
                    .addLoreLine(Text.literal("Everyone can trade with this villager.").formatted(Formatting.GREEN));

        } else {
            builder
                    .setItem(Items.REDSTONE)
                    .setName(Text.literal("❌ ").formatted(Formatting.DARK_RED)
                            .append(Text.literal("TRADING CLOSED").formatted(Formatting.DARK_RED, Formatting.BOLD)))
                    .addLoreLine(Text.literal("Only you and you trading partners").formatted(Formatting.RED))
                    .addLoreLine(Text.literal("can trade with this villager.").formatted(Formatting.RED));
        }
        return builder.build();
    }

    private GuiElementBuilder addTradePartnersLore(GuiElementBuilder builder) {
        PartnerState partnerState = PartnerState.getServerState(VersionHelper.getServer(player));
        Set<String> partners = partnerState.getPartners(player.getUuid());
        for (String partner : partners) {
            builder.addLoreLine(Text.literal("→ ").formatted(Formatting.GRAY)
                    .append(Text.literal(partner).formatted(Formatting.YELLOW)));
        }
        return builder;
    }

    private static String getOwnerName(@Nullable MinecraftServer server, VillagerHandler<?> villagerHandler) {
        if (server == null) return "?";
        return VersionHelper.getPlayerName(villagerHandler.getOwner(), server)
                .orElse("?");
    }
}
