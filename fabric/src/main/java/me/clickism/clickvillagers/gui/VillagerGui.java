/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.gui;

import me.clickism.clickvillagers.villager.VillagerHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class VillagerGui extends DecoratedGui {
    protected final VillagerHandler<?> villagerHandler;

    public VillagerGui(ServerPlayerEntity player, VillagerHandler<?> villagerHandler) {
        super(player);
        this.villagerHandler = villagerHandler;
    }
}
