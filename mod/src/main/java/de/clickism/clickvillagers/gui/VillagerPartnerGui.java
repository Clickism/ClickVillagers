/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.gui;

import de.clickism.clickvillagers.util.VersionHelper;
import de.clickism.fgui.api.elements.GuiElement;
import de.clickism.fgui.api.elements.GuiElementBuilder;
import de.clickism.fgui.api.gui.AnvilInputGui;
import de.clickism.clickvillagers.villager.PartnerState;
import de.clickism.clickvillagers.util.MessageType;
import net.minecraft.world.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import java.util.UUID;

import static de.clickism.clickvillagers.ClickVillagersConfig.*;

public class VillagerPartnerGui extends AnvilInputGui {
    private final MinecraftServer server;
    private final VillagerGui previous;

    public VillagerPartnerGui(ServerPlayer player, VillagerGui previous) {
        super(player, false);
        this.server = VersionHelper.getServer(player);
        this.previous = previous;
        if (this.server == null) throw new IllegalStateException("Server is null");
        setTitle(Component.literal("✍ ").withStyle(ChatFormatting.DARK_GRAY)
                .append(Component.literal("Add Partner").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.BOLD)));
        setSlot(2, getConfirmButton(getInput()));
        setSlot(1, new BackButton(previous));
        setSlot(0, new GuiElementBuilder(Items.PLAYER_HEAD).build());
        setDefaultInputValue("");
    }

    @Override
    public void onInput(String input) {
        setSlot(2, getConfirmButton(input));
    }

    private GuiElement getConfirmButton(String input) {
        GuiElementBuilder builder = new GuiElementBuilder(Items.ANVIL)
                .setName(Component.literal("✍ ").withStyle(ChatFormatting.WHITE)
                        .append(Component.literal("ADD PARTNER").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD)))
                .setCallback((index, type, action, gui) -> {
                    if (!isValid(input)) {
                        MessageType.FAIL.playSound(player);
                        return;
                    }
                    PartnerState partnerState = PartnerState.getServerState(server);
                    UUID uuid = player.getUUID();
                    if (partnerState.isPartner(uuid, input)) {
                        partnerState.removePartner(uuid, input);
                        MessageType.WARN.send(player, Component.literal("Removed " + input + " from your trading partners."));
                    } else {
                        int limit = PARTNER_LIMIT_PER_PLAYER.get();
                        if (partnerState.getPartners(uuid).size() >= limit) {
                            MessageType.FAIL.send(player, Component.literal("You have reached the partner limit: ")
                                    .append(Component.literal(String.valueOf(limit)).withStyle(ChatFormatting.BOLD)));
                            return;
                        }
                        partnerState.addPartner(uuid, input);
                        MessageType.CONFIRM.send(player, Component.literal("Added " + input + " to your trading partners."));
                    }
                    new VillagerEditGui(player, previous.villagerHandler).open();
                });
        if (isPartner(input)) {
            builder
                    .setItem(Items.BARRIER)
                    .setName(Component.literal("✍ ").withStyle(ChatFormatting.DARK_RED)
                            .append(Component.literal("REMOVE PARTNER").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD)))
                    .addLoreLine(Component.literal("Click to remove \"" + input + "\" from your trading partners.").withStyle(ChatFormatting.RED));
        } else {
            builder
                    .setName(Component.literal("✍ ").withStyle(ChatFormatting.DARK_GREEN)
                            .append(Component.literal("ADD PARTNER").withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD)))
                    .addLoreLine(Component.literal("Click to add \"" + input + "\" to your trading partners.").withStyle(ChatFormatting.GREEN));
        }
        return builder.build();
    }

    private boolean isPartner(String input) {
        PartnerState partnerState = PartnerState.getServerState(server);
        return partnerState.getPartners(player.getUUID()).contains(input);
    }

    private boolean isValid(String input) {
        return input.length() > 2 && !input.contains(" ");
    }
}
