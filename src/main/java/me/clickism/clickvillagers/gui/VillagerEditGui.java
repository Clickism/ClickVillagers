package me.clickism.clickvillagers.gui;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import me.clickism.clickvillagers.PartnerState;
import me.clickism.clickvillagers.PickupHandler;
import me.clickism.clickvillagers.VillagerHandler;
import me.clickism.clickvillagers.VillagerTextures;
import me.clickism.clickvillagers.util.MessageType;
import me.clickism.clickvillagers.util.Utils;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Set;

public class VillagerEditGui extends VillagerGui {
    public VillagerEditGui(ServerPlayerEntity player, VillagerHandler<?> villagerHandler) {
        super(player, villagerHandler);
        setTitle(Text.literal("⚒ ").formatted(Formatting.DARK_GREEN)
                .append(Text.literal(player.getName().getString()).formatted(Formatting.DARK_GREEN, Formatting.BOLD))
                .append(Text.literal("'s Villager").formatted(Formatting.DARK_GREEN)));
        setSlot(14, new GuiElementBuilder(Items.BRUSH)
                .setItemName(Text.literal("🌲 ").formatted(Formatting.GOLD)
                        .append(Text.literal("CHANGE BIOME").formatted(Formatting.GOLD, Formatting.BOLD)))
                .addLoreLine(Text.literal("Click to change the villager's biome.").formatted(Formatting.YELLOW))
                .hideDefaultTooltip()
                .setCallback((index, type, action, gui) -> {
                    MessageType.CONFIRM.playSound(player);
                    new VillagerBiomeChangeGui(player, villagerHandler, this).open();
                })
                .build());
        setSlot(10, new GuiElementBuilder(Items.PLAYER_HEAD)
                .setItemName(Text.literal("↑ ").formatted(Formatting.GOLD)
                        .append(Text.literal("PICK UP VILLAGER").formatted(Formatting.GOLD, Formatting.BOLD)
                                .append(Text.literal(" ↑").formatted(Formatting.GOLD))))
                .addLoreLine(Text.literal("Click to pick up the villager.").formatted(Formatting.YELLOW))
                .setSkullOwner(VillagerTextures.DEFAULT_TEXTURE)
                .setCallback((index, type, action, gui) -> {
                    PickupHandler.notifyPickup(player);
                    Utils.offerToHand(player, PickupHandler.toItemStack(villagerHandler.getEntity()));
                    gui.close();
                })
                .build());
        setSlot(16, new GuiElementBuilder(Items.BARRIER)
                .setItemName(Text.literal("🔓 ").formatted(Formatting.DARK_RED)
                        .append(Text.literal("UNCLAIM VILLAGER").formatted(Formatting.DARK_RED, Formatting.BOLD)))
                .addLoreLine(Text.literal("Click to unclaim this villager.").formatted(Formatting.RED, Formatting.BOLD))
                .addLoreLine(Text.literal("Unclaimed villagers can be picked up by anyone.").formatted(Formatting.RED))
                .hideDefaultTooltip()
                .setCallback((index, type, action, gui) -> {
                    MessageType.WARN.send(player, Text.literal("You unclaimed this villager."));
                    villagerHandler.setOwner(null);
                    gui.close();
                })
                .build());
        setSlot(13, getTradeOpenButton(villagerHandler));
        setSlot(12, addTradePartnersLore(new GuiElementBuilder(Items.WRITABLE_BOOK)
                .setItemName(Text.literal("✍ ").formatted(Formatting.GOLD)
                        .append(Text.literal("ADD TRADING PARTNER").formatted(Formatting.WHITE, Formatting.BOLD)))
                .addLoreLine(Text.literal("Click to add/remove a trading partner.").formatted(Formatting.GRAY))
                .addLoreLine(Text.literal("Trading partners can trade with all of your villagers.").formatted(Formatting.GRAY))
                .hideDefaultTooltip()
                .setCallback((index, type, action, gui) -> {
                    MessageType.CONFIRM.playSound(player);
                    new VillagerPartnerGui(player, villagerHandler, this).open();
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
                    .setItemName(Text.literal("$ ").formatted(Formatting.DARK_GREEN)
                            .append(Text.literal("TRADING OPEN").formatted(Formatting.DARK_GREEN, Formatting.BOLD)))
                    .addLoreLine(Text.literal("Everyone can trade with this villager.").formatted(Formatting.GREEN));

        } else {
            builder
                    .setItem(Items.REDSTONE)
                    .setItemName(Text.literal("❌ ").formatted(Formatting.DARK_RED)
                            .append(Text.literal("TRADING CLOSED").formatted(Formatting.DARK_RED, Formatting.BOLD)))
                    .addLoreLine(Text.literal("Only you can trade with this villager.").formatted(Formatting.RED));
        }
        return builder.build();
    }

    private GuiElementBuilder addTradePartnersLore(GuiElementBuilder builder) {
        @SuppressWarnings("DataFlowIssue")
        PartnerState partnerState = PartnerState.getServerState(player.getServer());
        Set<String> partners = partnerState.getPartners(player.getUuid());
        for (String partner : partners) {
            builder.addLoreLine(Text.literal("→ ").formatted(Formatting.GRAY)
                    .append(Text.literal(partner).formatted(Formatting.YELLOW)));
        }
        return builder;
    }
}
