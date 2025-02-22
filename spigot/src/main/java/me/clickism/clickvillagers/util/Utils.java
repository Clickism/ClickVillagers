/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Utils {

    /**
     * Colorizes a string with the alternate color code '&amp;'.
     *
     * @param text the text to colorize
     * @return colorized string
     **/
    public static String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String formatMaterial(Material material) {
        return titleCase(material.toString().replace("_", " "));
    }

    /**
     * Title cases a string. (i.E.: "HELLO world" -> "Hello World")
     *
     * @param string the string to title case
     * @return the title cased string
     */
    public static String titleCase(String string) {
        return capitalize(string.toLowerCase());
    }

    /**
     * Capitalizes the first letter of each word in a string.
     *
     * @param string the string to capitalize
     * @return the capitalized string
     */
    public static String capitalize(String string) {
        String[] words = string.split(" ");
        StringBuilder capitalizedString = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) continue;
            capitalizedString.append(capitalizeWord(word)).append(" ");
        }
        return capitalizedString.toString().trim();
    }

    public static String capitalizeWord(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static Villager.Profession getVillagerProfession(LivingEntity entity) {
        if (entity instanceof Villager villager) {
            return villager.getProfession();
        } else if (entity instanceof ZombieVillager villager) {
            return villager.getVillagerProfession();
        } else {
            throw new IllegalArgumentException("Entity is not a villager");
        }
    }

    public static void setHandOrGive(Player player, ItemStack item) {
        PlayerInventory inventory = player.getInventory();
        if (inventory.getItemInMainHand().getType() == Material.AIR) {
            inventory.setItemInMainHand(item);
            return;
        }
        player.getInventory().addItem(item).forEach((index, toDrop) -> {
            player.getWorld().dropItem(player.getLocation(), toDrop);
        });
    }
}
