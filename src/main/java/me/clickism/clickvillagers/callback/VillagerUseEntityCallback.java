package me.clickism.clickvillagers.callback;

import me.clickism.clickvillagers.AnchorHandler;
import me.clickism.clickvillagers.PickupHandler;
import me.clickism.clickvillagers.util.MessageType;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

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
        if (!(entity instanceof VillagerEntity) && !(entity instanceof ZombieVillagerEntity)) return ActionResult.PASS;
        if (hitResult == null) return ActionResult.CONSUME;
        PlayerInventory inventory = player.getInventory();
        if (inventory.getMainHandStack().getItem().equals(Items.SHEARS)) {
            handleAnchor((LivingEntity) entity, player);
            return ActionResult.CONSUME;
        }
        handlePickup(player, entity);
        return ActionResult.PASS;
    }
    
    private void handlePickup(PlayerEntity player, Entity entity) {
        PlayerInventory inventory = player.getInventory();
        PICKUP_MESSAGE.sendActionbar(player, Text.literal("You picked a villager up."));
        ItemStack itemStack = PickupHandler.toItemStack(entity);
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
