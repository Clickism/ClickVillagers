package me.clickism.clickvillagers.gui;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.village.VillagerDataContainer;

public abstract class VillagerGui<T extends LivingEntity & VillagerDataContainer> extends DecoratedGui {
    protected final T villager;

    public VillagerGui(ServerPlayerEntity player, T villager) {
        super(player);
        this.villager = villager;
    }
}
