package me.clickism.clickvillagers.villager;

import net.minecraft.entity.LivingEntity;
import net.minecraft.village.VillagerDataContainer;
import org.jetbrains.annotations.Nullable;
import java.util.UUID;

public class VillagerHandler<T extends LivingEntity & VillagerDataContainer> {
    protected final T entity;
    public VillagerHandler(T entity) {
        this.entity = entity;
    }
    
    public T getEntity() {
        return entity;
    }

    public void setOwner(UUID uuid) {
        ((ClaimedVillagerData) entity.getVillagerData()).clickVillagers_Fabric$setOwner(uuid);
    }
    
    @Nullable
    public UUID getOwner() {
        return ((ClaimedVillagerData) entity.getVillagerData()).clickVillagers_Fabric$getOwner();
    }
    
    public boolean hasOwner() {
        return getOwner() != null;
    }
    
    public boolean isOwner(UUID uuid) {
        return uuid.equals(getOwner());
    }
    
    public boolean isTradingOpen() {
        return ((ClaimedVillagerData) entity.getVillagerData()).clickVillagers_Fabric$isTradingOpen();
    }
    
    public void setTradingOpen(boolean open) {
        ((ClaimedVillagerData) entity.getVillagerData()).clickVillagers_Fabric$setTradingOpen(open);
    }
}