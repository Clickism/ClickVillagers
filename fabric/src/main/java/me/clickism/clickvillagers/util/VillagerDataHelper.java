/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.util;

import me.clickism.clickvillagers.villager.VillagerHandler;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;

public class VillagerDataHelper {
    private final VillagerData data;

    protected VillagerDataHelper(VillagerData data) {
        this.data = data;
    }

    public VillagerProfession getProfession() {
        return data.profession().value();
    }

    public int getLevel() {
        return data.level();
    }

    public VillagerType getType() {
        return data.type().value();
    }

    public static VillagerDataHelper of(VillagerData data) {
        return new VillagerDataHelper(data);
    }

    public static VillagerDataHelper of(VillagerHandler<?> villager) {
        return of(villager.getEntity().getVillagerData());
    }
}
