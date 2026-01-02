/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.gui;

import de.clickism.clickgui.menu.*;
import de.clickism.clickvillagers.message.Message;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;

import java.util.List;

import static de.clickism.clickvillagers.message.Message.localize;

public class VillagerBiomeChangeMenu extends Menu {

    private static final List<VillagerBiome> BIOMES = List.of(
            new VillagerBiome(Villager.Type.PLAINS, Material.OAK_SAPLING),
            new VillagerBiome(Villager.Type.DESERT, Material.DEAD_BUSH),
            new VillagerBiome(Villager.Type.JUNGLE, Material.JUNGLE_SAPLING),
            new VillagerBiome(Villager.Type.SNOW, Material.FERN),
            new VillagerBiome(Villager.Type.SAVANNA, Material.ACACIA_SAPLING),
            new VillagerBiome(Villager.Type.SWAMP, Material.BLUE_ORCHID),
            new VillagerBiome(Villager.Type.TAIGA, Material.SPRUCE_SAPLING)
    );

    public VillagerBiomeChangeMenu(Player viewer, LivingEntity villager, MenuView previous) {
        super(viewer, MenuType.MENU_9X3);
        setTitle("&8🌲 &l" + Message.TITLE_CHANGE_BIOME);
        setBackground(new VillagerBackground());
        addButton(18, BackButton.to(previous));
        placeBiomeButtons(villager);
    }

    private void placeBiomeButtons(LivingEntity villager) {
        int i = 10;
        for (VillagerBiome biome : BIOMES) {
            addButton(i, getBiomeButton(villager, biome.type, biome.icon));
            i++;
        }
    }

    private Button getBiomeButton(LivingEntity entity, Villager.Type type, Material material) {
        String name = localize("biome." + type.name().toLowerCase());
        return Button.withIcon(() -> Message.BUTTON_CHANGE_BIOME.toIcon(material)
                        .setName("&2🌲 &l" + name)
                        .runIf(type == getType(entity), Icon::addEnchantmentGlint))
                .setOnClick((player, view, slot) -> {
                    if (entity instanceof Villager villager) {
                        villager.setVillagerType(type);
                    } else if (entity instanceof ZombieVillager villager) {
                        villager.setVillagerType(type);
                    }
                    player.playSound(entity, Sound.BLOCK_COMPOSTER_FILL_SUCCESS, 1, .5f);
                    player.playSound(entity, Sound.BLOCK_AZALEA_LEAVES_PLACE, 1, 1);
                    player.playSound(entity, Sound.BLOCK_AZALEA_PLACE, 1, 2);
                    view.refresh();
                });
    }

    private Villager.Type getType(LivingEntity entity) {
        if (entity instanceof Villager villager) {
            return villager.getVillagerType();
        }
        if (entity instanceof ZombieVillager villager) {
            return villager.getVillagerType();
        }
        throw new IllegalArgumentException("Entity is not a villager");
    }

    private record VillagerBiome(Villager.Type type, Material icon) {
    }
}
