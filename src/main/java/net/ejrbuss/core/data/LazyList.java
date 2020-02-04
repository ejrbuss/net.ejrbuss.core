package net.ejrbuss.core.data;

import net.ejrbuss.core.function.Thunk;

import java.util.Iterator;

public class LazyList<V> extends List<V> {

    private final Thunk<Pair<Maybe<V>, List<V>>> next;

    public LazyList(Thunk<Pair<Maybe<V>, List<V>>> next) {
        this.next = next;
    }

    @Override
    public Iterator<V> iterator() {
        return new Iterator<V>() {

            private Maybe<V> first = first();
            private Seq<V> rest = rest();

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

    @Override
    public Maybe<V> first() {
        return next.get().left();
    }

    @Override
    public Maybe<V> last() {
        V last = null;
        for (V v : this) {
            last = v;
        }
        return last == null ? Maybe.none() : Maybe.some(last);
    }

    @Override
    public List<V> rest() {
        return next.get().right();
    }

    @Override
    public boolean isEmpty() {
        return next.get().left().isNone();
    }

    @Override
    public Maybe<V> nth(int n) {
        n = normalizeIndex(n);
        int i = 0;
        for (V v : this) {
            if (i++ == n) {
                return Maybe.some(v);
            }
        }
        return Maybe.none();
    }

    @Override
    public int count() {
        int i = 0;
        for (V v : this) {
            i++;
        }
        return i;
    }

    @Override
    public List<V> take(int n) {
        if (isEmpty() || n <= 0) {
            return empty();
        }
        return new LazyList<>(() -> Pair.of(first(), rest().take(n - 1)));
    }

    @Override
    public List<V> drop(int n) {
        List<V> list = this;
        while (!list.isEmpty() && n-- > 0) {
            list = list.rest();
        }
        return list;
    }

    @Override
    public List<V> reverse() {
        return strict().reverse();
    }

    @Override
    public List<V> strict() {
        return new StrictList<>(this);
    }

}
