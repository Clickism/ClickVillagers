package me.clickism.clickvillagers.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import me.clickism.clickvillagers.PickupHandler;
import me.clickism.clickvillagers.VillagerTextures;
import me.clickism.clickvillagers.util.MessageType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.village.VillagerDataContainer;

public class VillagerEditMenu<T extends LivingEntity & VillagerDataContainer> extends VillagerGui<T> {
    public VillagerEditMenu(ServerPlayerEntity player, T villager) {
        super(player, villager);
        setTitle(Text.literal("⚒ ").formatted(Formatting.DARK_GREEN)
                .append(Text.literal(player.getName().getString()).formatted(Formatting.DARK_GREEN, Formatting.BOLD))
                .append(Text.literal("'s Villager").formatted(Formatting.GREEN)));
        setSlot(14, new GuiElementBuilder(Items.BRUSH)
                .setItemName(Text.literal("🌲 ").formatted(Formatting.GOLD)
                        .append(Text.literal("CHANGE BIOME").formatted(Formatting.GOLD, Formatting.BOLD)))
                .addLoreLine(Text.literal("Click to change the villager's biome.").formatted(Formatting.YELLOW))
                .hideDefaultTooltip()
                .setCallback((index, type, action, gui) -> {
                    MessageType.CONFIRM.playSound(player);
                    new VillagerBiomeChangeMenu<>(player, villager, this).open();
                })
                .build());
        setSlot(10, new GuiElementBuilder(Items.PLAYER_HEAD)
                .setItemName(Text.literal("↑ ").formatted(Formatting.GOLD)
                        .append(Text.literal("PICK UP VILLAGER").formatted(Formatting.GOLD, Formatting.BOLD)
                                .append(Text.literal(" ↑").formatted(Formatting.GOLD))))
                .addLoreLine(Text.literal("Click to pick up the villager.").formatted(Formatting.YELLOW))
                .setSkullOwner(VillagerTextures.DEFAULT_TEXTURE)
                .setCallback((index, type, action, gui) -> {
                    MessageType.CONFIRM.playSound(player);
                    player.getInventory().offerOrDrop(PickupHandler.toItemStack(villager));
                    gui.close();
                })
                .build());
    }
}
