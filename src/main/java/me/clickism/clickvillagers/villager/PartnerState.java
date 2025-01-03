package me.clickism.clickvillagers.villager;

import me.clickism.clickvillagers.ClickVillagers;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import java.util.*;

public class PartnerState extends PersistentState {
    
    //? if >=1.21.1 {
    private static final Type<PartnerState> type = new Type<>(
            PartnerState::new,
            PartnerState::createFromNbt,
            null
    );
    //?}
    
    private final Map<UUID, Set<String>> partners = new HashMap<>();
    
    @Override
    public NbtCompound writeNbt(NbtCompound nbt
            //? if >=1.21.1
            , RegistryWrapper.WrapperLookup registries
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
            //? if >=1.21.1
            , RegistryWrapper.WrapperLookup registryLookup
    ) {
        PartnerState state = new PartnerState();
        NbtCompound compound = nbt.getCompound("TradePartnerMap");
        compound.getKeys().forEach(uuid -> {
            NbtList list = compound.getList(uuid, NbtElement.STRING_TYPE);
            Set<String> set = state.partners.computeIfAbsent(UUID.fromString(uuid), k -> new HashSet<>(list.size()));
            list.forEach(nbtElement -> set.add(nbtElement.asString()));
        });
        return state;
    }
    
    public static PartnerState getServerState(MinecraftServer server) {
        ServerWorld world = server.getWorld(World.OVERWORLD);
        if (world == null) throw new IllegalStateException("Overworld is null");
        PersistentStateManager persistentStateManager = world.getPersistentStateManager();
        //? if >=1.21.1 {
        PartnerState state = persistentStateManager.getOrCreate(type, ClickVillagers.MOD_ID);
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
