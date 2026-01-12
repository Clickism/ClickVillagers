/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.villager;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.villager.VillagerDataHolder;
import org.jetbrains.annotations.Nullable;
import java.util.UUID;

public class VillagerHandler<T extends LivingEntity & VillagerDataHolder> {
    protected final T entity;
    public VillagerHandler(T entity) {
        this.entity = entity;
    }
    
    public T getEntity() {
        return entity;
    }

    public void setOwner(UUID uuid) {
        ClaimedVillagerData.of(entity.getVillagerData())
                .clickVillagers_Fabric$setOwner(uuid);
    }
    
    @Nullable
    public UUID getOwner() {
        return ClaimedVillagerData.of(entity.getVillagerData())
                .clickVillagers_Fabric$getOwner();
    }
    
    public boolean hasOwner() {
        return getOwner() != null;
    }
    
    public boolean isOwner(UUID uuid) {
        return uuid.equals(getOwner());
    }
    
    public boolean isTradingOpen() {
        return ClaimedVillagerData.of(entity.getVillagerData())
                .clickVillagers_Fabric$isTradingOpen();
    }
    
    public void setTradingOpen(boolean open) {
        ClaimedVillagerData.of(entity.getVillagerData())
                .clickVillagers_Fabric$setTradingOpen(open);
    }
}
