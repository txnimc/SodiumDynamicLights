package toni.lib.networking.codecs;

import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function5;
import com.mojang.datafixers.util.Function6;
import io.netty.buffer.ByteBuf;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface StreamCodec<B, V> extends StreamDecoder<B, V>, StreamEncoder<B, V> {
    static <B, V> StreamCodec<B, V> of(final StreamEncoder<B, V> streamEncoder, final StreamDecoder<B, V> streamDecoder) {
        return new StreamCodec<B, V>() {
            public V decode(B object) {
                return streamDecoder.decode(object);
            }

            public void encode(B object, V object2) {
                streamEncoder.encode(object, object2);
            }
        };
    }

    static <B, V> StreamCodec<B, V> ofMember(final StreamMemberEncoder<B, V> streamMemberEncoder, final StreamDecoder<B, V> streamDecoder) {
        return new StreamCodec<B, V>() {
            public V decode(B object) {
                return streamDecoder.decode(object);
            }

            public void encode(B object, V object2) {
                streamMemberEncoder.encode(object2, object);
            }
        };
    }

    static <B, V> StreamCodec<B, V> unit(final V object) {
        return new StreamCodec<B, V>() {
            public V decode(B objectx) {
                return object;
            }

            public void encode(B objectx, V object2) {
                if (!object2.equals(object)) {
                    String var10002 = String.valueOf(object2);
                    throw new IllegalStateException("Can't encode '" + var10002 + "', expected '" + String.valueOf(object) + "'");
                }
            }
        };
    }

//    default <O> StreamCodec<B, O> apply(CodecOperation<B, V, O> codecOperation) {
//        return codecOperation.apply(this);
//    }

    default <O> StreamCodec<B, O> map(final Function<? super V, ? extends O> function, final Function<? super O, ? extends V> function2) {
        return new StreamCodec<B, O>() {
            public O decode(B object) {
                return function.apply(StreamCodec.this.decode(object));
            }

            public void encode(B object, O object2) {
                StreamCodec.this.encode(object, function2.apply(object2));
            }
        };
    }

    default <O extends ByteBuf> StreamCodec<O, V> mapStream(final Function<O, ? extends B> function) {
        return new StreamCodec<O, V>() {
            public V decode(O byteBuf) {
                B object = function.apply(byteBuf);
                return StreamCodec.this.decode(object);
            }

            public void encode(O byteBuf, V object) {
                B object2 = function.apply(byteBuf);
                StreamCodec.this.encode(object2, object);
            }
        };
    }

    default <U> StreamCodec<B, U> dispatch(final Function<? super U, ? extends V> function, final Function<? super V, ? extends StreamCodec<? super B, ? extends U>> function2) {
        return new StreamCodec<B, U>() {
            public U decode(B object) {
                V object2 = StreamCodec.this.decode(object);
                StreamCodec<? super B, ? extends U> streamCodec = (StreamCodec)function2.apply(object2);
                return streamCodec.decode(object);
            }

            public void encode(B object, U object2) {
                V object3 = function.apply(object2);
                StreamCodec<B, U> streamCodec = (StreamCodec)function2.apply(object3);
                StreamCodec.this.encode(object, object3);
                streamCodec.encode(object, object2);
            }
        };
    }

    static <B, C, T1> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> streamCodec, final Function<C, T1> function, final Function<T1, C> function2) {
        return new StreamCodec<B, C>() {
            public C decode(B object) {
                T1 object2 = streamCodec.decode(object);
                return function2.apply(object2);
            }

            public void encode(B object, C object2) {
                streamCodec.encode(object, function.apply(object2));
            }
        };
    }

    static <B, C, T1, T2> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> streamCodec, final Function<C, T1> function, final StreamCodec<? super B, T2> streamCodec2, final Function<C, T2> function2, final BiFunction<T1, T2, C> biFunction) {
        return new StreamCodec<B, C>() {
            public C decode(B object) {
                T1 object2 = streamCodec.decode(object);
                T2 object3 = streamCodec2.decode(object);
                return biFunction.apply(object2, object3);
            }

            public void encode(B object, C object2) {
                streamCodec.encode(object, function.apply(object2));
                streamCodec2.encode(object, function2.apply(object2));
            }
        };
    }

    static <B, C, T1, T2, T3> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> streamCodec, final Function<C, T1> function, final StreamCodec<? super B, T2> streamCodec2, final Function<C, T2> function2, final StreamCodec<? super B, T3> streamCodec3, final Function<C, T3> function3, final Function3<T1, T2, T3, C> function32) {
        return new StreamCodec<B, C>() {
            public C decode(B object) {
                T1 object2 = streamCodec.decode(object);
                T2 object3 = streamCodec2.decode(object);
                T3 object4 = streamCodec3.decode(object);
                return function32.apply(object2, object3, object4);
            }

            public void encode(B object, C object2) {
                streamCodec.encode(object, function.apply(object2));
                streamCodec2.encode(object, function2.apply(object2));
                streamCodec3.encode(object, function3.apply(object2));
            }
        };
    }

    static <B, C, T1, T2, T3, T4> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> streamCodec, final Function<C, T1> function, final StreamCodec<? super B, T2> streamCodec2, final Function<C, T2> function2, final StreamCodec<? super B, T3> streamCodec3, final Function<C, T3> function3, final StreamCodec<? super B, T4> streamCodec4, final Function<C, T4> function4, final Function4<T1, T2, T3, T4, C> function42) {
        return new StreamCodec<B, C>() {
            public C decode(B object) {
                T1 object2 = streamCodec.decode(object);
                T2 object3 = streamCodec2.decode(object);
                T3 object4 = streamCodec3.decode(object);
                T4 object5 = streamCodec4.decode(object);
                return function42.apply(object2, object3, object4, object5);
            }

            public void encode(B object, C object2) {
                streamCodec.encode(object, function.apply(object2));
                streamCodec2.encode(object, function2.apply(object2));
                streamCodec3.encode(object, function3.apply(object2));
                streamCodec4.encode(object, function4.apply(object2));
            }
        };
    }

    static <B, C, T1, T2, T3, T4, T5> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> streamCodec, final Function<C, T1> function, final StreamCodec<? super B, T2> streamCodec2, final Function<C, T2> function2, final StreamCodec<? super B, T3> streamCodec3, final Function<C, T3> function3, final StreamCodec<? super B, T4> streamCodec4, final Function<C, T4> function4, final StreamCodec<? super B, T5> streamCodec5, final Function<C, T5> function5, final Function5<T1, T2, T3, T4, T5, C> function52) {
        return new StreamCodec<B, C>() {
            public C decode(B object) {
                T1 object2 = streamCodec.decode(object);
                T2 object3 = streamCodec2.decode(object);
                T3 object4 = streamCodec3.decode(object);
                T4 object5 = streamCodec4.decode(object);
                T5 object6 = streamCodec5.decode(object);
                return function52.apply(object2, object3, object4, object5, object6);
            }

            public void encode(B object, C object2) {
                streamCodec.encode(object, function.apply(object2));
                streamCodec2.encode(object, function2.apply(object2));
                streamCodec3.encode(object, function3.apply(object2));
                streamCodec4.encode(object, function4.apply(object2));
                streamCodec5.encode(object, function5.apply(object2));
            }
        };
    }

    static <B, C, T1, T2, T3, T4, T5, T6> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> streamCodec, final Function<C, T1> function, final StreamCodec<? super B, T2> streamCodec2, final Function<C, T2> function2, final StreamCodec<? super B, T3> streamCodec3, final Function<C, T3> function3, final StreamCodec<? super B, T4> streamCodec4, final Function<C, T4> function4, final StreamCodec<? super B, T5> streamCodec5, final Function<C, T5> function5, final StreamCodec<? super B, T6> streamCodec6, final Function<C, T6> function6, final Function6<T1, T2, T3, T4, T5, T6, C> function62) {
        return new StreamCodec<B, C>() {
            public C decode(B object) {
                T1 object2 = streamCodec.decode(object);
                T2 object3 = streamCodec2.decode(object);
                T3 object4 = streamCodec3.decode(object);
                T4 object5 = streamCodec4.decode(object);
                T5 object6 = streamCodec5.decode(object);
                T6 object7 = streamCodec6.decode(object);
                return function62.apply(object2, object3, object4, object5, object6, object7);
            }

            public void encode(B object, C object2) {
                streamCodec.encode(object, function.apply(object2));
                streamCodec2.encode(object, function2.apply(object2));
                streamCodec3.encode(object, function3.apply(object2));
                streamCodec4.encode(object, function4.apply(object2));
                streamCodec5.encode(object, function5.apply(object2));
                streamCodec6.encode(object, function6.apply(object2));
            }
        };
    }

//    static <B, T> StreamCodec<B, T> recursive(final UnaryOperator<StreamCodec<B, T>> unaryOperator) {
//        return new StreamCodec<B, T>() {
//            private final Supplier<StreamCodec<B, T>> inner = Suppliers.memoize(() -> {
//                return (StreamCodec)unaryOperator.apply(this);
//            });
//
//            public T decode(B object) {
//                return ((StreamCodec)this.inner.get()).decode(object);
//            }
//
//            public void encode(B object, T object2) {
//                ((StreamCodec)this.inner.get()).encode(object, object2);
//            }
//        };
//    }

//    default <S extends B> StreamCodec<S, V> cast() {
//        return this;
//    }
//
//    @FunctionalInterface
//    public interface CodecOperation<B, S, T> {
//        StreamCodec<B, T> apply(StreamCodec<B, S> streamCodec);
//    }
}