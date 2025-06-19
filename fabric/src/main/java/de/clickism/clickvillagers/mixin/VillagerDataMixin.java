/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.clickism.clickvillagers.util.CodecUtils;
import de.clickism.clickvillagers.util.LazyCodec;
import de.clickism.clickvillagers.villager.ClaimedVillagerData;
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
    @Unique
    private boolean tradingOpen = true;

    @ModifyExpressionValue(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;",
                    remap = false
            )
    )
    private static Codec<VillagerData> modifyCodec(Codec<VillagerData> original) {
        return LazyCodec.of(() -> CodecUtils.withAlternative(
                RecordCodecBuilder.create(
                        instance -> instance.group(
                                CodecUtils.assumeMapUnsafe(original).forGetter(Function.identity()),
                                Uuids.CODEC.optionalFieldOf("owner").orElse(null).forGetter(villagerData ->
                                        Optional.ofNullable((ClaimedVillagerData.of(villagerData)).clickVillagers_Fabric$getOwner())),
                                Codec.BOOL.fieldOf("tradingOpen").orElse(true).forGetter(villagerData ->
                                        (ClaimedVillagerData.of(villagerData)).clickVillagers_Fabric$isTradingOpen())
                        ).apply(instance, (data, owner, tradingOpen) -> {
                            ClaimedVillagerData claimedData = ClaimedVillagerData.of(data);
                            claimedData.clickVillagers_Fabric$setOwner(owner.orElse(null));
                            claimedData.clickVillagers_Fabric$setTradingOpen(tradingOpen);
                            return data;
                        })
                ),
                original
        ));
    }

    @ModifyReturnValue(method = "withType", at = @At("RETURN"))
    private VillagerData modifyWithType(VillagerData data) {
        return modifyData(data);
    }

    @ModifyReturnValue(method = "withProfession", at = @At("RETURN"))
    private VillagerData modifyWithProfession(VillagerData data) {
        return modifyData(data);
    }

    @ModifyReturnValue(method = "withLevel", at = @At("RETURN"))
    private VillagerData modifyWithLevel(VillagerData data) {
        return modifyData(data);
    }

    @Unique
    private VillagerData modifyData(VillagerData data) {
        ClaimedVillagerData claimedData = ClaimedVillagerData.of(data);
        claimedData.clickVillagers_Fabric$setOwner(owner);
        claimedData.clickVillagers_Fabric$setTradingOpen(tradingOpen);
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

    @Override
    public boolean clickVillagers_Fabric$isTradingOpen() {
        return tradingOpen;
    }

    @Override
    public void clickVillagers_Fabric$setTradingOpen(boolean open) {
        tradingOpen = open;
    }

}
