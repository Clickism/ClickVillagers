/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.villager;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.clickism.clickvillagers.ClickVillagers;
import net.fabricmc.fabric.impl.attachment.AttachmentPersistentState;
import net.minecraft.nbt.*;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;
import java.util.*;

public class PartnerState extends PersistentState {

    //? if >=1.21.5 {
    /*public static final String ID = "clickvillagers_PartnerState";

    public static Codec<PartnerState> codec(ServerWorld world) {
        return Codec.of(
            new Encoder<>() {
                @Override
                @SuppressWarnings("unchecked")
                public <T> DataResult<T> encode(PartnerState partnerState, DynamicOps<T> dynamicOps, T t) {
                    NbtCompound compound = new NbtCompound();
                    partnerState.writeNbt(compound, world.getRegistryManager());
                    return DataResult.success((T) compound);
                }
            },
            new Decoder<>() {
                @Override
                public <T> DataResult<Pair<PartnerState, T>> decode(DynamicOps<T> dynamicOps, T t) {
                    NbtCompound compound = (NbtCompound) dynamicOps.convertTo(NbtOps.INSTANCE, t);
                    PartnerState partnerState = createFromNbt(compound, world.getRegistryManager());
                    return DataResult.success(Pair.of(partnerState, dynamicOps.empty()));
                }
            }
        );
    }

    public static PersistentStateType<PartnerState> createType(ServerWorld world) {
        return new PersistentStateType<>(ID, PartnerState::new, codec(world), null);
    }
    *///?} elif >=1.20.5 {
    private static final Type<PartnerState> TYPE = new Type<>(
            PartnerState::new,
            PartnerState::createFromNbt,
            null
    );
    //?}
    
    private final Map<UUID, Set<String>> partners = new HashMap<>();

    //? if <1.21.5
    @Override
    public NbtCompound writeNbt(NbtCompound nbt
            //? if >=1.20.5
            , RegistryWrapper.WrapperLookup registryLookup
    ) {
        NbtCompound compound = new NbtCompound();
        partners.forEach((uuid, set) -> {
            if (set.isEmpty()) return;
            NbtList list = new NbtList();
            set.forEach(partner -> list.add(NbtString.of(partner)));
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
        markDirty();
    }
    
    public void removePartner(UUID uuid, String partner) {
        getPartners(uuid).remove(partner);
        markDirty();
    }
    
    public static PartnerState createFromNbt(NbtCompound nbt
            //? if >=1.20.5
            , RegistryWrapper.WrapperLookup registryLookup
    ) {
        PartnerState state = new PartnerState();
        //? if >=1.21.5 {
        /*NbtCompound compound = nbt.getCompound("TradePartnerMap").orElse(null);
        if (compound == null) return state;
        compound.getKeys().forEach(uuid -> {
            NbtList list = compound.getListOrEmpty(uuid);
            Set<String> set = state.partners.computeIfAbsent(UUID.fromString(uuid), k -> new HashSet<>(list.size()));
            list.forEach(nbtElement -> nbtElement.asString().ifPresent(set::add));
        });
        *///?} else {
        NbtCompound compound = nbt.getCompound("TradePartnerMap");
        compound.getKeys().forEach(uuid -> {
            NbtList list = compound.getList(uuid, NbtElement.STRING_TYPE);
            Set<String> set = state.partners.computeIfAbsent(UUID.fromString(uuid), k -> new HashSet<>(list.size()));
            list.forEach(nbtElement -> set.add(nbtElement.asString()));
        });
        //?}
        return state;
    }
    
    public static PartnerState getServerState(MinecraftServer server) {
        ServerWorld world = server.getWorld(World.OVERWORLD);
        if (world == null) throw new IllegalStateException("Overworld is null");
        PersistentStateManager persistentStateManager = world.getPersistentStateManager();
        //? if >=1.21.5 {
        /*PartnerState state = persistentStateManager.getOrCreate(createType(world));
        *///?} elif >=1.20.5 {
        PartnerState state = persistentStateManager.getOrCreate(TYPE, ClickVillagers.MOD_ID);
        //?} else {
        /*PartnerState state = persistentStateManager.getOrCreate(
                PartnerState::createFromNbt,
                PartnerState::new,
                ClickVillagers.MOD_ID
        );
        *///?}
        state.markDirty();
        return state;
    }
}
