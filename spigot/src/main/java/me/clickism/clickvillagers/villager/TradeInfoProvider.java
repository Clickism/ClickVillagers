/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.villager;

import me.clickism.clickvillagers.util.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TradeInfoProvider {

    private static final String LINE_FORMAT = "   &8→ &7%s &8→ &7%s";
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

    public List<String> getTradeInfoLines(List<MerchantRecipe> recipes) {
        return recipes.stream()
                .filter(recipe -> recipe.getIngredients().stream().anyMatch(ingredientsFilter)
                        || resultsFilter.test(recipe.getResult()))
                .map(this::formatRecipe)
                .flatMap(LINE_BREAK_PATTERN::splitAsStream)
                .toList();
    }

    private String formatRecipe(MerchantRecipe recipe) {
        String ingredients = recipe.getIngredients().stream()
                .map(ingredientFormatter)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" + "));
        String result = resultFormatter.apply(recipe.getResult());
        String line = formatLine(ingredients, result);
        if (formatEnchantments) {
            line += formatEnchantments(recipe);
        }
        return line;
    }

    private static String formatLine(String ingredients, String result) {
        return String.format(LINE_FORMAT, ingredients, result);
    }

    private static final String SINGLE_SPACING = " ".repeat(26);
    private static final String DOUBLE_SPACING = " ".repeat(27);

    private static String formatEnchantments(MerchantRecipe recipe) {
        String spacing = getSpacing(recipe);
        ItemMeta meta = recipe.getResult().getItemMeta();
        if (meta == null) return "";
        return meta.getEnchants().entrySet().stream()
                .map(entry -> {
                    String name = Utils.titleCase(entry.getKey().getKey().getKey().replace("_", " "));
                    String level = Utils.toRomanNumeral(entry.getValue());
                    return "\n" + spacing + "&7" + name + " " + level;
                })
                .collect(Collectors.joining());
    }

    private static String getSpacing(MerchantRecipe recipe) {
        int emeraldCount = recipe.getIngredients().stream()
                .filter(item -> item.getType() == Material.EMERALD)
                .mapToInt(ItemStack::getAmount)
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
