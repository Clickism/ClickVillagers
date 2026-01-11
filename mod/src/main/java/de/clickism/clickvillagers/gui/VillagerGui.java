/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.gui;

import de.clickism.clickvillagers.villager.VillagerHandler;
import net.minecraft.server.level.ServerPlayer;

public abstract class VillagerGui extends DecoratedGui {
    protected final VillagerHandler<?> villagerHandler;

    public VillagerGui(ServerPlayer player, VillagerHandler<?> villagerHandler) {
        super(player);
        this.villagerHandler = villagerHandler;
    }
}
