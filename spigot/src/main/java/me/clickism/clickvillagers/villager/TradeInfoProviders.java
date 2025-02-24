/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.villager;

import me.clickism.clickvillagers.util.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Set;
import java.util.stream.Collectors;

public class TradeInfoProviders {

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
            .resultFormatter(item -> {
                if (!(item.getItemMeta() instanceof EnchantmentStorageMeta meta)) return "?";
                return "&d📖 " + meta.getStoredEnchants().entrySet().stream()
                        .map(entry -> {
                            String enchantment = entry.getKey().getKey().getKey().replace("_", " ");
                            String level = toRomanNumeral(entry.getValue());
                            return Utils.titleCase(enchantment) + " " + level;
                        })
                        .collect(Collectors.joining(" + "));
            })
            .build();

    public static final TradeInfoProvider FARMER = TradeInfoProvider.builder()
            .acceptIngredients(Material.WHEAT, Material.BEETROOT, Material.CARROT, Material.POTATO, Material.PUMPKIN, Material.MELON)
            .acceptResults(Material.GOLDEN_CARROT, Material.APPLE, Material.BREAD)
            .resultFormatter(item -> {
                int amount = item.getAmount();
                return switch (item.getType()) {
                    case GOLDEN_CARROT -> "&e🥕 " + amount + " Golden Carrot";
                    case APPLE -> "&c🍎 " + amount + " Apple";
                    default -> TradeInfoProvider.DEFAULT_FORMATTER.apply(item);
                };
            })
            .ingredientFormatter(item -> {
                int amount = item.getAmount();
                return switch (item.getType()) {
                    case WHEAT -> "&e🌾 " + amount + " Wheat";
                    case CARROT -> "&6🥕 " + amount + " Carrot";
                    case POTATO -> "&6🥔 " + amount + " Potato";
                    case PUMPKIN -> "&6🎃 " + amount + " Pumpkin";
                    case MELON -> "&a🍉 " + amount + " Melon";
                    default -> TradeInfoProvider.DEFAULT_FORMATTER.apply(item);
                };
            })
            .build();

    private static final Set<Material> DIAMOND_TOOLS = Set.of(Material.DIAMOND_AXE, Material.DIAMOND_PICKAXE,
            Material.DIAMOND_SHOVEL, Material.DIAMOND_HOE, Material.DIAMOND_SWORD);

    public static final TradeInfoProvider SMITH = TradeInfoProvider.builder()
            .acceptIngredients(Material.COAL)
            .acceptResults(DIAMOND_TOOLS.toArray(new Material[0]))
            .ingredientFormatter(item -> {
                if (item.getType() != Material.COAL) return TradeInfoProvider.DEFAULT_FORMATTER.apply(item);
                return "&8🪨 " + item.getAmount() + " Coal";
            })
            .resultFormatter(item -> {
                Material material = item.getType();
                if (!DIAMOND_TOOLS.contains(material)) return TradeInfoProvider.DEFAULT_FORMATTER.apply(item);
                ItemMeta meta = item.getItemMeta();
                if (meta == null) return TradeInfoProvider.DEFAULT_FORMATTER.apply(item);
                String enchantments = meta.getEnchants().entrySet().stream()
                        .map(entry -> {
                            String enchantment = entry.getKey().getKey().getKey().replace("_", " ");
                            String level = toRomanNumeral(entry.getValue());
                            return "\n                        &d   &7" + Utils.titleCase(enchantment) + " " + level;
                        })
                        .collect(Collectors.joining());
                String icon = (material == Material.DIAMOND_SWORD) ? "🗡" : "⛏";
                String name = "&b" + icon + " " + Utils.formatMaterial(material);
                return name + enchantments;
            })
            .build();

    public static TradeInfoProvider getProvider(Villager.Profession profession) {
        return switch (profession) {
            case LIBRARIAN -> LIBRARIAN;
            case FARMER -> FARMER;
            case TOOLSMITH, WEAPONSMITH -> SMITH;
            default -> ALL_TRADES;
        };
    }

    private static String toRomanNumeral(int number) {
        return switch (number) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> String.valueOf(number);
        };
    }
}
