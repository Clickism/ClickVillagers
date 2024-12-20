package me.clickism.clickvillagers.gui;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import eu.pb4.sgui.api.gui.GuiInterface;
import me.clickism.clickvillagers.PartnerState;
import me.clickism.clickvillagers.VillagerHandler;
import me.clickism.clickvillagers.util.MessageType;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.util.UUID;

public class VillagerPartnerGui extends AnvilInputGui {
    private final VillagerHandler<?> villagerHandler;
    private final GuiInterface previous;
    private final MinecraftServer server;

    public VillagerPartnerGui(ServerPlayerEntity player, VillagerHandler<?> villagerHandler, GuiInterface previous) {
        super(player, false);
        this.villagerHandler = villagerHandler;
        this.previous = previous;
        this.server = player.getServer();
        if (this.server == null) throw new IllegalStateException("Server is null");
        setTitle(Text.literal("✍ ").formatted(Formatting.DARK_GRAY)
                .append(Text.literal("Add Partner").formatted(Formatting.DARK_GRAY, Formatting.BOLD)));
        setSlot(2, getConfirmButton(getInput()));
    }

    @Override
    public void onInput(String input) {
        setSlot(2, getConfirmButton(input));
    }

    private GuiElement getConfirmButton(String input) {
        GuiElementBuilder builder = new GuiElementBuilder(Items.ANVIL)
                .setItemName(Text.literal("✍ ").formatted(Formatting.WHITE)
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
                        partnerState.addPartner(uuid, input);
                        MessageType.CONFIRM.send(player, Text.literal("Added " + input + " to your trading partners."));
                    }
                });
        if (isPartner(input)) {
            builder
                    .setItem(Items.BARRIER)
                    .setItemName(Text.literal("✍ ").formatted(Formatting.DARK_RED)
                            .append(Text.literal("REMOVE PARTNER").formatted(Formatting.DARK_RED, Formatting.BOLD)))
                    .addLoreLine(Text.literal("Click to remove \"" + input + "\" from your trading partners.").formatted(Formatting.RED));
        } else {
            builder
                    .setItemName(Text.literal("✍ ").formatted(Formatting.DARK_GREEN)
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
