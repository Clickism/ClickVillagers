/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.villager;

import me.clickism.clickvillagers.util.Utils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TradeInfoProvider {

    private static final String LINE_FORMAT = "   §8→ §7%s §8→ §7%s";
    private static final Pattern LINE_BREAK_PATTERN = Pattern.compile("\n");

    private final Predicate<ItemStack> ingredientsFilter;
    private final Predicate<ItemStack> resultsFilter;

    private final Function<ItemStack, String> ingredientFormatter;
    private final Function<ItemStack, String> resultFormatter;

    private final boolean formatEnchantments;

    public TradeInfoProvider(Predicate<ItemStack> ingredientsFilter,
                             Predicate<ItemStack> resultsFilter,
                             Function<ItemStack, String> ingredientFormatter,
                             Function<ItemStack, String> resultFormatter,
                             boolean formatEnchantments) {
        this.ingredientsFilter = ingredientsFilter;
        this.resultsFilter = resultsFilter;
        this.ingredientFormatter = ingredientFormatter;
        this.resultFormatter = resultFormatter;
        this.formatEnchantments = formatEnchantments;
    }

    public List<String> getTradeInfoLines(TradeOfferList offers) {
        return offers.stream()
                .filter(offer -> ingredientsFilter.test(offer.getDisplayedFirstBuyItem())
                        || resultsFilter.test(offer.getDisplayedSecondBuyItem())
                        || resultsFilter.test(offer.getSellItem()))
                .map(this::formatRecipe)
                .flatMap(LINE_BREAK_PATTERN::splitAsStream)
                .toList();
    }

    private String formatRecipe(TradeOffer offer) {
        String ingredients = Stream.of(offer.getDisplayedFirstBuyItem(), offer.getDisplayedSecondBuyItem())
                .filter(item -> !item.isOf(Items.AIR))
                .map(ingredientFormatter)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" + "));
        String result = resultFormatter.apply(offer.getSellItem());
        String line = formatLine(ingredients, result);
        if (formatEnchantments) {
            line += formatEnchantments(offer);
        }
        return line;
    }

    private static String formatLine(String ingredients, String result) {
        return String.format(LINE_FORMAT, ingredients, result);
    }

    private static final String SINGLE_SPACING = " ".repeat(26);
    private static final String DOUBLE_SPACING = " ".repeat(27);

    private static String formatEnchantments(TradeOffer offer) {
        String spacing = getSpacing(offer);
        ItemStack item = offer.getSellItem();
        return item.getEnchantments().getEnchantmentEntries().stream()
                .map(entry -> {
                    String enchantment = entry.getKey().getKey()
                            .map(RegistryKey::getValue)
                            .map(Identifier::getPath)
                            .map(s -> s.replace("_", " "))
                            .orElse("?");
                    String level = Utils.toRomanNumeral(entry.getIntValue());
                    return "\n" + spacing + "§7" + Utils.titleCase(enchantment) + " " + level;
                })
                .collect(Collectors.joining());
    }

    private static String getSpacing(TradeOffer recipe) {
        int emeraldCount = Stream.of(recipe.getDisplayedFirstBuyItem(), recipe.getDisplayedSecondBuyItem())
                .filter(item -> item.isOf(Items.EMERALD))
                .mapToInt(ItemStack::getCount)
                .sum();
        if (emeraldCount == 0) return "";
        return emeraldCount < 10 ? SINGLE_SPACING : DOUBLE_SPACING;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Predicate<ItemStack> ingredientsFilter = item -> false;
        private Predicate<ItemStack> resultsFilter = item -> false;

        private Function<ItemStack, String> ingredientFormatter = Utils::formatItem;
        private Function<ItemStack, String> resultFormatter = Utils::formatItem;

        private boolean formatEnchantments = false;

        private Builder() {
        }

        public Builder filterIngredients(Predicate<ItemStack> ingredientsFilter) {
            this.ingredientsFilter = ingredientsFilter;
            return this;
        }

        public Builder acceptIngredients(Item... ingredients) {
            Set<Item> ingredientsSet = Set.of(ingredients);
            this.ingredientsFilter = item -> ingredientsSet.contains(item.getItem());
            return this;
        }

        public Builder filterResults(Predicate<ItemStack> resultsFilter) {
            this.resultsFilter = resultsFilter;
            return this;
        }

        public Builder acceptResults(Item... results) {
            Set<Item> resultSet = Set.of(results);
            this.resultsFilter = item -> resultSet.contains(item.getItem());
            return this;
        }

        public Builder ingredientFormatter(Function<ItemStack, String> ingredientFormatter) {
            this.ingredientFormatter = ingredientFormatter;
            return this;
        }

        public Builder resultFormatter(Function<ItemStack, String> resultFormatter) {
            this.resultFormatter = resultFormatter;
            return this;
        }

        public Builder singleFormatter(Function<ItemStack, String> itemFormatter) {
            this.ingredientFormatter = itemFormatter;
            this.resultFormatter = itemFormatter;
            return this;
        }

        public Builder formatEnchantments() {
            this.formatEnchantments = true;
            return this;
        }

        public TradeInfoProvider build() {
            return new TradeInfoProvider(ingredientsFilter, resultsFilter, ingredientFormatter,
                    resultFormatter, formatEnchantments);
        }
    }

}
