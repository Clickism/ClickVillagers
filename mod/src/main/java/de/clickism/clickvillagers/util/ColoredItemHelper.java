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

    /*public static final ColorCollection<Item> STAINED_GLASS_PANE = new ColorCollection<>(
            Items.WHITE_STAINED_GLASS_PANE,
            Items.ORANGE_STAINED_GLASS_PANE,
            Items.MAGENTA_STAINED_GLASS_PANE,
            Items.LIGHT_BLUE_STAINED_GLASS_PANE,
            Items.YELLOW_STAINED_GLASS_PANE,
            Items.LIME_STAINED_GLASS_PANE,
            Items.PINK_STAINED_GLASS_PANE,
            Items.GRAY_STAINED_GLASS_PANE,
            Items.LIGHT_GRAY_STAINED_GLASS_PANE,
            Items.CYAN_STAINED_GLASS_PANE,
            Items.PURPLE_STAINED_GLASS_PANE,
            Items.BLUE_STAINED_GLASS_PANE,
            Items.BROWN_STAINED_GLASS_PANE,
            Items.GREEN_STAINED_GLASS_PANE,
            Items.RED_STAINED_GLASS_PANE,
            Items.BLACK_STAINED_GLASS_PANE
    );

    public static final ColorCollection<Item> GLAZED_TERRACOTTA = new ColorCollection<>(
            Items.WHITE_GLAZED_TERRACOTTA,
            Items.ORANGE_GLAZED_TERRACOTTA,
            Items.MAGENTA_GLAZED_TERRACOTTA,
            Items.LIGHT_BLUE_GLAZED_TERRACOTTA,
            Items.YELLOW_GLAZED_TERRACOTTA,
            Items.LIME_GLAZED_TERRACOTTA,
            Items.PINK_GLAZED_TERRACOTTA,
            Items.GRAY_GLAZED_TERRACOTTA,
            Items.LIGHT_GRAY_GLAZED_TERRACOTTA,
            Items.CYAN_GLAZED_TERRACOTTA,
            Items.PURPLE_GLAZED_TERRACOTTA,
            Items.BLUE_GLAZED_TERRACOTTA,
            Items.BROWN_GLAZED_TERRACOTTA,
            Items.GREEN_GLAZED_TERRACOTTA,
            Items.RED_GLAZED_TERRACOTTA,
            Items.BLACK_GLAZED_TERRACOTTA
    );

    public static final ColorCollection<Item> DYED_TERRACOTTA = new ColorCollection<>(
            Items.WHITE_TERRACOTTA,
            Items.ORANGE_TERRACOTTA,
            Items.MAGENTA_TERRACOTTA,
            Items.LIGHT_BLUE_TERRACOTTA,
            Items.YELLOW_TERRACOTTA,
            Items.LIME_TERRACOTTA,
            Items.PINK_TERRACOTTA,
            Items.GRAY_TERRACOTTA,
            Items.LIGHT_GRAY_TERRACOTTA,
            Items.CYAN_TERRACOTTA,
            Items.PURPLE_TERRACOTTA,
            Items.BLUE_TERRACOTTA,
            Items.BROWN_TERRACOTTA,
            Items.GREEN_TERRACOTTA,
            Items.RED_TERRACOTTA,
            Items.BLACK_TERRACOTTA
    );

    public static final ColorCollection<Item> WOOL = new ColorCollection<>(
            Items.WHITE_WOOL,
            Items.ORANGE_WOOL,
            Items.MAGENTA_WOOL,
            Items.LIGHT_BLUE_WOOL,
            Items.YELLOW_WOOL,
            Items.LIME_WOOL,
            Items.PINK_WOOL,
            Items.GRAY_WOOL,
            Items.LIGHT_GRAY_WOOL,
            Items.CYAN_WOOL,
            Items.PURPLE_WOOL,
            Items.BLUE_WOOL,
            Items.BROWN_WOOL,
            Items.GREEN_WOOL,
            Items.RED_WOOL,
            Items.BLACK_WOOL
    );
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
    }
    *///?}
}
