package net.ejrbuss.core.data;

import net.ejrbuss.core.function.Thunk;

@FunctionalInterface
public interface Box<V> extends Thunk<V> {

    static <V, B extends Box<V>> V unbox(B box) {
        return box.get();
    }

    static <V, B1 extends Box<V>, B2 extends Box<B1>> V unbox2(B2 box) {
        return box.get().get();
    }

    static <V, B1 extends Box<V>, B2 extends Box<B1>, B3 extends Box<B2>> V unbox3(B3 box) {
        return box.get().get().get();
    }

    V get();

    default V apply() {
        return get();
    }

}

