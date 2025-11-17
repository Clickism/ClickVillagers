/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("UnstableApiUsage")
public class SnapshotSaver implements EntitySaver {
    // Pattern to fix the NBT string
    private static final Pattern LEVEL_FIX_PATTERN = Pattern.compile("levels:\\{(.*?)}");
    private static final Matcher MATCHER = LEVEL_FIX_PATTERN.matcher("");

    @Override
    public String writeToString(Entity entity) throws IllegalArgumentException {
        try {
            var snapshot = entity.createSnapshot();
            if (snapshot == null) {
                throw new IllegalArgumentException("Failed to create snapshot for entity: " + entity);
            }
            return snapshot.getAsString();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to create snapshot: " + entity, e);
        }
    }

    @Override
    public Entity readAndSpawnAt(String string, EntityType type, Location location) throws IllegalArgumentException {
        string = getNBTFixedString(string);
        string = appendEntityTypeIfAbsent(string, type);
        try {

            EntitySnapshot snapshot = Bukkit.getEntityFactory().createEntitySnapshot(string);
            return snapshot.createEntity(location);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to read NBT: " + string, e);
        }
    }

    private static String appendEntityTypeIfAbsent(String string, EntityType type) {
        String typeData = "id:\"" + type.getKey() + "\"";
        if (!string.contains(typeData)) {
            StringBuilder builder = new StringBuilder(string);
            builder.setCharAt(builder.length() - 1, ',');
            builder.append(typeData);
            builder.append("}");
            return builder.toString();
        }
        return string;
    }

    private static String getNBTFixedString(String string) {
        MATCHER.reset(string);
        StringBuilder result = new StringBuilder();
        while (MATCHER.find()) {
            MATCHER.appendReplacement(result, Matcher.quoteReplacement(MATCHER.group(1)));
        }
        MATCHER.appendTail(result);
        return result.toString();
    }
}
