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
import net.minecraft.world.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@SuppressWarnings("deprecation")
public class VillagerEditGui extends VillagerGui {
    public VillagerEditGui(ServerPlayer player, VillagerHandler<?> villagerHandler) {
        super(player, villagerHandler);
        String ownerName = getOwnerName(VersionHelper.getServer(player), villagerHandler);
        setTitle(Component.literal("⚒ ").withStyle(ChatFormatting.DARK_GREEN)
                .append(Component.literal(ownerName).withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD))
                .append(Component.literal("'s Villager").withStyle(ChatFormatting.DARK_GREEN)));
        setSlot(14, new GuiElementBuilder(Items.BRUSH)
                .setName(Component.literal("🌲 ").withStyle(ChatFormatting.GOLD)
                        .append(Component.literal("CHANGE BIOME").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)))
                .addLoreLine(Component.literal("Click to change the villager's biome.").withStyle(ChatFormatting.YELLOW))
                .setCallback((index, type, action, gui) -> {
                    MessageType.CONFIRM.playSound(player);
                    new VillagerBiomeChangeGui(player, villagerHandler, this).open();
                })
                .build());
        setSlot(10, new GuiElementBuilder(Items.PLAYER_HEAD)
                .setName(Component.literal("↑ ").withStyle(ChatFormatting.GOLD)
                        .append(Component.literal("PICK UP VILLAGER").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                                .append(Component.literal(" ↑").withStyle(ChatFormatting.GOLD))))
                .addLoreLine(Component.literal("Click to pick up the villager.").withStyle(ChatFormatting.YELLOW))
                .setSkullOwner(VillagerTextures.DEFAULT_TEXTURE)
                .setCallback((index, type, action, gui) -> {
                    PickupHandler.notifyPickup(player, villagerHandler.getEntity());
                    Utils.offerToHand(player, PickupHandler.toItemStack(villagerHandler.getEntity()));
                    gui.close();
                })
                .build());
        setSlot(16, new GuiElementBuilder(Items.BARRIER)
                .setName(Component.literal("🔓 ").withStyle(ChatFormatting.DARK_RED)
                        .append(Component.literal("UNCLAIM VILLAGER").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD)))
                .addLoreLine(Component.literal("Click to unclaim this villager.").withStyle(ChatFormatting.RED, ChatFormatting.BOLD))
                .addLoreLine(Component.literal("Unclaimed villagers can be picked up by anyone.").withStyle(ChatFormatting.RED))
                .setCallback((index, type, action, gui) -> {
                    MessageType.WARN.send(player, Component.literal("You unclaimed this villager."));
                    villagerHandler.setOwner(null);
                    gui.close();
                })
                .build());
        setSlot(13, getTradeOpenButton(villagerHandler));
        setSlot(12, addTradePartnersLore(new GuiElementBuilder(Items.WRITABLE_BOOK)
                .setName(Component.literal("✍ ").withStyle(ChatFormatting.GOLD)
                        .append(Component.literal("ADD TRADING PARTNER").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD)))
                .addLoreLine(Component.literal("Click to add/remove a trading partner.").withStyle(ChatFormatting.GRAY))
                .addLoreLine(Component.literal("Trading partners can trade with all of your villagers.").withStyle(ChatFormatting.GRAY))
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
                    .setName(Component.literal("$ ").withStyle(ChatFormatting.DARK_GREEN)
                            .append(Component.literal("TRADING OPEN").withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD)))
                    .addLoreLine(Component.literal("Everyone can trade with this villager.").withStyle(ChatFormatting.GREEN));

        } else {
            builder
                    .setItem(Items.REDSTONE)
                    .setName(Component.literal("❌ ").withStyle(ChatFormatting.DARK_RED)
                            .append(Component.literal("TRADING CLOSED").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD)))
                    .addLoreLine(Component.literal("Only you and you trading partners").withStyle(ChatFormatting.RED))
                    .addLoreLine(Component.literal("can trade with this villager.").withStyle(ChatFormatting.RED));
        }
        return builder.build();
    }

    private GuiElementBuilder addTradePartnersLore(GuiElementBuilder builder) {
        PartnerState partnerState = PartnerState.getServerState(VersionHelper.getServer(player));
        Set<String> partners = partnerState.getPartners(player.getUUID());
        for (String partner : partners) {
            builder.addLoreLine(Component.literal("→ ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(partner).withStyle(ChatFormatting.YELLOW)));
        }
        return builder;
    }

    private static String getOwnerName(@Nullable MinecraftServer server, VillagerHandler<?> villagerHandler) {
        if (server == null) return "?";
        return VersionHelper.getPlayerName(villagerHandler.getOwner(), server)
                .orElse("?");
    }
}
