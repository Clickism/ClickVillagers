/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.gui;

import de.clickism.clickvillagers.util.VersionHelper;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import de.clickism.clickvillagers.villager.PartnerState;
import de.clickism.clickvillagers.util.MessageType;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.util.UUID;

import static de.clickism.clickvillagers.ClickVillagersConfig.*;

public class VillagerPartnerGui extends AnvilInputGui {
    private final MinecraftServer server;
    private final VillagerGui previous;

    public VillagerPartnerGui(ServerPlayerEntity player, VillagerGui previous) {
        super(player, false);
        this.server = VersionHelper.getServer(player);
        this.previous = previous;
        if (this.server == null) throw new IllegalStateException("Server is null");
        setTitle(Text.literal("✍ ").formatted(Formatting.DARK_GRAY)
                .append(Text.literal("Add Partner").formatted(Formatting.DARK_GRAY, Formatting.BOLD)));
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
                .setName(Text.literal("✍ ").formatted(Formatting.WHITE)
                        .append(Text.literal("ADD PARTNER").formatted(Formatting.WHITE, Formatting.BOLD)))
                .setCallback((index, type, action, gui) -> {
                    if (!isValid(input)) {
                        MessageType.FAIL.playSound(player);
                        return;
                    }
                    PartnerState partnerState = PartnerState.getServerState(server);
                    UUID uuid = player.getUuid();
                    if (partnerState.isPartner(uuid, input)) {
                        partnerState.removePartner(uuid, input);
                        MessageType.WARN.send(player, Text.literal("Removed " + input + " from your trading partners."));
                    } else {
                        int limit = PARTNER_LIMIT_PER_PLAYER.get();
                        if (partnerState.getPartners(uuid).size() >= limit) {
                            MessageType.FAIL.send(player, Text.literal("You have reached the partner limit: ")
                                    .append(Text.literal(String.valueOf(limit)).formatted(Formatting.BOLD)));
                            return;
                        }
                        partnerState.addPartner(uuid, input);
                        MessageType.CONFIRM.send(player, Text.literal("Added " + input + " to your trading partners."));
                    }
                    new VillagerEditGui(player, previous.villagerHandler).open();
                });
        if (isPartner(input)) {
            builder
                    .setItem(Items.BARRIER)
                    .setName(Text.literal("✍ ").formatted(Formatting.DARK_RED)
                            .append(Text.literal("REMOVE PARTNER").formatted(Formatting.DARK_RED, Formatting.BOLD)))
                    .addLoreLine(Text.literal("Click to remove \"" + input + "\" from your trading partners.").formatted(Formatting.RED));
        } else {
            builder
                    .setName(Text.literal("✍ ").formatted(Formatting.DARK_GREEN)
                            .append(Text.literal("ADD PARTNER").formatted(Formatting.DARK_GREEN, Formatting.BOLD)))
                    .addLoreLine(Text.literal("Click to add \"" + input + "\" to your trading partners.").formatted(Formatting.GREEN));
        }
        return builder.build();
    }

    private boolean isPartner(String input) {
        PartnerState partnerState = PartnerState.getServerState(server);
        return partnerState.getPartners(player.getUuid()).contains(input);
    }

    private boolean isValid(String input) {
        return input.length() > 2 && !input.contains(" ");
    }
}
