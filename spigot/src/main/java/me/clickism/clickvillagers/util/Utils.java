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
