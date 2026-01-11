/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.gui;

import de.clickism.fgui.api.elements.GuiElement;
import de.clickism.fgui.api.elements.GuiElementBuilder;
import de.clickism.fgui.api.gui.GuiInterface;
import de.clickism.clickvillagers.util.VersionHelper;
import de.clickism.clickvillagers.villager.VillagerHandler;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.npc.villager.VillagerType;

import java.util.List;

public class VillagerBiomeChangeGui extends VillagerGui {

    //? if >=1.21.5 {
    private record VillagerBiome(ResourceKey<VillagerType> type, Item icon) {}
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

    public VillagerBiomeChangeGui(ServerPlayer player, VillagerHandler<?> villagerHandler, GuiInterface previous) {
        super(player, villagerHandler);
        this.previous = previous;
        setSlot(18, new BackButton(previous));
        setTitle(Component.literal("🌲 ").withStyle(ChatFormatting.DARK_GRAY)
                .append(Component.literal("Choose Villager's Biome").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.BOLD)));
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
    private GuiElement getBiomeButton(ResourceKey<VillagerType> typeKey, Item icon) {
        //? if >= 1.21.9 {
        MinecraftServer server = player.createCommandSourceStack().getServer();
        //?} else
        //MinecraftServer server = player.getServer();
        if (server == null) {
            return new GuiElementBuilder(icon)
                    .setName(Component.literal("Error: Server is null").withStyle(ChatFormatting.RED))
                    .build();
        }
        Holder<VillagerType> type = server.registryAccess()
                .lookupOrThrow(net.minecraft.core.registries.Registries.VILLAGER_TYPE)
                .get(VersionHelper.identifier(typeKey))
                .orElseThrow();
        //?} else
        /*private GuiElement getBiomeButton(VillagerType type, Item icon) {*/
        var villager = villagerHandler.getEntity();
        //? if >=1.21.5 {
        String biomeName = VersionHelper.identifier(type.unwrapKey().orElseThrow())
                .getPath().toUpperCase();
        //?} else
        /*String biomeName = type.toString().toUpperCase();*/
        GuiElementBuilder builder = new GuiElementBuilder(icon)
                .setName(Component.literal(biomeName).withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD))
                .addLoreLine(Component.literal("Click to change the villager's biome.").withStyle(ChatFormatting.DARK_GREEN))
                .setCallback((index, t, action, gui) -> {
                    if (villager.isRemoved()) return;
                    villager.setVillagerData(villager.getVillagerData().withType(type));
                    VersionHelper.playSound(player, SoundEvents.COMPOSTER_FILL_SUCCESS, SoundSource.MASTER, 1, .5f);
                    VersionHelper.playSound(player, SoundEvents.AZALEA_LEAVES_PLACE, SoundSource.MASTER, 1, 1);
                    VersionHelper.playSound(player, SoundEvents.AZALEA_PLACE, SoundSource.MASTER, 1, 2);
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
