/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.villager;

import de.clickism.clickvillagers.anchor.AnchorHandler;
import de.clickism.clickvillagers.util.MessageType;
import de.clickism.clickvillagers.util.NbtFixer;
import de.clickism.clickvillagers.util.Utils;
import de.clickism.clickvillagers.util.VersionHelper;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.commands.TagCommand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.entity.npc.villager.VillagerDataHolder;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import java.util.*;
import static de.clickism.clickvillagers.ClickVillagersConfig.*;
//? if >=1.21.6 {
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.util.ProblemReporter;
//?}
//? if >=1.20.5 {
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.CustomData;
//?} else {
/*import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
 *///?}

//? if >=1.21.4
import net.minecraft.world.entity.EntitySpawnReason;

public class PickupHandler {

    private static final String TYPE_KEY = "EntityType";
    private static final String DATA_VERSION_KEY = "CVDataVersion";
    //? if <1.21.1
    /*private static final String DATA_KEY = "ClickVillagersData";*/
    //? if >=1.21.6 {
    private static final int DATA_VERSION = SharedConstants.getCurrentVersion().dataVersion().version();
    //?} else
    //private static final int DATA_VERSION = SharedConstants.getCurrentVersion().getDataVersion().getVersion();

    //? if >=1.21.6 {
    public static <T extends LivingEntity & VillagerDataHolder> ItemStack toItemStack(T entity) {
        TagValueOutput view = TagValueOutput.createWithContext(new ProblemReporter.Collector(), VersionHelper.getWorld(entity).registryAccess());
        entity.saveWithoutId(view);
        String id = EntityType.getKey(entity.getType()).toString();
        view.putString(TYPE_KEY, id);
        view.putString(DATA_VERSION_KEY, String.valueOf(DATA_VERSION));
        List<Component> lore = getLore(new VillagerHandler<>(entity));
        MutableComponent displayName = getDisplayName(entity);
        ItemStack itemStack = getItemStack(displayName, lore, view);
        VillagerTextures.setEntityTexture(itemStack, entity);
        entity.remove(Entity.RemovalReason.DISCARDED);
        return itemStack;
    }
    //?} else {
    /*public static <T extends LivingEntity & VillagerDataHolder> ItemStack toItemStack(T entity) {
        CompoundTag nbt = new CompoundTag();
        entity.save(nbt);
        String id = EntityType.getKey(entity.getType()).toString();
        nbt.putString(TYPE_KEY, id);
        nbt.putString(DATA_VERSION_KEY, String.valueOf(DATA_VERSION));
        List<Component> lore = getLore(new VillagerHandler<>(entity));
        ItemStack itemStack = getItemStack(getDisplayName(entity), lore, nbt);
        VillagerTextures.setEntityTexture(itemStack, entity);
        entity.remove(Entity.RemovalReason.DISCARDED);
        return itemStack;
    }
    *///?}

