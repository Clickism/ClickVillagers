/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.entity;

public class EntitySaverFactory {
    private static boolean isEntitySnapshotSupported;

    static {
        try {
            Class.forName("org.bukkit.entity.EntitySnapshot");
            Class.forName("org.bukkit.entity.EntityFactory");
            isEntitySnapshotSupported = true;
        } catch (ClassNotFoundException exception) {
            isEntitySnapshotSupported = false;
        }
    }

    public static EntitySaver create() {
        if (isEntitySnapshotSupported) {
            return new SnapshotSaver();
        }
        return new NMSSaver();
    }
}
