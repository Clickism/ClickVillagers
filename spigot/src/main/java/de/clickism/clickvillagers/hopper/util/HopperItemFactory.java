package de.clickism.clickvillagers.hopper.util;

import de.clickism.clickvillagers.ClickVillagers;
import de.clickism.clickvillagers.message.Message;
import me.clickism.clickgui.menu.Icon;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.persistence.PersistentDataType;

public final class HopperItemFactory {
    private HopperItemFactory() {}

    public static final NamespacedKey VILLAGER_HOPPER_KEY = new NamespacedKey(ClickVillagers.INSTANCE, "villager_hopper");
    public static final NamespacedKey DISPLAY_UUID_KEY = new NamespacedKey(ClickVillagers.INSTANCE, "display_uuid");
    private static final ItemStack HOPPER_ITEM = createHopperItem();

    private static ItemStack createHopperItem() {
        return Icon.of(Material.HOPPER)
                .setName(ChatColor.GREEN + Message.VILLAGER_HOPPER.toString())
                .setLore(Message.VILLAGER_HOPPER.getLore())
                .addEnchantmentGlint()
                .applyToMeta(meta ->
                        meta.getPersistentDataContainer().set(VILLAGER_HOPPER_KEY, PersistentDataType.BOOLEAN, true)
                ).get();
    }

    public static ItemStack getHopperItem() {
        return HOPPER_ITEM.clone();
    }

    public static void registerRecipe() {
        ItemStack item = getHopperItem();
        ShapelessRecipe recipe = new ShapelessRecipe(VILLAGER_HOPPER_KEY, item);
        recipe.addIngredient(Material.HOPPER);
        recipe.addIngredient(Material.EMERALD);
        Bukkit.addRecipe(recipe);
    }

    public static void unregisterRecipe() {
        Bukkit.removeRecipe(VILLAGER_HOPPER_KEY);
    }
}
