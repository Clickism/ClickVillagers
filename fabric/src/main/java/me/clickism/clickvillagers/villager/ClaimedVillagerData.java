/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.villager;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface ClaimedVillagerData {
    @Nullable
    UUID clickVillagers_Fabric$getOwner();
    void clickVillagers_Fabric$setOwner(@Nullable UUID owner);
    
    boolean clickVillagers_Fabric$isTradingOpen();
    void clickVillagers_Fabric$setTradingOpen(boolean open);
}
