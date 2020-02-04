package net.ejrbuss.core.data;

import net.ejrbuss.core.function.Eff;
import net.ejrbuss.core.function.Fn;
import net.ejrbuss.core.function.Fn2;
import net.ejrbuss.core.function.Thunk;

import java.util.*;

public abstract class List<V> implements Seq<V>, Iterable<V> {

    private static final List<?> empty = new Empty<>();

    @SuppressWarnings("unchecked")
    public static <V> List<V> empty() {
        return (List<V>) empty;
    }

    public static <V> List<V> of(V value) {
        return new SingleList<>(value);
    }

    @SafeVarargs
    public static <V> List<V> of(V ... values) {
        return new StrictList<>(Arrays.asList(values));
    }

    public static <V> List<V> from(Seq<V> value) {
        return new LazyList<>(() -> Pair.of(
                value.first(),
                List.from(value.rest())
        ));
    }

    public static <V> List<V> from(Iterable<V> values) {
        if (values instanceof List) {
            return (List<V>) values;
        }
        java.util.List<V> list = new ArrayList<>();
        for (V v : values) {
            list.add(v);
        }
        return new StrictList<>(list);
    }

    public static <V> List<V> repeat(V value) {
        return new LazyList<>(() -> Pair.of(
                Maybe.some(value),
                repeat(value)
        ));
    }

    public static <V> List<V> iterate(Thunk<V> thunk) {
        return new LazyList<>(() -> Pair.of(
           Maybe.some(thunk.get()),
           iterate(thunk)
        ));
    }

    public static <V> List<V> iterate(Fn<V, V> fn, V init) {
        return new LazyList<>(() -> Pair.of(
                Maybe.some(init),
                iterate(fn, fn.apply(init))
        ));
    }

    public static List<Integer> range(int end) {
        return range(0, end);
    }

    public static List<Integer> range(int start, int end) {
        return range(start, end, start < end ? 1 : -1);
    }

    public static List<Integer> range(int start, int end, int step) {
        if ((step < 0 && start <= end) || (step > 0 && start >= end)) {
            return empty();
        }
        return new LazyList<>(() -> Pair.of(Maybe.some(start), range(start + step, end, step)));
    }

    public static List<Double> range(double start, double end, double step) {
        if ((step < 0 && start <= end) || (step > 0 && start >= end)) {
            return empty();
        }
        return new LazyList<>(() -> Pair.of(Maybe.some(start), range(start + step, end, step)));
    }

    public static <V> List<V> concat(List<V> list1, List<V> list2) {
        if (list1.isEmpty()) {
            return list2;
        }
        return new LazyList<>(() -> Pair.of(list1.first(), concat(list1.rest(), list2)));
    }

    public static <V1, V2> List<Pair<V1, V2>> zip(List<V1> list1, List<V2> list2) {
        return zipWith(Pair::of, list1, list2);
    }

    public static <V1, V2, R> List<R> zipWith(Fn2<V1, V2, R> zipper, List<V1> list1, List<V2> list2) {
        if (list1.isEmpty() || list2.isEmpty()) {
            return empty();
        }
        return new LazyList<>(() -> Pair.of(
                Maybe.some(zipper.apply(list1.first().force(), list2.first().force())),
                zipWith(zipper, list1.rest(), list2.rest())
        ));
    }



    protected int normalizeIndex(int index) {
        if (index >= 0) {
            return index;
        } else {
            return count() + index;
        }
    }



