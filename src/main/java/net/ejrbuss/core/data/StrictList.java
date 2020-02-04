package net.ejrbuss.core.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class StrictList<V> extends List<V> {

    protected final java.util.List<V> list;

    public StrictList(java.util.List<V> list) {
        this.list = list;
    }

    public StrictList(List<V> list) {
        this.list = new ArrayList<>();
        for (V v : list) {
            this.list.add(v);
        }
    }

    @Override
    public Iterator<V> iterator() {
        return list.iterator();
    }

    @Override
    public Maybe<V> first() {
        if (list.isEmpty()) {
            return Maybe.none();
        } else {
            return Maybe.some(list.get(0));
        }
    }

    @Override
    public Maybe<V> last() {
        if (list.isEmpty()) {
            return Maybe.none();
        } else {
            return Maybe.some(list.get(list.size() - 1));
        }
    }

    @Override
    public List<V> rest() {
        if (list.isEmpty()) {
            return this;
        } else {
            return new StrictList<>(list.subList(1, list.size()));
        }
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public Maybe<V> nth(int n) {
        n = normalizeIndex(n);
        if (n < 0 || n >= list.size()) {
            return Maybe.none();
        } else {
            return Maybe.some(list.get(n));
        }
    }

    @Override
    public int count() {
        return list.size();
    }

    @Override
    public List<V> take(int n) {
        if (n >= list.size()) {
            return this;
        } else {
            return new StrictList<>(list.subList(0, n));
        }
    }

    @Override
    public List<V> drop(int n) {
        if (n >= list.size()) {
            return empty();
        } else {
            return new StrictList<>(list.subList(n, list.size()));
        }
    }

    @Override
    public List<V> sort(Comparator<V> comparator) {
        java.util.List<V> newList = new ArrayList<>(list);
        newList.sort(comparator);
        return new StrictList<>(newList);
    }

    @Override
    public List<V> reverse() {
        return new ReverseList<>(list);
    }

    @Override
    public List<V> strict() {
        return this;
    }

}
