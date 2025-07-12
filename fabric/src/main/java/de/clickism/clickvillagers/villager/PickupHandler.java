/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.villager;

import com.mojang.authlib.GameProfile;
import de.clickism.clickvillagers.anchor.AnchorHandler;
import de.clickism.clickvillagers.util.MessageType;
import de.clickism.clickvillagers.util.NbtFixer;
import de.clickism.clickvillagers.util.Utils;
import de.clickism.clickvillagers.util.VersionHelper;
import net.minecraft.SharedConstants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.UserCache;
import net.minecraft.village.Merchant;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import java.util.*;
import static de.clickism.clickvillagers.ClickVillagersConfig.*;
//? if >=1.21.6 {
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.ReadView;
import net.minecraft.util.ErrorReporter;
//?}
//? if >=1.20.5 {
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
//?} else {
/*import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
 *///?}

public class PickupHandler {

    private static final String TYPE_KEY = "EntityType";
    private static final String DATA_VERSION_KEY = "CVDataVersion";
    //? if <1.21.1
    /*private static final String DATA_KEY = "ClickVillagersData";*/
    //? if >=1.21.6 {
    private static final int DATA_VERSION = SharedConstants.getGameVersion().dataVersion().id();
    //?} else
    /*private static final int DATA_VERSION = SharedConstants.getGameVersion().getSaveVersion().getId();*/

    //? if >=1.21.6 {
    public static <T extends LivingEntity & VillagerDataContainer> ItemStack toItemStack(T entity) {
        NbtWriteView view = NbtWriteView.create(new ErrorReporter.Impl(), entity.getWorld().getRegistryManager());
        entity.writeData(view);
        String id = EntityType.getId(entity.getType()).toString();
        view.putString(TYPE_KEY, id);
        view.putString(DATA_VERSION_KEY, String.valueOf(DATA_VERSION));
        List<Text> lore = getLore(new VillagerHandler<>(entity));
        ItemStack itemStack = getItemStack(getDisplayName(entity), lore, view);
        VillagerTextures.setEntityTexture(itemStack, entity);
        entity.remove(Entity.RemovalReason.DISCARDED);
        return itemStack;
    }
    //?} else {
    /*public static <T extends LivingEntity & VillagerDataContainer> ItemStack toItemStack(T entity) {
        NbtCompound nbt = new NbtCompound();
        entity.writeNbt(nbt);
        String id = EntityType.getId(entity.getType()).toString();
        nbt.putString(TYPE_KEY, id);
        nbt.putString(DATA_VERSION_KEY, String.valueOf(DATA_VERSION));
        List<Text> lore = getLore(new VillagerHandler<>(entity));
        ItemStack itemStack = getItemStack(getDisplayName(entity), lore, nbt);
        VillagerTextures.setEntityTexture(itemStack, entity);
        entity.remove(Entity.RemovalReason.DISCARDED);
        return itemStack;
    }
    *///?}

    private static ItemStack getItemStack(Text name, List<Text> lore,
                                          //? if >=1.21.6 {
                                          NbtWriteView nbt
                                          //?} else
                                          /*NbtCompound nbt*/
    ) {
        ItemStack itemStack = Items.PLAYER_HEAD.getDefaultStack();
        writeCustomData(itemStack, nbt);
        formatItem(itemStack, name.copy().fillStyle(Style.EMPTY.withItalic(false).withColor(Formatting.YELLOW)), lore);
        return itemStack;
    }

    private static List<Text> getLore(VillagerHandler<?> villager) {
        List<Text> lore = new ArrayList<>();
        lore.add(Text.literal("Right click to place the villager back.")
                .fillStyle(Style.EMPTY.withItalic(false))
                .formatted(Formatting.DARK_GRAY));
        LivingEntity entity = villager.getEntity();
        if (villager.hasOwner()) {
            UUID ownerUuid = villager.getOwner();
            MinecraftServer server = entity.getServer();
            String ownerName = (server != null)
                    ? getPlayerName(ownerUuid, server).orElse("?")
                    : "?";
            lore.add(Text.literal("🔑 Owner: ")
                    .fillStyle(Style.EMPTY.withItalic(false))
                    .formatted(Formatting.GOLD)
                    .append(Text.literal(ownerName).formatted(Formatting.RESET).formatted(Formatting.WHITE)));
        }
        if (AnchorHandler.isAnchored(entity)) {
            lore.add(Text.literal("⚓ Anchored")
                    .fillStyle(Style.EMPTY.withItalic(false))
                    .formatted(Formatting.DARK_AQUA));
        }
        if (!villager.isTradingOpen()) {
            lore.add(Text.literal("👥 Trading Closed")
                    .fillStyle(Style.EMPTY.withItalic(false))
                    .formatted(Formatting.RED));
        }
        if (entity instanceof Merchant merchant && entity instanceof VillagerDataContainer container
                && !merchant.getOffers().isEmpty() && CONFIG.get(SHOW_TRADES)) {
            //? if >=1.21.5 {
            RegistryKey<VillagerProfession> profession = container.getVillagerData().profession()
                    .getKey().orElseThrow();
            //?} else
            /*VillagerProfession profession = container.getVillagerData().getProfession();*/
            TradeInfoProvider provider = (CONFIG.get(FORMAT_TRADES))
                    ? TradeInfoProviders.getProvider(profession)
                    : TradeInfoProviders.ALL_TRADES;
            List<String> tradeInfoLines = provider.getTradeInfoLines(merchant.getOffers());
            if (!tradeInfoLines.isEmpty()) {
                lore.add(Text.literal(" "));
                lore.add(Text.literal("🛍 Trades:")
                        .fillStyle(Style.EMPTY.withItalic(false))
                        .formatted(Formatting.GRAY));
                tradeInfoLines.forEach(line -> {
                    lore.add(Text.literal(line));
                });
            }
        }
        return lore;
    }

