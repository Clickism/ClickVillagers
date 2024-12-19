package me.clickism.clickvillagers;

import me.clickism.clickvillagers.util.Utils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PickupHandler {
    
    private static final String TYPE_KEY = "PickupVillagerType";
    
    private enum PickupVillagerType {
        VILLAGER, ZOMBIE_VILLAGER
    }

    public static ItemStack toItemStack(Entity entity) {
        NbtCompound nbt = new NbtCompound();
        entity.writeNbt(nbt);
        PickupVillagerType type = entity instanceof VillagerEntity 
                ? PickupVillagerType.VILLAGER 
                : PickupVillagerType.ZOMBIE_VILLAGER;
        nbt.putString(TYPE_KEY, type.toString());
        ItemStack itemStack = getItemStack(getDisplayName(entity), NbtComponent.of(nbt));
        VillagerTextures.setEntityTexture(itemStack, entity);
        entity.remove(Entity.RemovalReason.DISCARDED);
        return itemStack;
    }
    
    private static ItemStack getItemStack(Text name, NbtComponent data) {
        ItemStack itemStack = Items.PLAYER_HEAD.getDefaultStack();
        itemStack.set(DataComponentTypes.CUSTOM_DATA, data);
        itemStack.set(DataComponentTypes.ITEM_NAME, name);
        itemStack.set(DataComponentTypes.LORE, new LoreComponent(List.of(
                Text.literal("Right click to place the villager back.")
                        .fillStyle(Style.EMPTY.withItalic(false).withColor(Formatting.DARK_GRAY)))));
        return itemStack;
    }

    @Nullable
    public static Entity readEntityFromItemStack(World world, ItemStack itemStack) {
        try {
            NbtComponent nbtComponent = itemStack.get(DataComponentTypes.CUSTOM_DATA);
            if (nbtComponent == null) return null;
            NbtCompound nbt = nbtComponent.copyNbt();
            PickupVillagerType type = PickupVillagerType.valueOf(nbt.getString(TYPE_KEY));
            Entity entity;
            if (type == PickupVillagerType.VILLAGER) {
                entity = new VillagerEntity(EntityType.VILLAGER, world);
            } else {
                entity = new ZombieVillagerEntity(EntityType.ZOMBIE_VILLAGER, world);
            }
            entity.readNbt(nbt);
            return entity;
        } catch (Exception e) {
            return null;
        }
    }

    private static MutableText getDisplayName(Entity entity) {
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
        return Text.literal(Utils.titleCase(profession.toString()));
    }
}
