/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
//? if >=26.2
import net.minecraft.world.level.block.ColorCollection;

import java.util.List;

public class ColoredItemHelper {
    //? if >=26.2 {
    public static final ColorCollection<Item> STAINED_GLASS_PANE = Items.STAINED_GLASS_PANE;
    public static final ColorCollection<Item> GLAZED_TERRACOTTA = Items.GLAZED_TERRACOTTA;
    public static final ColorCollection<Item> DYED_TERRACOTTA = Items.DYED_TERRACOTTA;
    public static final ColorCollection<Item> WOOL = Items.WOOL;
    //?} else {
    /*public static final ColorCollection<Item> STAINED_GLASS_PANE = ColorCollection.fromFieldsOfClass(Items.class, "STAINED_GLASS_PANE");
    public static final ColorCollection<Item> GLAZED_TERRACOTTA = ColorCollection.fromFieldsOfClass(Items.class, "GLAZED_TERRACOTTA");
    public static final ColorCollection<Item> DYED_TERRACOTTA = ColorCollection.fromFieldsOfClass(Items.class, "TERRACOTTA");
    public static final ColorCollection<Item> WOOL = ColorCollection.fromFieldsOfClass(Items.class, "WOOL");
    *///?}

    //? if <26.2 {
    /*public record ColorCollection<T>(
            T white,
            T orange,
            T magenta,
            T lightBlue,
            T yellow,
            T lime,
            T pink,
            T gray,
            T lightGray,
            T cyan,
            T purple,
            T blue,
            T brown,
            T green,
            T red,
            T black
    ) {
        public List<T> asList() {
            return List.of(white, orange, magenta, lightBlue, yellow, lime, pink, gray, lightGray, cyan, purple, blue, brown, green, red, black);
        }

        public static <T> ColorCollection<T> fromFieldsOfClass(Class<?> clazz, String fieldSuffix) {
            return new ColorCollection<>(
                    staticField(clazz, "WHITE_" + fieldSuffix),
                    staticField(clazz, "ORANGE_" + fieldSuffix),
                    staticField(clazz, "MAGENTA_" + fieldSuffix),
                    staticField(clazz, "LIGHT_BLUE_" + fieldSuffix),
                    staticField(clazz, "YELLOW_" + fieldSuffix),
                    staticField(clazz, "LIME_" + fieldSuffix),
                    staticField(clazz, "PINK_" + fieldSuffix),
                    staticField(clazz, "GRAY_" + fieldSuffix),
                    staticField(clazz, "LIGHT_GRAY_" + fieldSuffix),
                    staticField(clazz, "CYAN_" + fieldSuffix),
                    staticField(clazz, "PURPLE_" + fieldSuffix),
                    staticField(clazz, "BLUE_" + fieldSuffix),
                    staticField(clazz, "BROWN_" + fieldSuffix),
                    staticField(clazz, "GREEN_" + fieldSuffix),
                    staticField(clazz, "RED_" + fieldSuffix),
                    staticField(clazz, "BLACK_" + fieldSuffix)
            );
        }

        @SuppressWarnings("unchecked")
        private static <T> T staticField(Class<?> clazz, String fieldName) {
            try {
                return (T) clazz.getField(fieldName).get(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
    *///?}
}
