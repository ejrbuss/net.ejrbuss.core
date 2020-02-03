package net.ejrbuss.core.data;

import net.ejrbuss.core.function.Eff;
import net.ejrbuss.core.function.Fn;
import net.ejrbuss.core.function.Fn2;
import net.ejrbuss.core.function.Thunk;

import java.util.*;

public abstract class Seq<V> implements Traversable<V> {

    private static final Seq<?> empty = new Empty<>();

    @SuppressWarnings("unchecked")
    public static <V> Seq<V> empty() {
        return (Seq<V>) empty;
    }

    public static <V> Seq<V> of(V value) {
        return new Single<>(value);
    }

    @SafeVarargs
    public static <V> Seq<V> of(V ... values) {
        return new Strict<>(Arrays.asList(values));
    }

    public static <V> Seq<V> from(Iterable<V> values) {
        if (values instanceof Seq) {
            return (Seq<V>) values;
        }
        List<V> list = new ArrayList<>();
        for (V v : values) {
            list.add(v);
        }
        return new Strict<>(list);
    }

    public static Seq<Integer> range(int end) {
        return range(0, end);
    }

    public static Seq<Integer> range(int start, int end) {
        return range(start, end, start < end ? 1 : -1);
    }

    public static Seq<Integer> range(int start, int end, int step) {
        if ((step < 0 && start <= end) || (step > 0 && start >= end)) {
            return empty();
        }
        return new Lazy<>(() -> Pair.of(Maybe.some(start), range(start + step, end, step)));
    }

    public static Seq<Double> range(double start, double end, double step) {
        if ((step < 0 && start <= end) || (step > 0 && start >= end)) {
            return empty();
        }
        return new Lazy<>(() -> Pair.of(Maybe.some(start), range(start + step, end, step)));
    }

    public static <V> Seq<V> concat(Seq<V> seq1, Seq<V> seq2) {
        if (seq1.isEmpty()) {
            return seq2;
        }
        return new Lazy<>(() -> Pair.of(seq1.first(), concat(seq1.rest(), seq2)));
    }

    public static <V1, V2> Seq<Pair<V1, V2>> zip(Seq<V1> seq1, Seq<V2> seq2) {
        return zipWith(Pair::of, seq1, seq2);
    }

