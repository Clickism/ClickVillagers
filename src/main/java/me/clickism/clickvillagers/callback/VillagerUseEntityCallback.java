package me.clickism.clickvillagers.callback;

import me.clickism.clickvillagers.AnchorHandler;
import me.clickism.clickvillagers.PartnerState;
import me.clickism.clickvillagers.PickupHandler;
import me.clickism.clickvillagers.VillagerHandler;
import me.clickism.clickvillagers.gui.VillagerClaimGui;
import me.clickism.clickvillagers.gui.VillagerEditGui;
import me.clickism.clickvillagers.util.MessageType;
import me.clickism.clickvillagers.util.Utils;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
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
            return handleTrade(player, villagerHandler);
        }
        if (hitResult == null) return ActionResult.CONSUME;
        PlayerInventory inventory = player.getInventory();
        ItemStack itemStack = inventory.getMainHandStack();
        Item item = itemStack.getItem();
        if (item.equals(Items.SHEARS)) {
            handleAnchor((LivingEntity) entity, player);
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
            return;
        }
        if (villagerHandler.isOwner(player.getUuid())) {
            // Open Edit Menu
            handleEdit(player, villagerHandler);
            return;
        }
        MessageType.WARN.send(player, Text.literal("This villager is already claimed."));
    }
    
    private ActionResult handleTrade(PlayerEntity player, VillagerHandler<?> villagerHandler) {
        if (villagerHandler.isTradingOpen()) return ActionResult.PASS;
        if (!villagerHandler.hasOwner()) return ActionResult.PASS;
        if (villagerHandler.isOwner(player.getUuid())) return ActionResult.PASS;
        @SuppressWarnings("DataFlowIssue")
        PartnerState partnerState = PartnerState.getServerState(player.getServer());
        if (partnerState.isPartner(villagerHandler.getOwner(), player.getName().getString())) {
            // Player is a partner
            return ActionResult.PASS;
        }
        MessageType.FAIL.send(player, Text.literal("This villager is closed for trading."));
        return ActionResult.CONSUME;
    }
    
    private void handleEdit(PlayerEntity player, VillagerHandler<?> villagerHandler) {
        new VillagerEditGui((ServerPlayerEntity) player, villagerHandler).open();
    }
    
    private void handlePickup(PlayerEntity player, VillagerHandler<?> villagerHandler) {
        PickupHandler.notifyPickup(player);
        ItemStack itemStack = PickupHandler.toItemStack(villagerHandler.getEntity());
        Utils.offerToHand(player, itemStack);
    }

    private void handleAnchor(LivingEntity entity, PlayerEntity player) {
        if (AnchorHandler.isAnchored(entity)) {
            AnchorHandler.removeAnchorEffect(entity);
            MessageType.WARN.send(player, Text.literal("Villager anchor removed."));
        } else {
            AnchorHandler.addAnchorEffect(entity);
            MessageType.CONFIRM.sendSilently(player, Text.literal("Villager anchored."));
            player.playSoundToPlayer(SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.MASTER, 1, 1);
        }
    }
}
