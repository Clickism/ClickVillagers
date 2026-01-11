/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.callback;

import de.clickism.clickvillagers.anchor.AnchorHandler;
import de.clickism.clickvillagers.gui.VillagerClaimGui;
import de.clickism.clickvillagers.gui.VillagerEditGui;
import de.clickism.clickvillagers.util.MessageType;
import de.clickism.clickvillagers.util.Utils;
import de.clickism.clickvillagers.util.VersionHelper;
import de.clickism.clickvillagers.villager.PartnerState;
import de.clickism.clickvillagers.villager.PickupHandler;
import de.clickism.clickvillagers.villager.VillagerHandler;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.ItemTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.npc.villager.VillagerDataHolder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import static de.clickism.clickvillagers.ClickVillagersConfig.*;

public class UseVillagerEntityCallback implements UseEntityCallback {
    private final CooldownManager cooldownManager;

    public UseVillagerEntityCallback(CooldownManager cooldownManager) {
        this.cooldownManager = cooldownManager;
    }

    @Override
    public InteractionResult interact(Player player, Level world, InteractionHand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (world.isClientSide()) return InteractionResult.PASS;
        if (!hand.equals(InteractionHand.MAIN_HAND)) return InteractionResult.PASS;
        if (player.isSpectator()) return InteractionResult.PASS;
        if (!(entity instanceof LivingEntity && entity instanceof VillagerDataHolder)) return InteractionResult.PASS;
        // Don't allow zombie villagers if setting is disabled
        if (!ALLOW_ZOMBIE_VILLAGERS.get() && entity instanceof ZombieVillager) return InteractionResult.PASS;
        var villager = (LivingEntity & VillagerDataHolder) entity;
        VillagerHandler<?> villagerHandler = new VillagerHandler<>(villager);
        if (hitResult != null) return InteractionResult.CONSUME;
        if (!player.isShiftKeyDown()) {
            return handleTrade(player, villagerHandler);
        }
        Inventory inventory = player.getInventory();
        ItemStack itemStack = VersionHelper.getSelectedStack(inventory);
        Item item = itemStack.getItem();
        if (item.equals(Items.SHEARS)) {
            handleAnchor(player, villagerHandler);
            return InteractionResult.CONSUME;
        }
        if (itemStack.is(ItemTags.SHOVELS)) {
            handleClaim(player, villagerHandler);
            return InteractionResult.CONSUME;
        }
        if (villagerHandler.isOwner(player.getUUID()) || (villagerHandler.hasOwner() && VersionHelper.isOp(player))) {
            handleEdit(player, villagerHandler);
            return InteractionResult.CONSUME;
        }
        if (!ENABLE_PICKUP.get()) {
            return InteractionResult.PASS; // Pickup is disabled
        }
        handlePickup(player, villagerHandler);
        return InteractionResult.CONSUME;
    }

    private void handleClaim(Player player, VillagerHandler<?> villagerHandler) {
        UUID owner = villagerHandler.getOwner();
        ServerPlayer serverPlayer = (ServerPlayer) player;
        if (owner == null) {
            // Allow claim
            if (!ENABLE_CLAIMS.get()) {
                MessageType.FAIL.send(player, Component.literal("Claiming villagers is disabled."));
                return;
            }
            if (cooldownManager.hasCooldown(player)) {
                long seconds = cooldownManager.getRemainingCooldownSeconds(player);
                MessageType.FAIL.send(player, Component.literal("Please wait §l" + seconds
                                                                + " seconds §cto claim this villager."));
                return;
            }
            new VillagerClaimGui(serverPlayer, villagerHandler, cooldownManager)
                    .open();
            playOpenSound(player);
            return;
        }
        if (villagerHandler.isOwner(player.getUUID())) {
            // Open Edit Menu
            handleEdit(player, villagerHandler);
            return;
        }
        MessageType.FAIL.send(player, Component.literal("This villager is already claimed."));
    }

