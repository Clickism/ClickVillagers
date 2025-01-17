/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.nbt;

import org.bukkit.entity.LivingEntity;

public interface NBTHelper {
    String write(LivingEntity entity) throws IllegalArgumentException;

    void read(LivingEntity entity, String nbtString) throws IllegalArgumentException;
}
