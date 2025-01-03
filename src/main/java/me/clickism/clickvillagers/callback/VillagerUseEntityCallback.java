package me.clickism.clickvillagers.callback;

import me.clickism.clickvillagers.anchor.AnchorHandler;
import me.clickism.clickvillagers.gui.VillagerClaimGui;
import me.clickism.clickvillagers.gui.VillagerEditGui;
import me.clickism.clickvillagers.util.MessageType;
import me.clickism.clickvillagers.util.Utils;
import me.clickism.clickvillagers.util.VersionHelper;
import me.clickism.clickvillagers.villager.PartnerState;
import me.clickism.clickvillagers.villager.PickupHandler;
import me.clickism.clickvillagers.villager.VillagerHandler;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class VillagerUseEntityCallback implements UseEntityCallback {
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (world.isClient()) return ActionResult.PASS;
        if (!hand.equals(Hand.MAIN_HAND)) return ActionResult.PASS;
        if (player.isSpectator()) return ActionResult.PASS;
        if (!(entity instanceof LivingEntity && entity instanceof VillagerDataContainer)) return ActionResult.PASS;
        var villager = (LivingEntity & VillagerDataContainer) entity;
        VillagerHandler<?> villagerHandler = new VillagerHandler<>(villager);
        if (!player.isSneaking()) {
            return handleTrade(player, villagerHandler, hitResult);
        }
        if (hitResult == null) return ActionResult.CONSUME;
        PlayerInventory inventory = player.getInventory();
        ItemStack itemStack = inventory.getMainHandStack();
        Item item = itemStack.getItem();
        if (item.equals(Items.SHEARS)) {
            handleAnchor(player, villagerHandler);
            return ActionResult.CONSUME;
        }
        if (itemStack.isIn(ItemTags.SHOVELS)) {
            handleClaim(player, villagerHandler);
            return ActionResult.CONSUME;
        }
        if (villagerHandler.isOwner(player.getUuid())) {
            handleEdit(player, villagerHandler);
            return ActionResult.CONSUME;
        }
        handlePickup(player, villagerHandler);
        return ActionResult.CONSUME;
    }

    private void handleClaim(PlayerEntity player, VillagerHandler<?> villagerHandler) {
        UUID owner = villagerHandler.getOwner();
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        if (owner == null) {
            // Allow claim
            new VillagerClaimGui(serverPlayer, villagerHandler).open();
            playOpenSound(player);
            return;
        }
        if (villagerHandler.isOwner(player.getUuid())) {
            // Open Edit Menu
            handleEdit(player, villagerHandler);
            return;
        }
        MessageType.FAIL.send(player, Text.literal("This villager is already claimed."));
    }

    private ActionResult handleTrade(PlayerEntity player, VillagerHandler<?> villagerHandler, HitResult hitResult) {
        if (villagerHandler.isTradingOpen()) return ActionResult.PASS;
        if (!villagerHandler.hasOwner()) return ActionResult.PASS;
        if (villagerHandler.isOwner(player.getUuid())) return ActionResult.PASS;
        @SuppressWarnings("DataFlowIssue")
        PartnerState partnerState = PartnerState.getServerState(player.getServer());
        if (partnerState.isPartner(villagerHandler.getOwner(), player.getName().getString())) {
            // Player is a partner
            return ActionResult.PASS;
        }
        if (hitResult == null) return ActionResult.PASS;
        MessageType.FAIL.send(player, Text.literal("This villager is closed for trading."));
        return ActionResult.CONSUME;
    }

    private void handleEdit(PlayerEntity player, VillagerHandler<?> villagerHandler) {
        new VillagerEditGui((ServerPlayerEntity) player, villagerHandler).open();
        playOpenSound(player);
    }

    private void handlePickup(PlayerEntity player, VillagerHandler<?> villagerHandler) {
        if (villagerHandler.hasOwner() && !villagerHandler.isOwner(player.getUuid())) {
            MessageType.FAIL.send(player, Text.literal("You can't pick up this villager."));
            return;
        }
        PickupHandler.notifyPickup(player, villagerHandler.getEntity());
        ItemStack itemStack = PickupHandler.toItemStack(villagerHandler.getEntity());
        Utils.offerToHand(player, itemStack);
    }

    private void handleAnchor(PlayerEntity player, VillagerHandler<?> villagerHandler) {
        if (villagerHandler.hasOwner() && !villagerHandler.isOwner(player.getUuid())) {
            MessageType.FAIL.send(player, Text.literal("You can't anchor this villager."));
            return;
        }
        LivingEntity entity = villagerHandler.getEntity();
        ServerWorld world = (ServerWorld) player.getWorld();
        if (AnchorHandler.isAnchored(entity)) {
            AnchorHandler.removeAnchorEffect(entity);
            MessageType.ANCHOR_REMOVE.sendActionbarSilently(player, Text.literal("You removed this villager's anchor."));
            VersionHelper.playSound(player, SoundEvents.ENTITY_LEASH_KNOT_PLACE, SoundCategory.MASTER, 1, 1);
            world.spawnParticles(
                    ParticleTypes.WAX_OFF,
                    entity.getX(), entity.getY(), entity.getZ(),
                    10, .2, 0, .2, 2
            );
        } else {
            AnchorHandler.addAnchorEffect(entity);
            MessageType.ANCHOR_ADD.sendActionbarSilently(player, Text.literal("You anchored this villager."));
            VersionHelper.playSound(player, SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.NEUTRAL, 1, 1);
            BlockPos posBelow = entity.getBlockPos().down();
            world.spawnParticles(
                    ParticleTypes.WAX_ON,
                    entity.getX(), entity.getY(), entity.getZ(),
                    10, .2, 0, .2, 2
            );
            world.spawnParticles(
                    new BlockStateParticleEffect(ParticleTypes.BLOCK, world.getBlockState(posBelow)),
                    entity.getX(), entity.getY(), entity.getZ(),
                    30, 0, 0, 0, 1
            );
        }
    }

    private void playOpenSound(PlayerEntity player) {
        VersionHelper.playSound(player, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.NEUTRAL, 1, .8f);
    }
}
