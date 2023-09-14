package me.clickism.clickvillagers.menu;

import me.clickism.clickvillagers.Utils;
import me.clickism.clickvillagers.config.Messages;
import me.clickism.clickvillagers.managers.VillagerData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;

public class EditVillagerMenu {
    public static Inventory get(LivingEntity entity) {
        Inventory inv = MenuBackground.getBackground(getTitle(entity));
        inv.setItem(10, Buttons.PICK.item(entity));
        inv.setItem(12, Buttons.ADD_TRADE_PARTNER.item().clone());
        if (VillagerData.isTradable(entity)) {
            inv.setItem(13, Buttons.TRADABLE.item().clone());
        } else {
            inv.setItem(13, Buttons.NOT_TRADABLE.item().clone());
        }
        inv.setItem(14, Buttons.BIOME.item().clone());
        inv.setItem(16, Buttons.UNCLAIM.item().clone());
        return inv;
    }

    public static String getTitle(LivingEntity entity) {
        return Utils.colorize(Utils.colorize("&2⚒ &l" + VillagerData.getOwner(entity) + "&r&a&l" + Messages.get("menu-edit")));
    }
}
