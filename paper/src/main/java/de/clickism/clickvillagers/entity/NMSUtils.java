/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.entity;

import org.bukkit.Bukkit;

public class NMSUtils {
    private static final String CRAFTBUKKIT_PACKAGE = Bukkit.getServer().getClass().getPackageName();

    public static String cbClass(String clazz) {
        return CRAFTBUKKIT_PACKAGE + "." + clazz;
    }
}
