package net.ejrbuss.core.data;

import java.util.Iterator;

public interface Traversable<V> extends Iterable<V> {

    Maybe<V> first();
    Traversable<V> rest();

    @Override
    default Iterator<V> iterator() {
        return new Iterator<V>() {

            private Maybe<V> first = first();
            private Traversable<V> rest = rest();

            @Override
            public boolean hasNext() {
                return first.isSome();
            }

            @Override
            public V next() {
                V value = first.force();
                first = rest.first();
                rest = rest.rest();
                return value;
            }
        };
    }

}
