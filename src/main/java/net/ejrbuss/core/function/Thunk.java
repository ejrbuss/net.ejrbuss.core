package net.ejrbuss.core.function;

@FunctionalInterface
public interface Thunk<V> {

    V get();

    static <V> Thunk<V> of(V thing) {
        return () -> thing;
    }

    default <V2> Thunk<V2> then(Fn<V, V2> fn) {
        return () -> fn.apply(get());
    }

    default CachedThunk<V> cached() {
        return CachedThunk.of(this);
    }

}

