package me.clickism.clickvillagers.callback;

import me.clickism.clickvillagers.AnchorHandler;
import me.clickism.clickvillagers.VillagerHandler;
import me.clickism.clickvillagers.PickupHandler;
import me.clickism.clickvillagers.gui.VillagerClaimGui;
import me.clickism.clickvillagers.gui.VillagerEditMenu;
import me.clickism.clickvillagers.util.MessageType;
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
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import java.util.UUID;

public class VillagerUseEntityCallback implements UseEntityCallback {
    private static final MessageType PICKUP_MESSAGE = new MessageType(
            Text.literal("[↑] ").formatted(Formatting.GREEN),
            Text.literal("< ").formatted(Formatting.DARK_GRAY)
                    .append(Text.literal("↑ ").formatted(Formatting.DARK_GREEN)),
            Text.literal(" >").formatted(Formatting.DARK_GRAY),
            Style.EMPTY.withColor(Formatting.GREEN)
    ) {
        @Override
        public void playSound(PlayerEntity player) {
            MessageType.CONFIRM.playSound(player);
        }
    };
    
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (!hand.equals(Hand.MAIN_HAND)) return ActionResult.PASS;
        if (!player.isSneaking()) return ActionResult.PASS;
        if (player.isSpectator()) return ActionResult.PASS;
        if (!(entity instanceof LivingEntity && entity instanceof VillagerDataContainer)) return ActionResult.PASS;
        if (hitResult == null) return ActionResult.CONSUME;
        PlayerInventory inventory = player.getInventory();
        ItemStack itemStack = inventory.getMainHandStack();
        Item item = itemStack.getItem();
        if (item.equals(Items.SHEARS)) {
            handleAnchor((LivingEntity) entity, player);
            return ActionResult.CONSUME;
        }
        var villager = (LivingEntity & VillagerDataContainer) entity;
        VillagerHandler<?> villagerHandler = new VillagerHandler<>(villager);
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
        var villager = villagerHandler.getEntity();
        if (owner == null) {
            // Allow claim
            new VillagerClaimGui<>(serverPlayer, villager).open();
            return;
        }
        if (villagerHandler.isOwner(player.getUuid())) {
            // Open Edit Menu
            handleEdit(player, villagerHandler);
            return;
        }
        MessageType.WARN.send(player, Text.literal("This villager is already claimed."));
    }
    
    private void handleEdit(PlayerEntity player, VillagerHandler<?> villagerHandler) {
        new VillagerEditMenu<>((ServerPlayerEntity) player, villagerHandler.getEntity()).open();
    }
    
    private void handlePickup(PlayerEntity player, VillagerHandler<?> villagerHandler) {
        PlayerInventory inventory = player.getInventory();
        PICKUP_MESSAGE.sendActionbar(player, Text.literal("You picked a villager up"));
        ItemStack itemStack = PickupHandler.toItemStack(villagerHandler.getEntity());
        int selectedSlot = inventory.selectedSlot;
        if (inventory.getStack(selectedSlot).isEmpty()) {
            inventory.insertStack(selectedSlot, itemStack);
            return;
        }
        inventory.offerOrDrop(itemStack);
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
