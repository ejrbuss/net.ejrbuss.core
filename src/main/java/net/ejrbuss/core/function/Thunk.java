package net.ejrbuss.core.function;

@FunctionalInterface
public interface Thunk<V> {

    V apply();

    static <V> Thunk<V> of(V thing) {
        return () -> thing;
    }

    default <V2> Thunk<V2> then(Fn<V, V2> fn) {
        return () -> fn.apply(apply());
    }

    default CachedThunk<V> cached() {
        return CachedThunk.of(this);
    }

}