    private InteractionResult handleTrade(Player player, VillagerHandler<?> villagerHandler) {
        if (villagerHandler.isTradingOpen()) return InteractionResult.PASS;
        if (!villagerHandler.hasOwner()) return InteractionResult.PASS;
        if (villagerHandler.isOwner(player.getUUID())) return InteractionResult.PASS;
        @SuppressWarnings("DataFlowIssue")
        PartnerState partnerState = PartnerState.getServerState(VersionHelper.getServer(player));
        if (partnerState.isPartner(villagerHandler.getOwner(), player.getName().getString())) {
            // Player is a partner
            return InteractionResult.PASS;
        }
        MessageType.FAIL.send(player, Component.literal("This villager is closed for trading."));
        return InteractionResult.CONSUME;
    }

    private void handleEdit(Player player, VillagerHandler<?> villagerHandler) {
        new VillagerEditGui((ServerPlayer) player, villagerHandler).open();
        playOpenSound(player);
    }

    private void handlePickup(Player player, VillagerHandler<?> villagerHandler) {
        if (villagerHandler.hasOwner() && !villagerHandler.isOwner(player.getUUID())) {
            MessageType.FAIL.send(player, Component.literal("You can't pick up this villager."));
            return;
        }
        if (cooldownManager.hasCooldown(player)) {
            long seconds = cooldownManager.getRemainingCooldownSeconds(player);
            MessageType.FAIL.send(player, Component.literal("Please wait §l" + seconds
                                                            + " seconds §cto pick up this villager."));
            return;
        }
        PickupHandler.notifyPickup(player, villagerHandler.getEntity());
        ItemStack itemStack = PickupHandler.toItemStack(villagerHandler.getEntity());
        Utils.offerToHand(player, itemStack);
        cooldownManager.giveCooldown(player);
    }

    private void handleAnchor(Player player, VillagerHandler<?> villagerHandler) {
        if (villagerHandler.hasOwner() && !villagerHandler.isOwner(player.getUUID())) {
            MessageType.FAIL.send(player, Component.literal("You can't anchor this villager."));
            return;
        }
        LivingEntity entity = villagerHandler.getEntity();
        ServerLevel world = (ServerLevel) VersionHelper.getWorld(entity);
        if (AnchorHandler.isAnchored(entity)) {
            AnchorHandler.removeAnchorEffect(entity);
            MessageType.ANCHOR_REMOVE.sendActionbarSilently(player, Component.literal("You removed this villager's anchor."));
            VersionHelper.playSound(player, SoundEvents.CHAIN_PLACE, SoundSource.MASTER, 1, 1);
            world.sendParticles(
                    ParticleTypes.WAX_OFF,
                    entity.getX(), entity.getY(), entity.getZ(),
                    10, .2, 0, .2, 2
            );
        } else {
            if (!ENABLE_ANCHORS.get()) {
                MessageType.FAIL.send(player, Component.literal("Anchoring villagers is disabled."));
                return;
            }
            AnchorHandler.addAnchorEffect(entity);
            MessageType.ANCHOR_ADD.sendActionbarSilently(player, Component.literal("You anchored this villager."));
            VersionHelper.playSound(player, SoundEvents.CHAIN_PLACE, SoundSource.NEUTRAL, 1, .5f);
            BlockPos posBelow = entity.blockPosition().below();
            world.sendParticles(
                    ParticleTypes.WAX_ON,
                    entity.getX(), entity.getY(), entity.getZ(),
                    10, .2, 0, .2, 2
            );
            world.sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, world.getBlockState(posBelow)),
                    entity.getX(), entity.getY(), entity.getZ(),
                    30, 0, 0, 0, 1
            );
        }
    }

    private void playOpenSound(Player player) {
        VersionHelper.playSound(player, SoundEvents.CHEST_OPEN, SoundSource.NEUTRAL, 1, .8f);
    }
}
