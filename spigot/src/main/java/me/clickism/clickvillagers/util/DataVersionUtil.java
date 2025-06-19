/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.util;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Optional;

public class DataVersionUtil {
    private static Integer dataVersion = null;

    public static Optional<Integer> getDataVersion() {
        if (dataVersion != null) {
            return Optional.of(dataVersion);
        }
        dataVersion = getDataVersionInternal();
        return Optional.ofNullable(dataVersion);
    }

    private static @Nullable Integer getDataVersionInternal() {
        try {
            Class<?> sharedConstantsClass = Class.forName("net.minecraft.SharedConstants");
            Object currentVersion = sharedConstantsClass.getMethod("getCurrentVersion").invoke(null);
            Class<?> currentVersionClass = currentVersion.getClass();
            try {
                Method getDataVersionMethod = currentVersionClass.getMethod("getDataVersion");
                Object dataVersion = getDataVersionMethod.invoke(currentVersion);
                return (Integer) dataVersion.getClass().getMethod("getVersion").invoke(dataVersion);
            } catch (Exception ignored) {
            }
            try {
                Method getDataVersionMethod = currentVersionClass.getMethod("dataVersion");
                Object dataVersion = getDataVersionMethod.invoke(currentVersion);
                return (Integer) dataVersion.getClass().getMethod("version").invoke(dataVersion);
            } catch (Exception ignored) {
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