    public static <V1, V2, R> Seq<R> zipWith(Fn2<V1, V2, R> zipper, Seq<V1> seq1, Seq<V2> seq2) {
        if (seq1.isEmpty() || seq2.isEmpty()) {
            return empty();
        }
        return new Lazy<>(() -> Pair.of(
                Maybe.some(zipper.apply(seq1.first().force(), seq2.first().force())),
                zipWith(zipper, seq1.rest(), seq2.rest())
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
        if (other instanceof Seq) {
            Seq<?> seq1 = (Seq<?>) this;
            Seq<?> seq2 = (Seq<?>) other;
            while (!seq1.isEmpty() && !seq2.isEmpty()) {
                if (!seq1.first().equals(seq2.first())) {
                    return false;
                }
                seq1 = seq1.rest();
                seq2 = seq2.rest();
            }
            return seq1.isEmpty() && seq2.isEmpty();
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

    public Seq<V> append(V value) {
        return concat(this, new Single<>(value));
    }

    public Seq<V> prepend(V value) {
        return concat(new Single<>(value), this);
    }

    public Seq<V> push(V value) {
        return prepend(value);
    }

    public Seq<V> pop() {
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

    public Seq<V> takeWhile(Fn<? super V, Boolean> pred) {
        if (isEmpty() || !pred.apply(first().force())) {
            return empty();
        }
        return new Lazy<>(() -> Pair.of(first(), rest().takeWhile(pred)));
    }

    public Seq<V> dropWhile(Fn<? super V, Boolean> pred) {
        Seq<V> seq = this;
        while (!seq.isEmpty() && pred.apply(seq.first().force())) {
            seq = seq.rest();
        }
        return seq;
    }

    public <V2> Seq<V2> map(Fn<? super V, V2> transform) {
        if (isEmpty()) {
            return empty();
        }
        return new Lazy<>(() -> Pair.of(
                Maybe.some(transform.apply(first().force())),
                rest().map(transform))
        );
    }

    public <V2> Seq<V2> flatMap(Fn<? super V, Iterable<V2>> transform) {
        if (isEmpty()) {
            return empty();
        }
        return concat(Seq.from(transform.apply(first().force())), new Lazy<>(() -> {
            Seq<V2> seq = rest().flatMap(transform);
            return Pair.of(seq.first(), seq.rest());
        }));
    }

    public Seq<V> filter(Fn<V, Boolean> pred) {
        if (isEmpty()) {
            return empty();
        }
        if (pred.apply(first().force())) {
            return new Lazy<>(() -> Pair.of(
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

    public Seq<V> sort(Comparator<V> comparator) {
        return new Strict<>(this).sort(comparator);
    }

    public <C extends Comparable<C>> Seq<V> sortBy(Fn<? super V, C> selector) {
        return sort(Comparator.comparing(selector::apply));
    }

    public Seq<V> slice(int start, int end) {
        start = normalizeIndex(start);
        end = normalizeIndex(end);
        Seq<V> seq = drop(start);
        return seq.take(end - start);
    }

    public Seq<Pair<Integer, V>> enumerate() {
        return zip(range(count()), this);
    }

    public Seq<Seq<V>> chunk(int n) {
        if (isEmpty()) {
            return empty();
        }
        if (n < 0) {
            return reverse().chunk(-n);
        }
        return new Lazy<>(() -> Pair.of(
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



    public abstract Seq<V> rest();

    public abstract boolean isEmpty();

    public abstract Maybe<V> last();

    public abstract Maybe<V> nth(int n);

    public abstract int count();

    public abstract Seq<V> take(int n);

    public abstract Seq<V> drop(int n);

    public abstract Seq<V> strict();

    public abstract Seq<V> reverse();



    private static class Empty<V> extends Seq<V> {

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
            if (other instanceof Seq) {
                Seq<?> otherSeq = (Seq<?>) other;
                return otherSeq.isEmpty();
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
        public Seq<V> takeWhile(Fn<? super V, Boolean> pred) {
            return this;
        }

        @Override
        public Seq<V> dropWhile(Fn<? super V, Boolean> pred) {
            return this;
        }

        @Override
        public <V2> Seq<V2> map(Fn<? super V, V2> transform) {
            return empty();
        }

        @Override
        public <V2> Seq<V2> flatMap(Fn<? super V, Iterable<V2>> transform) {
            return empty();
        }

        @Override
        public Seq<V> filter(Fn<V, Boolean> pred) {
            return this;
        }

        @Override
        public <R> R reduce(Fn2<R, ? super V, R> reducer, R init) {
            return init;
        }

        @Override
        public Seq<V> sort(Comparator<V> comparator) {
            return this;
        }

        @Override
        public Seq<Pair<Integer, V>> enumerate() {
            return empty();
        }

        @Override
        public Seq<Seq<V>> chunk(int n) {
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
        public Seq<V> rest() {
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
        public Seq<V> take(int n) {
            return this;
        }

        @Override
        public Seq<V> drop(int n) {
            return this;
        }

        @Override
        public Seq<V> reverse() {
            return this;
        }

        @Override
        public Seq<V> strict() {
            return this;
        }

    }



    private static class Single<V> extends Seq<V> {

        private final V first;

        public Single(V first) {
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
        public Seq<V> takeWhile(Fn<? super V, Boolean> pred) {
            if (pred.apply(first)) {
                return this;
            } else {
                return empty();
            }
        }

        @Override
        public Seq<V> dropWhile(Fn<? super V, Boolean> pred) {
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
        public <V2> Seq<V2> map(Fn<? super V, V2> transform) {
            return new Single<>(transform.apply(first));
        }

        @Override
        public <V2> Seq<V2> flatMap(Fn<? super V, Iterable<V2>> transform) {
            return Seq.from(transform.apply(first));
        }

        @Override
        public Seq<V> filter(Fn<V, Boolean> pred) {
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
        public Seq<V> sort(Comparator<V> comparator) {
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
        public Seq<V> rest() {
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
        public Seq<V> take(int n) {
            if (n > 0) {
                return this;
            } else {
                return empty();
            }
        }

        @Override
        public Seq<V> drop(int n) {
            if (n > 0) {
                return empty();
            } else {
                return this;
            }
        }

        @Override
        public Seq<V> strict() {
            return this;
        }

        @Override
        public Seq<V> reverse() {
            return this;
        }

    }



    private static class Cons<V> extends Seq<V> {

        private final V first;
        private final Seq<V> rest;

        public Cons(V first, Seq<V> rest) {
            this.first = first;
            this.rest = rest;
        }

        @Override
        public Iterator<V> iterator() {
            return new Iterator<V>() {
                private Iterator<V> restIter = rest.iterator();
                private boolean firstIter;

                @Override
                public boolean hasNext() {
                    return firstIter || rest.iterator().hasNext();
                }

                @Override
                public V next() {
                    if (firstIter) {
                        firstIter = false;
                        return first;
                    }
                    return restIter.next();
                }
            };
        }

        @Override
        public Maybe<V> first() {
            return Maybe.some(first);
        }

        @Override
        public Seq<V> rest() {
            return rest;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public Maybe<V> last() {
            return rest.last();
        }

        @Override
        public Maybe<V> nth(int n) {
            n = normalizeIndex(n);
            if (n == 0) {
                return Maybe.some(first);
            } else {
                return rest.nth(n - 1);
            }
        }

        @Override
        public int count() {
            return rest.count() + 1;
        }

        @Override
        public Seq<V> take(int n) {
            if (n > 0) {
                return new Cons<>(first, rest.take(n - 1));
            } else {
                return empty();
            }
        }

        @Override
        public Seq<V> drop(int n) {
            if (n > 0) {
                return rest.drop(n - 1);
            } else {
                return this;
            }
        }

        @Override
        public Seq<V> reverse() {
            return strict().reverse();
        }

        @Override
        public Seq<V> strict() {
            return new Strict<>(this);
        }

    }



    private static class Lazy<V> extends Seq<V> {

        private final Thunk<Pair<Maybe<V>, Seq<V>>> next;

        private Lazy(Thunk<Pair<Maybe<V>, Seq<V>>> next) {
            this.next = next;
        }

        @Override
        public Iterator<V> iterator() {
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
        public Seq<V> rest() {
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
        public Seq<V> take(int n) {
            if (isEmpty() || n <= 0) {
                return empty();
            }
            return new Lazy<>(() -> Pair.of(first(), rest().take(n - 1)));
        }

        @Override
        public Seq<V> drop(int n) {
            Seq<V> seq = this;
            while (!seq.isEmpty() && n-- > 0) {
                seq = seq.rest();
            }
            return seq;
        }

        @Override
        public Seq<V> reverse() {
            return strict().reverse();
        }

        @Override
        public Seq<V> strict() {
            return new Strict<>(this);
        }
    }



    private static class Strict<V> extends Seq<V> {

        protected final List<V> list;

        public Strict(List<V> list) {
            this.list = list;
        }

        public Strict(Seq<V> seq) {
            list = new ArrayList<>();
            for (V v : seq) {
                list.add(v);
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
        public Seq<V> rest() {
            if (list.isEmpty()) {
                return this;
            } else {
                return new Strict<>(list.subList(1, list.size()));
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
        public Seq<V> take(int n) {
            if (n >= list.size()) {
                return this;
            } else {
                return new Strict<>(list.subList(0, n));
            }
        }

        @Override
        public Seq<V> drop(int n) {
            if (n >= list.size()) {
                return empty();
            } else {
                return new Strict<>(list.subList(n, list.size()));
            }
        }

        @Override
        public Seq<V> sort(Comparator<V> comparator) {
            List<V> newList = new ArrayList<>(list);
            newList.sort(comparator);
            return new Strict<>(newList);
        }

        @Override
        public Seq<V> reverse() {
            return new Reverse<>(list);
        }

        @Override
        public Seq<V> strict() {
            return this;
        }

    }

    private static class Reverse<V> extends Strict<V> {


        public Reverse(List<V> list) {
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
        public Seq<V> rest() {
            if (list.isEmpty()) {
                return empty();
            } else {
                return new Reverse<>(list.subList(0, list.size() - 1));
            }
        }

        @Override
        public Maybe<V> nth(int n) {
            n = normalizeIndex(n);
            return super.nth(list.size() - 1 - n);
        }

        @Override
        public Seq<V> take(int n) {
            if (n >= list.size()) {
                return this;
            } else {
                return new Reverse<>(list.subList(list.size() - n, list.size()));
            }
        }

        @Override
        public Seq<V> drop(int n) {
            if (n >= list.size()) {
                return empty();
            } else {
                return new Reverse<>(list.subList(0, list.size() - n));
            }
        }

        @Override
        public Seq<V> reverse() {
            return new Strict<>(list);
        }

        @Override
        public Seq<V> strict() {
            return this;
        }

    }

    /*
    private static class Infinite<V> extends Seq2<V> {

        @Override
        public Maybe<V> first() {
            return null;
        }

        @Override
        public Traversable<V> rest() {
            return null;
        }

        @Override
        public boolean isFinite() {
            return true;
            // !bina246812$#
        }
    }
    */

}
