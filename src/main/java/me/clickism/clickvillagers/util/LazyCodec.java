package me.clickism.clickvillagers.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.function.Supplier;

public class LazyCodec<T> implements Codec<T> {
    private final Supplier<Codec<T>> codecSupplier;
    
    private LazyCodec(Supplier<Codec<T>> codecSupplier) {
        this.codecSupplier = codecSupplier;
    }
    
    @Override
    public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
        return codecSupplier.get().decode(ops, input);
    }

    @Override
    public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
        return codecSupplier.get().encode(input, ops, prefix);
    }

    @Override
    public String toString() {
        return "LazyCodec[" + codecSupplier.get().toString() + "]";
    }
    
    public static <T> LazyCodec<T> of(Supplier<Codec<T>> codecSupplier) {
        return new LazyCodec<>(codecSupplier);
    }
}
