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

import java.util.stream.Collectors;

public class TradeInfoProviders {

    public static final TradeInfoProvider ALL_TRADES = TradeInfoProvider.builder().build();

    public static final TradeInfoProvider LIBRARIAN = TradeInfoProvider.builder()
            .filterResults(result -> result.getType() == Material.ENCHANTED_BOOK)
            .resultFormatter(item -> {
                if (!(item.getItemMeta() instanceof EnchantmentStorageMeta meta)) return "?";
                return meta.getStoredEnchants().entrySet().stream()
                        .map(entry -> {
                            String enchantment = entry.getKey().getKey().getKey().replace("_", " ");
                            String level = toRomanNumeral(entry.getValue());
                            return "📖 " + Utils.titleCase(enchantment) + " " + level;
                        })
                        .collect(Collectors.joining(" + "));
            })
            .build();

    public static final TradeInfoProvider FARMER = TradeInfoProvider.builder()
            .acceptIngredients(Material.WHEAT, Material.BEETROOT, Material.CARROT, Material.POTATO, Material.PUMPKIN, Material.MELON)
            .acceptResults(Material.GOLDEN_CARROT)
            .build();

    public static TradeInfoProvider getProvider(Villager.Profession profession) {
        return switch (profession) {
            case LIBRARIAN -> LIBRARIAN;
            case FARMER -> FARMER;
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
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> String.valueOf(number);
        };
    }
}
