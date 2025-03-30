/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SnapshotSaver implements EntitySaver {
    // Reflection classes and methods
    private static final Class<?> ENTITY_SNAPSHOT_CLASS;
    private static final Class<?> ENTITY_FACTORY_CLASS;
    private static final Method CREATE_SNAPSHOT_METHOD;
    private static final Method GET_AS_STRING_METHOD;

    private static final Method GET_FACTORY_METHOD;
    private static final Method CREATE_ENTITY_SNAPSHOT_METHOD;
    private static final Method CREATE_ENTITY_METHOD;

    // Pattern to fix the NBT string
    private static final Pattern LEVEL_FIX_PATTERN = Pattern.compile("levels:\\{(.*?)}");
    private static final Matcher MATCHER = LEVEL_FIX_PATTERN.matcher("");

    static {
        try {
            ENTITY_SNAPSHOT_CLASS = Class.forName("org.bukkit.entity.EntitySnapshot");
            GET_AS_STRING_METHOD = ENTITY_SNAPSHOT_CLASS.getMethod("getAsString");
            CREATE_SNAPSHOT_METHOD = Entity.class.getMethod("createSnapshot");
            GET_FACTORY_METHOD = Bukkit.class.getMethod("getEntityFactory");
            ENTITY_FACTORY_CLASS = Class.forName("org.bukkit.entity.EntityFactory");
            CREATE_ENTITY_SNAPSHOT_METHOD = ENTITY_FACTORY_CLASS.getMethod("createEntitySnapshot", String.class);
            CREATE_ENTITY_METHOD = ENTITY_SNAPSHOT_CLASS.getMethod("createEntity", Location.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException("Failed to initialize SnapshotSaver", e);
        }
    }

    @Override
    public String writeToString(Entity entity) throws IllegalArgumentException {
        try {
            Object snapshot = CREATE_SNAPSHOT_METHOD.invoke(entity);
            if (snapshot == null) {
                throw new IllegalArgumentException("Failed to create snapshot for entity: " + entity);
            }
            return (String) GET_AS_STRING_METHOD.invoke(snapshot);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to create snapshot: " + entity, e);
        }
    }

    @Override
    public Entity readAndSpawnAt(String string, EntityType type, Location location) throws IllegalArgumentException {
        string = getNBTFixedString(string);
        string = appendEntityTypeIfAbsent(string, type);
        try {
            Object factory = GET_FACTORY_METHOD.invoke(null);
            Object snapshot = CREATE_ENTITY_SNAPSHOT_METHOD.invoke(factory, string);
            return (Entity) CREATE_ENTITY_METHOD.invoke(snapshot, location);
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