    private static Optional<String> getPlayerName(UUID uuid, MinecraftServer server) {
        UserCache userCache = server.getUserCache();
        if (userCache == null) return Optional.empty();
        return userCache.getByUuid(uuid).map(GameProfile::getName);
    }

    //? if >=1.20.5 {
    private static void writeCustomData(ItemStack itemStack,
                                        //? if >=1.21.6 {
                                        NbtWriteView view
                                        //?} else
                                        /*NbtCompound nbt*/
    ) {
        itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(
                //? if >=1.21.6 {
                view.getNbt()
                //?} else
                /*nbt*/
        ));
    }

    @Nullable
    private static NbtCompound readCustomData(ItemStack itemStack) {
        NbtComponent nbt = itemStack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbt == null) return null;
        return nbt.copyNbt();
    }

    private static void formatItem(ItemStack itemStack, Text name, List<Text> lore) {
        itemStack.set(DataComponentTypes.ITEM_NAME, name);
        itemStack.set(DataComponentTypes.LORE, new LoreComponent(lore));
    }
    //?} else {
    /*private static void writeCustomData(ItemStack itemStack, NbtCompound nbt) {
        itemStack.getOrCreateNbt().put(DATA_KEY, nbt);
    }

    @Nullable
    private static NbtCompound readCustomData(ItemStack itemStack) {
        NbtCompound nbt = itemStack.getNbt();
        if (nbt == null) return null;
        return nbt.getCompound(DATA_KEY);
    }

    private static void formatItem(ItemStack itemStack, Text name, List<Text> lore) {
        NbtList list = new NbtList();
        lore.forEach(text -> list.add(NbtString.of(Text.Serializer.toJson(text))));
        NbtCompound display = itemStack.getOrCreateSubNbt("display");
        display.put("Lore", list);
        display.put("Name", NbtString.of(Text.Serializer.toJson(name)));
    }
    *///?}

    public static boolean isVillager(ItemStack itemStack) {
        return readCustomData(itemStack) != null;
    }

    @Nullable
    public static Entity readEntityFromItemStack(World world, ItemStack itemStack) {
        try {
            NbtCompound nbt = readCustomData(itemStack);
            if (nbt == null) return null;
            MinecraftServer server = world.getServer();
            if (server == null) return null;
            //? if >=1.21.5
            NbtFixer.applyDataFixes(nbt);
            // PickupVillagerType type = PickupVillagerType.valueOf(nbt.getString(TYPE_KEY));
            //? if >=1.21.5 {
            String id = nbt.getString(TYPE_KEY).orElseThrow();
            //?} else
            /*String id = nbt.getString(TYPE_KEY);*/
            if (id == null) return null;
            EntityType<?> type = EntityType.get(id).orElse(null);
            if (type == null) return null;
            //? if >=1.21.3 {
            Entity entity = type.create(world, SpawnReason.SPAWN_ITEM_USE);
            //?} else
            /*Entity entity = type.create(world);*/
            if (entity == null) return null;
            //? if >=1.21.6 {
            ReadView view = NbtReadView.create(new ErrorReporter.Impl(), world.getRegistryManager(), nbt);
            entity.readData(view);
            //?} else
            /*entity.readNbt(nbt);*/
            return entity;
        } catch (Exception e) {
            return null;
        }
    }

    private static MutableText getDisplayName(Entity entity) {
        if (entity.hasCustomName()) {
            return Text.literal("\"").append(entity.getCustomName()).append("\"");
        }
        if (entity instanceof VillagerEntity villager) {
            return getVillagerDisplayName(villager);
        }
        if (entity instanceof ZombieVillagerEntity) {
            return Text.literal("Zombie Villager");
        }
        return Text.literal("Unknown");
    }

    private static MutableText getVillagerDisplayName(VillagerEntity villager) {
        if (villager.isBaby()) {
            return Text.literal("Baby Villager");
        }
        //? if >=1.21.5 {
        RegistryEntry<VillagerProfession> profession = villager.getVillagerData().profession();
        String professionName = profession.getKey().orElseThrow().getValue().getPath();
        if (profession.matchesKey(VillagerProfession.NONE)) {
            return Text.literal("Villager");
        }
        //?} else {
        /*VillagerProfession profession = villager.getVillagerData().getProfession();
        String professionName = profession.toString();
        if (profession.equals(VillagerProfession.NONE)) {
            return Text.literal("Villager");
        }
        *///?}

        return Text.literal(Utils.titleCase(professionName) + " Villager");
    }

    public static void notifyPickup(PlayerEntity player, Entity entity) {
        MessageType.PICKUP_MESSAGE.sendActionbarSilently(player, Text.literal("You picked up a villager"));
        ServerWorld world = (ServerWorld) player.getWorld();
        double x = entity.getX();
        double y = entity.getY() + .25f;
        double z = entity.getZ();
        world.spawnParticles(ParticleTypes.SWEEP_ATTACK, x, y, z, 1, 0, 0, 0, 1);
        VersionHelper.playSound(player, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.NEUTRAL, 1, .5f);
    }
}
