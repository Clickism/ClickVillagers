package me.clickism.clickvillagers.menu;

import me.clickism.clickvillagers.ClickVillagers;
import me.clickism.clickvillagers.config.Messages;
import org.bukkit.inventory.Inventory;

public class ClaimVillagerMenu {

    public static void setPlugin(ClickVillagers pl) {
        plugin = pl;
    }
    static ClickVillagers plugin;
    public static Inventory get() {
        Inventory inv = MenuBackground.getBackground(Messages.get("menu-claim"));
        inv.setItem(13, Buttons.CLAIM.item().clone());
        return inv;
    }

    public static String getTitle() {
        return Messages.get("menu-claim");
    }
}