    @Override
    public boolean equals(Object other) {
        if (other instanceof List) {
            List<?> list1 = (List<?>) this;
            List<?> list2 = (List<?>) other;
            while (!list1.isEmpty() && !list2.isEmpty()) {
                if (!list1.first().equals(list2.first())) {
                    return false;
                }
                list1 = list1.rest();
                list2 = list2.rest();
            }
            return list1.isEmpty() && list2.isEmpty();
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + join(", ") + ")";
    }

    public boolean contains(V value) {
        return index(value).isSome();
    }

    public void each(Eff<? super V> eff) {
        for (V v : this) {
            eff.cause(v);
        }
    }

    public List<V> append(V value) {
        return concat(this, new SingleList<>(value));
    }

    public List<V> prepend(V value) {
        return concat(new SingleList<>(value), this);
    }

    public List<V> push(V value) {
        return prepend(value);
    }

    public List<V> pop() {
        return rest();
    }

    public Maybe<V> peek() {
        return first();
    }

    public Maybe<Integer> indexBy(Fn<? super V, Boolean> pred) {
        int i = 0;
        for (V v : this) {
            if (pred.apply(v)) {
                return Maybe.some(i);
            }
            i++;
        }
        return Maybe.none();
    }

    public Maybe<Integer> index(V value) {
        return indexBy(value::equals);
    }

    public Maybe<V> find(Fn<? super V, Boolean> pred) {
        for (V v : this) {
            if (pred.apply(v)) {
                return Maybe.some(v);
            }
        }
        return Maybe.none();
    }

    public List<V> takeWhile(Fn<? super V, Boolean> pred) {
        if (isEmpty() || !pred.apply(first().force())) {
            return empty();
        }
        return new LazyList<>(() -> Pair.of(first(), rest().takeWhile(pred)));
    }

    public List<V> dropWhile(Fn<? super V, Boolean> pred) {
        List<V> list = this;
        while (!list.isEmpty() && pred.apply(list.first().force())) {
            list = list.rest();
        }
        return list;
    }

    public <V2> List<V2> map(Fn<? super V, V2> transform) {
        if (isEmpty()) {
            return empty();
        }
        return new LazyList<>(() -> Pair.of(
                Maybe.some(transform.apply(first().force())),
                rest().map(transform))
        );
    }

    public <V2> List<V2> flatMap(Fn<? super V, Iterable<V2>> transform) {
        if (isEmpty()) {
            return empty();
        }
        return concat(List.from(transform.apply(first().force())), new LazyList<>(() -> {
            List<V2> list = rest().flatMap(transform);
            return Pair.of(list.first(), list.rest());
        }));
    }

    public List<V> filter(Fn<V, Boolean> pred) {
        if (isEmpty()) {
            return empty();
        }
        if (pred.apply(first().force())) {
            return new LazyList<>(() -> Pair.of(
                    first(),
                    rest().filter(pred)
            ));
        } else {
            return rest().filter(pred);
        }
    }

    public <R> R reduce(Fn2<R, ? super V, R> reducer, R init) {
        for (V v : this) {
            init = reducer.apply(init, v);
        }
        return init;
    }

    public V reduce(Fn2<V, ? super V, V> reducer) {
        return rest().reduce(reducer, first().force());
    }

    public List<V> sort(Comparator<V> comparator) {
        return new StrictList<>(this).sort(comparator);
    }

    public <C extends Comparable<C>> List<V> sortBy(Fn<? super V, C> selector) {
        return sort(Comparator.comparing(selector::apply));
    }

    public List<V> slice(int start, int end) {
        start = normalizeIndex(start);
        end = normalizeIndex(end);
        List<V> list = drop(start);
        return list.take(end - start);
    }

    public List<Pair<Integer, V>> enumerate() {
        return zip(range(count()), this);
    }

    public List<List<V>> chunk(int n) {
        if (isEmpty()) {
            return empty();
        }
        if (n < 0) {
            return reverse().chunk(-n);
        }
        return new LazyList<>(() -> Pair.of(
                Maybe.some(take(n)),
                drop(n).chunk(n)
        ));
    }

    public boolean any(Fn<? super V, Boolean> pred) {
        for (V v : this) {
            if (pred.apply(v)) {
                return true;
            }
        }
        return false;
    }

    public boolean all(Fn<? super V, Boolean> pred) {
        for (V v : this) {
            if (!pred.apply(v)) {
                return false;
            }
        }
        return true;
    }

    public String join(String seperator) {
        int i = 0;
        StringBuilder builder = new StringBuilder();
        builder.append(first().match(Object::toString, () -> ""));
        for (V v : rest()) {
            builder.append(seperator).append(v);
        }
        return builder.toString();
    }

    public String join() {
        return join("");
    }



    public abstract List<V> rest();

    public abstract boolean isEmpty();

    public abstract Maybe<V> last();

    public abstract Maybe<V> nth(int n);

    public abstract int count();

    public abstract List<V> take(int n);

    public abstract List<V> drop(int n);

    public abstract List<V> strict();

    public abstract List<V> reverse();

}
