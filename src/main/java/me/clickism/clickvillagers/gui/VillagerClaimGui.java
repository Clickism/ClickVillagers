package me.clickism.clickvillagers.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import me.clickism.clickvillagers.VillagerHandler;
import me.clickism.clickvillagers.util.MessageType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.village.VillagerDataContainer;

public class VillagerClaimGui<T extends LivingEntity & VillagerDataContainer> extends VillagerGui<T> {

    public VillagerClaimGui(ServerPlayerEntity player, T villager) {
        super(player, villager);
        setTitle(Text.literal("🔒 Claim Villager").formatted(Formatting.DARK_GRAY, Formatting.BOLD));
        setSlot(13, new GuiElementBuilder(Items.GOLDEN_SHOVEL)
                .setItemName(Text.literal("🔒 ").formatted(Formatting.GOLD)
                        .append(Text.literal("CLAIM VILLAGER").formatted(Formatting.GOLD, Formatting.BOLD)))
                .hideDefaultTooltip()
                .addLoreLine(Text.literal("Click to claim this villager.").formatted(Formatting.YELLOW))
                .setCallback((index, type, action, gui) -> {
                    MessageType.CONFIRM.send(player, Text.literal("You claimed this villager."));
                    new VillagerHandler<>(villager).setOwner(player.getUuid());
                    gui.close();
                })
                .build());
    }
}
