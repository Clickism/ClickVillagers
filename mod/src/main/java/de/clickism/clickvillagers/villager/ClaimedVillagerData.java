/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.villager;

import net.minecraft.world.entity.npc.villager.VillagerData;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface ClaimedVillagerData {
    static ClaimedVillagerData of(VillagerData data) {
        return (ClaimedVillagerData) (Object) data;
    }

    static ClaimedVillagerData of(Object data) {
        return (ClaimedVillagerData) data;
    }

    @Nullable
    UUID clickVillagers_Fabric$getOwner();
    void clickVillagers_Fabric$setOwner(@Nullable UUID owner);
    
    boolean clickVillagers_Fabric$isTradingOpen();
    void clickVillagers_Fabric$setTradingOpen(boolean open);
}
