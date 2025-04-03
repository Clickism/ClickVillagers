/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.gui;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.GuiInterface;
import me.clickism.clickvillagers.util.VersionHelper;
import me.clickism.clickvillagers.villager.VillagerHandler;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.village.VillagerType;

import java.util.List;

public class VillagerBiomeChangeGui extends VillagerGui {

    //? if >=1.21.5 {
    private record VillagerBiome(RegistryKey<VillagerType> type, Item icon) {}
    //?} else
    /*private record VillagerBiome(VillagerType type, Item icon) {}*/

    private static final List<VillagerBiome> BIOMES = List.of(
            new VillagerBiome(VillagerType.PLAINS, Items.OAK_SAPLING),
            new VillagerBiome(VillagerType.DESERT, Items.DEAD_BUSH),
            new VillagerBiome(VillagerType.JUNGLE, Items.JUNGLE_SAPLING),
            new VillagerBiome(VillagerType.SNOW, Items.FERN),
            new VillagerBiome(VillagerType.SAVANNA, Items.ACACIA_SAPLING),
            new VillagerBiome(VillagerType.SWAMP, Items.BLUE_ORCHID),
            new VillagerBiome(VillagerType.TAIGA, Items.SPRUCE_SAPLING)
    );

    private final GuiInterface previous;

    public VillagerBiomeChangeGui(ServerPlayerEntity player, VillagerHandler<?> villagerHandler, GuiInterface previous) {
        super(player, villagerHandler);
        this.previous = previous;
        setSlot(18, new BackButton(previous));
        setTitle(Text.literal("🌲 ").formatted(Formatting.DARK_GRAY)
                .append(Text.literal("Choose Villager's Biome").formatted(Formatting.DARK_GRAY, Formatting.BOLD)));
        placeBiomeButtons();
    }

    private void placeBiomeButtons() {
        int i = 10;
        for (VillagerBiome biome : BIOMES) {
            setSlot(i, getBiomeButton(biome.type, biome.icon));
            i++;
        }
    }

    //? if >=1.21.5 {
    private GuiElement getBiomeButton(RegistryKey<VillagerType> typeKey, Item icon) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return new GuiElementBuilder(icon)
                    .setName(Text.literal("Error: Server is null").formatted(Formatting.RED))
                    .build();
        }
        RegistryEntry<VillagerType> type = player.getServer().getRegistryManager()
                .getOrThrow(net.minecraft.registry.RegistryKeys.VILLAGER_TYPE)
                .getEntry(typeKey.getValue())
                .orElseThrow();
        //?} else
        /*private GuiElement getBiomeButton(VillagerType type, Item icon) {*/
        var villager = villagerHandler.getEntity();
        //? if >=1.21.5 {
        String biomeName = type.getKey().orElseThrow().getValue().getPath().toUpperCase();
        //?} else
        /*String biomeName = type.toString().toUpperCase();*/
        GuiElementBuilder builder = new GuiElementBuilder(icon)
                .setName(Text.literal(biomeName).formatted(Formatting.GREEN, Formatting.BOLD))
                .addLoreLine(Text.literal("Click to change the villager's biome.").formatted(Formatting.DARK_GREEN))
                .setCallback((index, t, action, gui) -> {
                    if (villager.isRemoved()) return;
                    villager.setVillagerData(villager.getVillagerData().withType(type));
                    VersionHelper.playSound(player, SoundEvents.BLOCK_COMPOSTER_FILL_SUCCESS, SoundCategory.MASTER, 1, .5f);
                    VersionHelper.playSound(player, SoundEvents.BLOCK_AZALEA_LEAVES_PLACE, SoundCategory.MASTER, 1, 1);
                    VersionHelper.playSound(player, SoundEvents.BLOCK_AZALEA_PLACE, SoundCategory.MASTER, 1, 2);
                    placeBiomeButtons();
                });
        //? if >=1.21.5 {
        if (villager.getVillagerData().type().equals(type)) {
        //?} else
        /*if (villager.getVillagerData().getType().equals(type)) {*/
            builder.glow();
        }
        return builder.build();
    }
}
