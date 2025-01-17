/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.*;

import java.util.function.Function;
import java.util.stream.Stream;

public class CodecUtils {

    public static <A> MapCodec<A> assumeMapUnsafe(final Codec<A> codec) {
        return new MapCodec<>() {
            private static final String COMPRESSED_VALUE_KEY = "value";

            @Override
            public <T> Stream<T> keys(final DynamicOps<T> ops) {
                return Stream.of(ops.createString(COMPRESSED_VALUE_KEY));
            }

            @Override
            public <T> DataResult<A> decode(final DynamicOps<T> ops, final MapLike<T> input) {
                if (ops.compressMaps()) {
                    final T value = input.get(COMPRESSED_VALUE_KEY);
                    if (value == null) {
                        return DataResult.error(() -> "Missing value");
                    }
                    return codec.parse(ops, value);
                }
                return codec.parse(ops, ops.createMap(input.entries()));
            }

            @Override
            public <T> RecordBuilder<T> encode(final A input, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
                final DataResult<T> encoded = codec.encodeStart(ops, input);
                if (ops.compressMaps()) {
                    return prefix.add(COMPRESSED_VALUE_KEY, encoded);
                }
                final DataResult<MapLike<T>> encodedMapResult = encoded.flatMap(ops::getMap);
                return encodedMapResult.map(encodedMap -> {
                    encodedMap.entries().forEach(pair -> prefix.add(pair.getFirst(), pair.getSecond()));
                    return prefix;
                }).result().orElseGet(() -> prefix.withErrorsFrom(encodedMapResult));
            }
        };
    }

    public static <T> Codec<T> withAlternative(final Codec<T> primary, final Codec<? extends T> alternative) {
        return Codec.either(
                primary,
                alternative
        ).xmap(
                either -> either.map(Function.identity(), Function.identity()),
                Either::left
        );
    }
}
