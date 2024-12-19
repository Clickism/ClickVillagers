package me.clickism.clickvillagers.gui;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.GuiInterface;
import me.clickism.clickvillagers.util.MessageType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerType;
import java.util.List;

public class VillagerBiomeChangeMenu<T extends LivingEntity & VillagerDataContainer> extends VillagerGui<T> {

    private record VillagerBiome(VillagerType type, Item icon) {
    }
    
    private static final List<VillagerBiome> BIOMES = List.of(
            new VillagerBiome(VillagerType.PLAINS, Items.OAK_SAPLING),
            new VillagerBiome(VillagerType.DESERT, Items.DEAD_BUSH),
            new VillagerBiome(VillagerType.JUNGLE, Items.JUNGLE_SAPLING),
            new VillagerBiome(VillagerType.SNOW, Items.FERN),
            new VillagerBiome(VillagerType.SAVANNA, Items.ACACIA_SAPLING),
            new VillagerBiome(VillagerType.SWAMP, Items.BLUE_ORCHID),
            new VillagerBiome(VillagerType.TAIGA, Items.SPRUCE_SAPLING)
    );
    
    public VillagerBiomeChangeMenu(ServerPlayerEntity player, T villager, GuiInterface previous) {
        super(player, villager);
        setSlot(18, new BackButton(previous));
        setTitle(Text.literal("🌲 ").formatted(Formatting.DARK_GRAY)
                .append(Text.literal("Choose Villager's Biome").formatted(Formatting.DARK_GRAY, Formatting.BOLD)));
        int i = 10;
        for (VillagerBiome biome : BIOMES) {
            setSlot(i, getBiomeButton(biome.type, biome.icon, previous));
            i++;
        }
    }
    
    private GuiElement getBiomeButton(VillagerType type, Item icon, GuiInterface previous) {
        GuiElementBuilder builder = new GuiElementBuilder(icon)
                .setItemName(Text.literal(type.toString().toUpperCase()).formatted(Formatting.GREEN, Formatting.BOLD))
                .addLoreLine(Text.literal("Click to change the villager's biome.").formatted(Formatting.DARK_GREEN))
                .setCallback((index, t, action, gui) -> {
                    if (villager.isRemoved()) return;
                    villager.setVillagerData(villager.getVillagerData().withType(type));
                    MessageType.CONFIRM.send(player, Text.literal("You changed the villager's biome to " + type + "."));
                    new VillagerBiomeChangeMenu<>(player, villager, previous).open();
                });
        if (villager.getVillagerData().getType().equals(type)) {
            builder.glow();
        }
        return builder.build();
    }
}
