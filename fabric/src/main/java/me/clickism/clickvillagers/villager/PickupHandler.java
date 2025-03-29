/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.villager;

import com.mojang.authlib.GameProfile;
import me.clickism.clickvillagers.anchor.AnchorHandler;
import me.clickism.clickvillagers.util.MessageType;
import me.clickism.clickvillagers.util.Utils;
import me.clickism.clickvillagers.util.VersionHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.particle.ParticleTypes;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

//? if >=1.20.5 {
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.SpawnReason;
//?} else {
/*import net.minecraft.nbt.NbtList;
 *///?}

public class PickupHandler {

    private static final String TYPE_KEY = "EntityType";
    //? if <1.21.1
    /*private static final String DATA_KEY = "ClickVillagersData";*/

    public static <T extends LivingEntity & VillagerDataContainer> ItemStack toItemStack(T entity) {
        NbtCompound nbt = new NbtCompound();
        entity.writeNbt(nbt);
        String id = EntityType.getId(entity.getType()).toString();
        nbt.putString("EntityType", id);
        List<Text> lore = getLore(new VillagerHandler<>(entity));
        ItemStack itemStack = getItemStack(getDisplayName(entity), lore, nbt);
        VillagerTextures.setEntityTexture(itemStack, entity);
        entity.remove(Entity.RemovalReason.DISCARDED);
        return itemStack;
    }

    private static ItemStack getItemStack(Text name, List<Text> lore, NbtCompound nbt) {
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
                && !merchant.getOffers().isEmpty()) {
            lore.add(Text.literal(" "));
            lore.add(Text.literal("🛍 Trades:")
                    .fillStyle(Style.EMPTY.withItalic(false))
                    .formatted(Formatting.GRAY));
            VillagerProfession profession = container.getVillagerData().getProfession();
            TradeInfoProvider provider = TradeInfoProviders.getProvider(profession);
            provider.getTradeInfoLines(merchant.getOffers()).forEach(line -> {
                lore.add(Text.literal(line));
            });
        }
        return lore;
    }

    private static Optional<String> getPlayerName(UUID uuid, MinecraftServer server) {
        UserCache userCache = server.getUserCache();
        if (userCache == null) return Optional.empty();
        return userCache.getByUuid(uuid).map(GameProfile::getName);
    }

    //? if >=1.20.5 {
    private static void writeCustomData(ItemStack itemStack, NbtCompound nbt) {
        itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
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
            // PickupVillagerType type = PickupVillagerType.valueOf(nbt.getString(TYPE_KEY));
            String id = nbt.getString(TYPE_KEY);
            if (id == null) return null;
            EntityType<?> type = EntityType.get(id).orElse(null);
            if (type == null) return null;
            //? if >=1.21.3 {
            Entity entity = type.create(world, SpawnReason.SPAWN_ITEM_USE);
            //?} else
            /*Entity entity = type.create(world);*/
            if (entity == null) return null;
            entity.readNbt(nbt);
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
        VillagerProfession profession = villager.getVillagerData().getProfession();
        if (profession.equals(VillagerProfession.NONE)) {
            return Text.literal("Villager");
        }
        return Text.literal(Utils.titleCase(profession.toString()) + " Villager");
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
