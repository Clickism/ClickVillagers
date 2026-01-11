/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.villager;

import de.clickism.clickvillagers.util.Utils;
import de.clickism.clickvillagers.util.VersionHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

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

    public List<String> getTradeInfoLines(MerchantOffers offers) {
        return offers.stream()
                .filter(offer -> ingredientsFilter.test(VersionHelper.getFirstBuyItem(offer))
                        || resultsFilter.test(VersionHelper.getSecondBuyItem(offer))
                        || resultsFilter.test(offer.getResult()))
                .map(this::formatRecipe)
                .flatMap(LINE_BREAK_PATTERN::splitAsStream)
                .toList();
    }

    private String formatRecipe(MerchantOffer offer) {
        String ingredients = Stream.of(VersionHelper.getFirstBuyItem(offer), VersionHelper.getSecondBuyItem(offer))
                .filter(item -> !item.is(Items.AIR))
                .map(ingredientFormatter)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" + "));
        String result = resultFormatter.apply(offer.getResult());
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

    //? if >=1.20.5 {
    private static String formatEnchantments(MerchantOffer offer) {
        String spacing = getSpacing(offer);
        ItemStack item = offer.getResult();
        return item.getEnchantments().entrySet().stream()
                .map(entry -> {
                    String enchantment = entry.getKey().unwrapKey()
                            .map(ResourceKey::identifier)
                            .map(Identifier::getPath)
                            .map(s -> s.replace("_", " "))
                            .orElse("?");
                    String level = Utils.toRomanNumeral(entry.getIntValue());
                    return "\n" + spacing + "§7" + Utils.titleCase(enchantment) + " " + level;
                })
                .collect(Collectors.joining());
    }
    //?} else {
    /*private static String formatEnchantments(TradeOffer offer) {
        String spacing = getSpacing(offer);
        ItemStack item = offer.getSellItem();
        return Utils.streamEnchantments(item.getEnchantments())
                .map(s -> "\n" + spacing + "§7" + s)
                .collect(Collectors.joining());
    }
    *///?}

    private static String getSpacing(MerchantOffer offer) {
        int emeraldCount = Stream.of(VersionHelper.getFirstBuyItem(offer), VersionHelper.getSecondBuyItem(offer))
                .filter(item -> item.is(Items.EMERALD))
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
