/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.entity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public interface EntitySaver {
    String writeToString(Entity entity) throws IllegalArgumentException;

    Entity readAndSpawnAt(String string, EntityType entityType, Location location) throws IllegalArgumentException;
}
