/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.villager;

import me.clickism.clickvillagers.util.Utils;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TradeInfoProvider {

    private static final String LINE_FORMAT = "   &8→ &7%s &8→ &7%s";

    public static final Function<ItemStack, String> DEFAULT_FORMATTER = Utils::formatItem;

    private final Predicate<ItemStack> ingredientsFilter;
    private final Predicate<ItemStack> resultsFilter;

    private final Function<ItemStack, String> ingredientFormatter;
    private final Function<ItemStack, String> resultFormatter;

    public TradeInfoProvider(Predicate<ItemStack> ingredientsFilter, Predicate<ItemStack> resultsFilter,
                             Function<ItemStack, String> ingredientFormatter, Function<ItemStack, String> resultFormatter) {
        this.ingredientsFilter = ingredientsFilter;
        this.resultsFilter = resultsFilter;
        this.ingredientFormatter = ingredientFormatter;
        this.resultFormatter = resultFormatter;
    }

    public List<String> getTradeInfoLines(List<MerchantRecipe> recipes) {
        return recipes.stream()
                .filter(recipe -> recipe.getIngredients().stream().anyMatch(ingredientsFilter)
                        || resultsFilter.test(recipe.getResult()))
                .map(this::formatRecipe)
                .toList();
    }

    private String formatRecipe(MerchantRecipe recipe) {
        String ingredients = recipe.getIngredients().stream()
                .map(ingredientFormatter)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" + "));
        String result = resultFormatter.apply(recipe.getResult());
        return formatLine(ingredients, result);
    }

    private static String formatLine(String ingredients, String result) {
        return String.format(LINE_FORMAT, ingredients, result);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Predicate<ItemStack> ingredientsFilter = item -> true;
        private Predicate<ItemStack> resultsFilter = item -> true;

        private Function<ItemStack, String> ingredientFormatter = Utils::formatItem;
        private Function<ItemStack, String> resultFormatter = Utils::formatItem;

        private Builder() {
        }

        public Builder filterIngredients(Predicate<ItemStack> ingredientsFilter) {
            this.ingredientsFilter = ingredientsFilter;
            return this;
        }

        public Builder acceptIngredients(Material... ingredients) {
            Set<Material> ingredientsSet = Set.of(ingredients);
            this.ingredientsFilter = item -> ingredientsSet.contains(item.getType());
            return this;
        }

        public Builder filterResults(Predicate<ItemStack> resultsFilter) {
            this.resultsFilter = resultsFilter;
            return this;
        }

        public Builder acceptResults(Material... results) {
            Set<Material> resultSet = Set.of(results);
            this.resultsFilter = item -> resultSet.contains(item.getType());
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

        public TradeInfoProvider build() {
            return new TradeInfoProvider(ingredientsFilter, resultsFilter, ingredientFormatter, resultFormatter);
        }
    }

}
