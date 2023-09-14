package me.clickism.clickvillagers.menu;

import me.clickism.clickvillagers.config.Messages;
import me.clickism.clickvillagers.managers.SkullManager;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public enum Buttons {

    CLAIM(Material.OAK_HANGING_SIGN, "button-claim"),
    UNCLAIM(Material.BARRIER, "button-unclaim"),
    TRADABLE(Material.EMERALD, "button-tradable"),
    NOT_TRADABLE(Material.REDSTONE, "button-not-tradable"),
    ADD_TRADE_PARTNER(Material.WRITABLE_BOOK, "button-owner"),
    BIOME(Material.BRUSH, "button-biome"),
    PICK(Material.PLAYER_HEAD, "button-pick") {
        @Override
        public ItemStack item(LivingEntity entity) {
            ItemStack skull = SkullManager.getVillagerHeadItem(entity);
            ItemMeta meta = skull.getItemMeta();
            meta.setDisplayName(PICK.display);
            meta.setLore(PICK.lore);
            skull.setItemMeta(meta);
            return skull;
        }
    },
    BLANK_GRAY(Material.GRAY_STAINED_GLASS_PANE, "blank"),
    BLANK_BLACK(Material.BLACK_STAINED_GLASS_PANE, "blank");

    private final ItemStack item;
    private final String display;
    private final List<String> lore;

    Buttons(Material mat, String path) {
        item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Messages.get(path));
        List<String> lore = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            if (!Messages.get(path + "-lore-" + i).equals("")) {
                lore.add(Messages.get(path + "-lore-" + i));
            }
        }
        display = Messages.get(path);
        this.lore = lore;
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

    }

    public ItemStack item() {
        return item;
    }

    public ItemStack item(LivingEntity entity) {
        return item;
    }
}
