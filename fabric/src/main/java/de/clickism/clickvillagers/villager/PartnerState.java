/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.villager;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import de.clickism.clickvillagers.ClickVillagers;
import net.minecraft.nbt.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
//? if >=1.21.5
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.Level;
import java.util.*;

public class PartnerState extends SavedData {

    //? if >=1.21.5 {
    @SuppressWarnings("unchecked")
    public static Codec<PartnerState> codec(ServerLevel world) {
        return Codec.of(new Encoder<>() {
            @Override
            public <T> DataResult<T> encode(PartnerState input, DynamicOps<T> ops, T prefix) {
                CompoundTag nbtCompound = new CompoundTag();
                input.save(nbtCompound, world.registryAccess());
                return DataResult.success((T) nbtCompound);
            }
        }, new Decoder<>() {
            @Override
            public <T> DataResult<Pair<PartnerState, T>> decode(DynamicOps<T> ops, T input) {
                CompoundTag nbtCompound = (CompoundTag) ops.convertTo(NbtOps.INSTANCE, input);
                PartnerState partnerState = createFromNbt(nbtCompound, world.registryAccess());
                return DataResult.success(Pair.of(partnerState, ops.empty()));
            }
        });
    }
    //?} elif >=1.20.5 {
    /*private static final Factory<PartnerState> type = new Factory<>(
            PartnerState::new,
            PartnerState::createFromNbt,
            null
    );
    *///?}
    
    private final Map<UUID, Set<String>> partners = new HashMap<>();

    //? if <1.21.5
    //@Override
    public CompoundTag save(CompoundTag nbt
                                //? if >=1.20.5
            , HolderLookup.Provider registries
    ) {
        CompoundTag compound = new CompoundTag();
        partners.forEach((uuid, set) -> {
            if (set.isEmpty()) return;
            ListTag list = new ListTag();
            set.forEach(partner -> list.add(StringTag.valueOf(partner)));
            compound.put(uuid.toString(), list);
        });
        nbt.put("TradePartnerMap", compound);
        return nbt;
    }
    
    public Set<String> getPartners(UUID uuid) {
        return partners.computeIfAbsent(uuid, k -> new HashSet<>());
    }
    
    public boolean isPartner(UUID uuid, String partner) {
        return getPartners(uuid).contains(partner);
    }
    
    public void addPartner(UUID uuid, String partner) {
        getPartners(uuid).add(partner);
        setDirty();
    }
    
    public void removePartner(UUID uuid, String partner) {
        getPartners(uuid).remove(partner);
        setDirty();
    }
    
    public static PartnerState createFromNbt(CompoundTag nbt
                                             //? if >=1.20.5
            , HolderLookup.Provider registryLookup
    ) {
        PartnerState state = new PartnerState();
        //? if >=1.21.5 {
        CompoundTag compound = nbt.getCompound("TradePartnerMap").orElseThrow();
        //?} else
        //CompoundTag compound = nbt.getCompound("TradePartnerMap");
        compound
                //? if >=1.21.5 {
                .keySet()
                //?} else
                //.getAllKeys()
                .forEach(uuid -> {
            //? if >=1.21.5 {
            ListTag list = compound.getListOrEmpty(uuid);
            //?} else
            //ListTag list = compound.getList(uuid, Tag.TAG_STRING);
            Set<String> set = state.partners.computeIfAbsent(UUID.fromString(uuid), k -> new HashSet<>(list.size()));
            list.forEach(nbtElement -> set.add(
                    //? if >=1.21.5 {
                    nbtElement.asString().orElseThrow()
                    //?} else
                    //nbtElement.getAsString()
            ));
        });
        return state;
    }
    
    public static PartnerState getServerState(MinecraftServer server) {
        ServerLevel world = server.getLevel(Level.OVERWORLD);
        if (world == null) throw new IllegalStateException("Overworld is null");
        DimensionDataStorage persistentStateManager = world.getDataStorage();
        //? if >=1.21.5 {
        SavedDataType<PartnerState> type = new SavedDataType<>(
                ClickVillagers.MOD_ID,
                PartnerState::new,
                codec(world),
                null
        );
        PartnerState state = persistentStateManager.computeIfAbsent(type);
        //?} elif >=1.20.5 {
        /*PartnerState state = persistentStateManager.computeIfAbsent(type, ClickVillagers.MOD_ID);
        *///?} else {
        /*PartnerState state = persistentStateManager.getOrCreate(
                PartnerState::createFromNbt,
                PartnerState::new,
                ClickVillagers.MOD_ID
        );
        *///?}
        state.setDirty();
        return state;
    }
}
