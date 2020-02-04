package net.ejrbuss.core.data;

import java.util.Iterator;

public class ReverseList<V> extends StrictList<V> {

    public ReverseList(java.util.List<V> list) {
        super(list);
    }

    @Override
    public Iterator<V> iterator() {
        return new Iterator<V>() {
            int i = list.size() - 1;

            @Override
            public boolean hasNext() {
                return i >= 0;
            }

            @Override
            public V next() {
                return list.get(i--);
            }
        };
    }

    @Override
    public Maybe<V> first() {
        return super.last();
    }

    @Override
    public Maybe<V> last() {
        return super.first();
    }

    @Override
    public List<V> rest() {
        if (list.isEmpty()) {
            return empty();
        } else {
            return new ReverseList<>(list.subList(0, list.size() - 1));
        }
    }

    @Override
    public Maybe<V> nth(int n) {
        n = normalizeIndex(n);
        return super.nth(list.size() - 1 - n);
    }

    @Override
    public List<V> take(int n) {
        if (n >= list.size()) {
            return this;
        } else {
            return new ReverseList<>(list.subList(list.size() - n, list.size()));
        }
    }

    @Override
    public List<V> drop(int n) {
        if (n >= list.size()) {
            return empty();
        } else {
            return new ReverseList<>(list.subList(0, list.size() - n));
        }
    }

    @Override
    public List<V> reverse() {
        return new StrictList<>(list);
    }

    @Override
    public List<V> strict() {
        return this;
    }

}
