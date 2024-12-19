package me.clickism.clickvillagers.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.clickism.clickvillagers.ClaimedVillagerData;
import net.minecraft.util.Uuids;
import net.minecraft.village.VillagerData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Mixin(VillagerData.class)
public abstract class VillagerDataMixin implements ClaimedVillagerData {
    @Unique
    private UUID owner;

    @ModifyExpressionValue(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;",
                    remap = false
            )
    )
    private static Codec<VillagerData> modifyCodec(Codec<VillagerData> original) {
        return Codec.lazyInitialized(() -> Codec.withAlternative(
                RecordCodecBuilder.create(
                        instance -> instance.group(
                                MapCodec.assumeMapUnsafe(original).forGetter(Function.identity()),
                                Uuids.CODEC.optionalFieldOf("owner").forGetter(villagerData ->
                                        Optional.ofNullable(((ClaimedVillagerData) villagerData).clickVillagers_Fabric$getOwner()))
                        ).apply(instance, (data, owner) -> {
                            ((ClaimedVillagerData) data).clickVillagers_Fabric$setOwner(owner.orElse(null));
                            return data;
                        })
                ), original));
    }

    @ModifyReturnValue(method = "withType", at = @At("RETURN"))
    private VillagerData modifyWithType(VillagerData data) {
        ((ClaimedVillagerData) data).clickVillagers_Fabric$setOwner(owner);
        return data;
    }
    
    @ModifyReturnValue(method = "withProfession", at = @At("RETURN"))
    private VillagerData modifyWithProfession(VillagerData data) {
        ((ClaimedVillagerData) data).clickVillagers_Fabric$setOwner(owner);
        return data;
    }
    
    @ModifyReturnValue(method = "withLevel", at = @At("RETURN"))
    private VillagerData modifyWithLevel(VillagerData data) {
        ((ClaimedVillagerData) data).clickVillagers_Fabric$setOwner(owner);
        return data;
    }

    @Override
    @Nullable
    public UUID clickVillagers_Fabric$getOwner() {
        return owner;
    }

    @Override
    public void clickVillagers_Fabric$setOwner(@Nullable UUID owner) {
        this.owner = owner;
    }
}
