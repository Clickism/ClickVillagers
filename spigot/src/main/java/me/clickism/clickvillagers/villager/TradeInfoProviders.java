/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.villager;

import me.clickism.clickvillagers.util.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.stream.Collectors;

public class TradeInfoProviders {

    private static final Function<ItemStack, String> ITEM_FORMATTER = item -> {
        Material material = item.getType();
        String prefix = getPrefix(material);
        prefix = (prefix == null) ? "" : prefix + " ";
        boolean isTool = item.getType().getMaxDurability() != 0;
        String name = (isTool)
                ? Utils.formatMaterial(material)
                : Utils.formatItem(item);
        return prefix + name;
    };

    public static final TradeInfoProvider ALL_TRADES = TradeInfoProvider.builder()
            .filterIngredients(item -> true)
            .filterResults(item -> true)
            .build();

    public static final TradeInfoProvider LIBRARIAN = TradeInfoProvider.builder()
            .acceptResults(Material.ENCHANTED_BOOK)
            .ingredientFormatter(item -> {
                if (item.getType() != Material.EMERALD) return null;
                return item.getAmount() + " Emeralds";
            })
            .resultFormatter(TradeInfoProviders::formatEnchantedBook)
            .build();

    public static final TradeInfoProvider FARMER = TradeInfoProvider.builder()
            .acceptIngredients(Material.WHEAT, Material.BEETROOT, Material.CARROT, Material.POTATO, Material.PUMPKIN, Material.MELON)
            .acceptResults(Material.GOLDEN_CARROT, Material.APPLE, Material.BREAD)
            .singleFormatter(ITEM_FORMATTER)
            .build();

    public static final TradeInfoProvider SMITH = TradeInfoProvider.builder()
            .acceptIngredients(Material.COAL)
            .acceptResults(Material.DIAMOND_AXE, Material.DIAMOND_PICKAXE, Material.DIAMOND_SHOVEL,
                    Material.DIAMOND_HOE, Material.DIAMOND_SWORD, Material.DIAMOND_HELMET,
                    Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
                    Material.SHIELD)
            .singleFormatter(ITEM_FORMATTER)
            .formatEnchantments()
            .build();

    public static final TradeInfoProvider BUTCHER = TradeInfoProvider.builder()
            .acceptIngredients(Material.CHICKEN, Material.PORKCHOP, Material.RABBIT,
                    Material.SWEET_BERRIES, Material.KELP)
            .acceptResults(Material.COOKED_CHICKEN, Material.COOKED_PORKCHOP)
            .singleFormatter(ITEM_FORMATTER)
            .build();

    public static final TradeInfoProvider FISHERMAN = TradeInfoProvider.builder()
            .acceptIngredients(Material.STRING, Material.COAL)
            .acceptResults(Material.CAMPFIRE, Material.FISHING_ROD)
            .singleFormatter(ITEM_FORMATTER)
            .formatEnchantments()
            .build();

    public static TradeInfoProvider getProvider(Villager.Profession profession) {
        return switch (profession) {
            case LIBRARIAN -> LIBRARIAN;
            case FARMER -> FARMER;
            case TOOLSMITH,
                 WEAPONSMITH,
                 ARMORER -> SMITH;
            case BUTCHER -> BUTCHER;
            case FISHERMAN -> FISHERMAN;
            default -> ALL_TRADES;
        };
    }

    @Nullable
    private static String getPrefix(Material material) {
        return switch (material) {
            case ENCHANTED_BOOK -> "&d📖";

            case GOLDEN_CARROT -> "&e🥕";
            case APPLE -> "&c🍎";

            case WHEAT -> "&e🌾";
            case CARROT -> "&6🥕";
            case POTATO -> "&6🥔";
            case PUMPKIN -> "&6🎃";
            case MELON -> "&a🍉";

            case COAL -> "&8🪨";

            case DIAMOND_SWORD -> "&b🗡";
            case DIAMOND_AXE,
                 DIAMOND_PICKAXE,
                 DIAMOND_SHOVEL,
                 DIAMOND_HOE -> "&b⛏";
            case DIAMOND_HELMET,
                 DIAMOND_CHESTPLATE,
                 DIAMOND_LEGGINGS,
                 DIAMOND_BOOTS -> "&b👕";

            case CHICKEN -> "&e🐓";
            case PORKCHOP -> "&e🐷";
            case RABBIT -> "&e🐰";
            case SWEET_BERRIES -> "&c🍇";
            case KELP -> "&a🌿";

            case COOKED_CHICKEN -> "&6🐓";
            case COOKED_PORKCHOP -> "&6🐷";

            case STRING -> "&7🪡";
            case CAMPFIRE -> "&c🔥";
            case FISHING_ROD -> "&6🎣";

            case SHIELD -> "&6🛡️";

            default -> null;
        };
    }

    private static String formatEnchantedBook(ItemStack item) {
        if (!(item.getItemMeta() instanceof EnchantmentStorageMeta meta)) return "";
        String enchantments = meta.getStoredEnchants().entrySet().stream()
                .map(entry -> {
                    String enchantment = entry.getKey().getKey().getKey().replace("_", " ");
                    String level = Utils.toRomanNumeral(entry.getValue());
                    return Utils.titleCase(enchantment) + " " + level;
                })
                .collect(Collectors.joining(" + "));
        return "&d📖 " + enchantments;
    }
}