    private static ItemStack getItemStack(Component name, List<Component> lore,
                                          //? if >=1.21.6 {
                                          TagValueOutput nbt
                                          //?} else
                                          //CompoundTag nbt
    ) {
        ItemStack itemStack = Items.PLAYER_HEAD.getDefaultInstance();
        writeCustomData(itemStack, nbt);
        formatItem(itemStack, name.copy().withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)), lore);
        return itemStack;
    }

    private static List<Component> getLore(VillagerHandler<?> villager) {
        List<Component> lore = new ArrayList<>();
        lore.add(Component.literal("Right click to place the villager back.")
                .withStyle(Style.EMPTY.withItalic(false))
                .withStyle(ChatFormatting.DARK_GRAY));
        LivingEntity entity = villager.getEntity();
        if (villager.hasOwner()) {
            UUID ownerUuid = villager.getOwner();
            MinecraftServer server = VersionHelper.getServer(entity);
            String ownerName = (server != null)
                    ? VersionHelper.getPlayerName(ownerUuid, server).orElse("?")
                    : "?";
            lore.add(Component.literal("🔑 Owner: ")
                    .withStyle(Style.EMPTY.withItalic(false))
                    .withStyle(ChatFormatting.GOLD)
                    .append(Component.literal(ownerName).withStyle(ChatFormatting.RESET).withStyle(ChatFormatting.WHITE)));
        }
        if (AnchorHandler.isAnchored(entity)) {
            lore.add(Component.literal("⚓ Anchored")
                    .withStyle(Style.EMPTY.withItalic(false))
                    .withStyle(ChatFormatting.DARK_AQUA));
        }
        if (!villager.isTradingOpen()) {
            lore.add(Component.literal("👥 Trading Closed")
                    .withStyle(Style.EMPTY.withItalic(false))
                    .withStyle(ChatFormatting.RED));
        }
        if (entity instanceof Merchant merchant && entity instanceof VillagerDataHolder container
            && !merchant.getOffers().isEmpty() && SHOW_TRADES.get()) {
            //? if >=1.21.5 {
            ResourceKey<VillagerProfession> profession = container.getVillagerData().profession()
                    .unwrapKey().orElseThrow();
            //?} else
            //VillagerProfession profession = container.getVillagerData().getProfession();
            TradeInfoProvider provider = (FORMAT_TRADES.get())
                    ? TradeInfoProviders.getProvider(profession)
                    : TradeInfoProviders.ALL_TRADES;
            List<String> tradeInfoLines = provider.getTradeInfoLines(merchant.getOffers());
            if (!tradeInfoLines.isEmpty()) {
                lore.add(Component.literal(" "));
                lore.add(Component.literal("🛍 Trades:")
                        .withStyle(Style.EMPTY.withItalic(false))
                        .withStyle(ChatFormatting.GRAY));
                tradeInfoLines.forEach(line -> {
                    lore.add(Component.literal(line));
                });
            }
        }
        return lore;
    }

    //? if >=1.20.5 {
    private static void writeCustomData(ItemStack itemStack,
                                        //? if >=1.21.6 {
                                        TagValueOutput view
                                        //?} else
                                        //CompoundTag nbt
    ) {
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(
                //? if >=1.21.6 {
                view.buildResult()
                //?} else
                //nbt
        ));
    }

    @Nullable
    private static CompoundTag readCustomData(ItemStack itemStack) {
        CustomData nbt = itemStack.get(DataComponents.CUSTOM_DATA);
        if (nbt == null) return null;
        return nbt.copyTag();
    }

    private static void formatItem(ItemStack itemStack, Component name, List<Component> lore) {
        itemStack.set(DataComponents.ITEM_NAME, name);
        itemStack.set(DataComponents.CUSTOM_NAME, name);
        itemStack.set(DataComponents.LORE, new ItemLore(lore));
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
    public static Entity readEntityFromItemStack(Level world, ItemStack itemStack) {
        try {
            CompoundTag nbt = readCustomData(itemStack);
            if (nbt == null) return null;
            removeUuidIfDuplicate(nbt, world);
            MinecraftServer server = world.getServer();
            if (server == null) return null;
            //? if >=1.21.5
            NbtFixer.applyDataFixes(nbt);
            // PickupVillagerType type = PickupVillagerType.valueOf(nbt.getString(TYPE_KEY));
            //? if >=1.21.5 {
            String id = nbt.getString(TYPE_KEY).orElseThrow();
            //?} else
            //String id = nbt.getString(TYPE_KEY);
            if (id == null) return null;
            EntityType<?> type = EntityType.byString(id).orElse(null);
            if (type == null) return null;
            //? if >=1.21.3 {
            Entity entity = type.create(world, EntitySpawnReason.SPAWN_ITEM_USE);
            //?} else
            //Entity entity = type.create(world);
            if (entity == null) return null;
            //? if >=1.21.6 {
            ValueInput view = TagValueInput.create(new ProblemReporter.Collector(), world.registryAccess(), nbt);
            entity.load(view);
            //?} else
            //entity.load(nbt);
            return entity;
        } catch (Exception e) {
            return null;
        }
    }

    private static void removeUuidIfDuplicate(CompoundTag nbt, Level world) {
        if (!nbt.contains("UUID")) return;
        //? if >=1.21.5 {
        nbt.read("UUID", UUIDUtil.CODEC).ifPresent(uuid -> {
            if (world.getEntity(uuid) != null) {
                nbt.remove("UUID");
            }
        });
        //?} else {
        /*UUID uuid = nbt.getUUID("UUID");
        if (((ServerLevel) world).getEntity(uuid) != null) {
            nbt.remove("UUID");
        }
        *///?}
    }

    private static MutableComponent getDisplayName(Entity entity) {
        if (entity.hasCustomName()) {
            return Component.literal("\"").append(entity.getCustomName()).append("\"");
        }
        if (entity instanceof Villager villager) {
            return getVillagerDisplayName(villager);
        }
        if (entity instanceof ZombieVillager) {
            return Component.literal("Zombie Villager");
        }
        return Component.literal("Unknown");
    }

    private static MutableComponent getVillagerDisplayName(Villager villager) {
        if (villager.isBaby()) {
            return Component.literal("Baby Villager");
        }
        //? if >=1.21.5 {
        Holder<VillagerProfession> profession = villager.getVillagerData().profession();
        String professionName = VersionHelper.identifier(profession.unwrapKey().orElseThrow()).getPath();
        if (profession.is(VillagerProfession.NONE)) {
            return Component.literal("Villager");
        }
        //?} else {
        /*VillagerProfession profession = villager.getVillagerData().getProfession();
        String professionName = profession.toString();
        if (profession.equals(VillagerProfession.NONE)) {
            return Component.literal("Villager");
        }
        *///?}

        return Component.literal(Utils.titleCase(professionName) + " Villager");
    }

    public static void notifyPickup(Player player, Entity entity) {
        MessageType.PICKUP_MESSAGE.sendActionbarSilently(player, Component.literal("You picked up a villager"));
        ServerLevel world = (ServerLevel) VersionHelper.getWorld(entity);
        double x = entity.getX();
        double y = entity.getY() + .25f;
        double z = entity.getZ();
        world.sendParticles(ParticleTypes.SWEEP_ATTACK, x, y, z, 1, 0, 0, 0, 1);
        VersionHelper.playSound(player, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.NEUTRAL, 1, .5f);
    }
}
