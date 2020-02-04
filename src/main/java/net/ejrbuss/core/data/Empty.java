package net.ejrbuss.core.data;

import net.ejrbuss.core.function.Eff;
import net.ejrbuss.core.function.Fn;
import net.ejrbuss.core.function.Fn2;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Empty<V> extends List<V> {

    @Override
    public Iterator<V> iterator() {
        return new Iterator<V>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public V next() {
                throw new NoSuchElementException("next() on empty!");
            }
        };
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof List) {
            List<?> otherList = (List<?>) other;
            return otherList.isEmpty();
        }
        return false;
    }

    @Override
    public boolean contains(V value) {
        return false;
    }

    @Override
    public void each(Eff<? super V> eff) {}

    @Override
    public Maybe<Integer> index(V value) {
        return Maybe.none();
    }

    @Override
    public Maybe<V> find(Fn<? super V, Boolean> pred) {
        return Maybe.none();
    }

    @Override
    public List<V> takeWhile(Fn<? super V, Boolean> pred) {
        return this;
    }

    @Override
    public List<V> dropWhile(Fn<? super V, Boolean> pred) {
        return this;
    }

    @Override
    public <V2> List<V2> map(Fn<? super V, V2> transform) {
        return empty();
    }

    @Override
    public <V2> List<V2> flatMap(Fn<? super V, Iterable<V2>> transform) {
        return empty();
    }

    @Override
    public List<V> filter(Fn<V, Boolean> pred) {
        return this;
    }

    @Override
    public <R> R reduce(Fn2<R, ? super V, R> reducer, R init) {
        return init;
    }

    @Override
    public List<V> sort(Comparator<V> comparator) {
        return this;
    }

    @Override
    public List<Pair<Integer, V>> enumerate() {
        return empty();
    }

    @Override
    public List<List<V>> chunk(int n) {
        return empty();
    }

    @Override
    public Maybe<V> first() {
        return Maybe.none();
    }

    @Override
    public Maybe<V> last() {
        return Maybe.none();
    }

    @Override
    public List<V> rest() {
        return this;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public Maybe<V> nth(int n) {
        return Maybe.none();
    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public List<V> take(int n) {
        return this;
    }

    @Override
    public List<V> drop(int n) {
        return this;
    }

    @Override
    public List<V> reverse() {
        return this;
    }

    @Override
    public List<V> strict() {
        return this;
    }

}
