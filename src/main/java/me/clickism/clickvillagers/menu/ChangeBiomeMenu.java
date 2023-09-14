package me.clickism.clickvillagers.menu;

import me.clickism.clickvillagers.Utils;
import me.clickism.clickvillagers.config.Messages;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ChangeBiomeMenu {

    private static String lore;

    public static Inventory get() {
        Inventory inv = MenuBackground.getBackground(Messages.get("menu-biome"));
        inv.setItem(10, getTypeButton(Villager.Type.PLAINS, Material.OAK_SAPLING));
        inv.setItem(11, getTypeButton(Villager.Type.DESERT, Material.DEAD_BUSH));
        inv.setItem(12, getTypeButton(Villager.Type.JUNGLE, Material.JUNGLE_SAPLING));
        inv.setItem(13, getTypeButton(Villager.Type.SNOW, Material.FERN));
        inv.setItem(14, getTypeButton(Villager.Type.SAVANNA, Material.ACACIA_SAPLING));
        inv.setItem(15, getTypeButton(Villager.Type.SWAMP, Material.BLUE_ORCHID));
        inv.setItem(16, getTypeButton(Villager.Type.TAIGA, Material.SPRUCE_SAPLING));
        return inv;
    }

    public static String getTitle() {
        return Messages.get("menu-biome");
    }

    private static ItemStack getTypeButton(Villager.Type type, Material mat) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + type.name());
        lore = Utils.colorize("&2" + ChatColor.stripColor(Messages.get("button-biome-lore-1")));
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    public static Villager.Type getType(ItemStack item) {
        String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
        for (Villager.Type value : Villager.Type.values()) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }

    public static boolean isTypeButton(ItemStack item) {
        return item.getItemMeta().getLore().get(0).equals(lore);
    }
}
