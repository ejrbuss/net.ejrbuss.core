package net.ejrbuss.core.data;

import net.ejrbuss.core.function.Fn;
import net.ejrbuss.core.function.Fn2;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class SingleList<V> extends List<V> {

    private final V first;

    public SingleList(V first) {
        this.first = first;
    }

    @Override
    public Iterator<V> iterator() {
        return Collections.singletonList(first).iterator();
    }

    @Override
    public boolean contains(V value) {
        return first.equals(value);
    }

    @Override
    public Maybe<Integer> indexBy(Fn<? super V, Boolean> pred) {
        if (pred.apply(first)) {
            return Maybe.some(0);
        } else {
            return Maybe.none();
        }
    }

    @Override
    public List<V> takeWhile(Fn<? super V, Boolean> pred) {
        if (pred.apply(first)) {
            return this;
        } else {
            return empty();
        }
    }

    @Override
    public List<V> dropWhile(Fn<? super V, Boolean> pred) {
        if (pred.apply(first)) {
            return empty();
        } else {
            return this;
        }
    }

    @Override
    public Maybe<V> find(Fn<? super V, Boolean> pred) {
        if (pred.apply(first)) {
            return Maybe.some(first);
        } else {
            return Maybe.none();
        }
    }

    @Override
    public <V2> List<V2> map(Fn<? super V, V2> transform) {
        return new SingleList<>(transform.apply(first));
    }

    @Override
    public <V2> List<V2> flatMap(Fn<? super V, Iterable<V2>> transform) {
        return List.from(transform.apply(first));
    }

    @Override
    public List<V> filter(Fn<V, Boolean> pred) {
        if (pred.apply(first)) {
            return this;
        } else {
            return empty();
        }
    }

    @Override
    public <R> R reduce(Fn2<R, ? super V, R> reducer, R init) {
        return reducer.apply(init, first);
    }

    @Override
    public List<V> sort(Comparator<V> comparator) {
        return this;
    }

    @Override
    public Maybe<V> first() {
        return Maybe.some(first);
    }

    @Override
    public Maybe<V> last() {
        return Maybe.some(first);
    }

    @Override
    public List<V> rest() {
        return empty();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Maybe<V> nth(int n) {
        return Maybe.given(n == 0, first);
    }

    @Override
    public int count() {
        return 1;
    }

    @Override
    public List<V> take(int n) {
        if (n > 0) {
            return this;
        } else {
            return empty();
        }
    }

    @Override
    public List<V> drop(int n) {
        if (n > 0) {
            return empty();
        } else {
            return this;
        }
    }

    @Override
    public List<V> strict() {
        return this;
    }

    @Override
    public List<V> reverse() {
        return this;
    }

}
