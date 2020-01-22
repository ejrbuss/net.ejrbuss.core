package net.ejrbuss.core.data;

import net.ejrbuss.core.function.Eff;
import net.ejrbuss.core.function.Eff2;
import net.ejrbuss.core.function.Fn;
import net.ejrbuss.core.function.Fn2;

import java.util.Iterator;

public interface Seq<V> extends Iterable<V> {

    Maybe<V> first();

    Seq<V> rest();

    static <V> Seq<V> empty() {
        return Empty.empty();
    }

    static <V> Seq<V> of(V ... values) {
        // TODO default implementation should be Vec
        Seq<V> seq = empty();
        for (V v : values) {
            seq = seq.prepend(v);
        }
        return seq;
    }

    static Seq<Integer> range(int end) {
        return range(0, end);
    }

    static Seq<Integer> range(int start, int end) {
        return range(start, end, start < end ? 1 : -1);
    }

    static Seq<Integer> range(int start, int end, int step) {
        if ((step < 0 && start < end) || (step > 0 && start >= end)) {
            return empty();
        }
        return LazySeq.of(() -> Pair.of(Maybe.some(start), range(start + step, end, step)));
    }

    static Seq<Double> range(double start, double end, double step) {
        if ((step < 0 && start < end) || (step > 0 && start >= end)) {
            return empty();
        }
        return LazySeq.of(() -> Pair.of(Maybe.some(start), range(start + step, end, step)));
    }

    static <V> Seq<V> concat(Seq<V> seq1, Seq<V> seq2) {
        if (seq1.isEmpty()) {
            return seq2;
        }
        return LazySeq.of(() -> Pair.of(seq1.first(), concat(seq1.rest(), seq2)));
    }

    static <V1, V2> Seq<Pair<V1, V2>> zip(Seq<V1> seq1, Seq<V2> seq2) {
        return zipWith(Pair::of, seq1, seq2);
    }

    static <V1, V2, R> Seq<R> zipWith(Fn2<V1, V2, R> zipper, Seq<V1> seq1, Seq<V2> seq2) {
        if (seq1.isEmpty() || seq2.isEmpty()) {
            return empty();
        }
        return LazySeq.of(() -> Pair.of(
                Maybe.some(zipper.apply(seq1.first().force(), seq2.first().force())),
                zipWith(zipper, seq1.rest(), seq2.rest())
        ));
    }

    static boolean equal(Seq<?> seq1, Seq<?> seq2) {
        while (!seq1.isEmpty() && !seq2.isEmpty()) {
            if (!seq1.first().force().equals(seq2.first().force())) {
                return false;
            }
        }
        return seq1.isEmpty() && seq2.isEmpty();
    }

    @Override
    default Iterator<V> iterator() {
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

    default void each(Eff<? super V> eff) {
        for (V v : this) {
            eff.apply(v);
        }
    }

    default boolean isEmpty() {
        return first().isNone();
    }

    default int normalizeIndex(int index) {
        if (index >= 0) {
            return index;
        } else {
            return count() + index;
        }
    }

    default Maybe<V> last() {
        V last = null;
        for (V v : this) {
            last = v;
        }
        return last == null ? Maybe.none() : Maybe.some(last);
    }

    default Maybe<V> nth(int n) {
        n = normalizeIndex(n);
        int i = 0;
        for (V v : this) {
            if (i++ == n) {
              return Maybe.some(v);
            }
        }
        return Maybe.none();
    }

    default int count() {
        int i = 0;
        for (V v : this) {
            i++;
        }
        return i;
    }

    default boolean has(V value) {
        for (V v : this) {
            if (value.equals(v)) {
                return true;
            }
        }
        return false;
    }

    default Seq<V> append(V value) {
        return concat(this, Seq.of(value));
    }

    default Seq<V> prepend(V value) {
        return LazySeq.of(() -> Pair.of(Maybe.some(value), this));
    }

    default Seq<V> push(V value) {
        return prepend(value);
    }

    default Maybe<V> peek() {
        return first();
    }

    default Seq<V> pop() {
        return rest();
    }

    default Maybe<Integer> index(V value) {
        int i = 0;
        for (V v : this) {
            if (value.equals(v)) {
                return Maybe.some(i);
            }
            i++;
        }
        return Maybe.none();
    }

    default Maybe<Integer> indexBy(Fn<V, Boolean> pred) {
        int i = 0;
        for (V v : this) {
            if (pred.apply(v)) {
                return Maybe.some(i);
            }
            i++;
        }
        return Maybe.none();
    }

    default Maybe<V> find(Fn<V, Boolean> pred) {
        for (V v : this) {
            if (pred.apply(v)) {
                return Maybe.some(v);
            }
        }
        return Maybe.none();
    }

    default Seq<V> take(int n) {
        if (isEmpty() || n <= 0) {
            return empty();
        }
        return LazySeq.of(() -> Pair.of(first(), rest().take(n - 1)));
    }

    default Seq<V> takeWhile(Fn<V, Boolean> pred) {
        if (isEmpty() || !pred.apply(first().force())) {
            return empty();
        }
        return LazySeq.of(() -> Pair.of(first(), rest().takeWhile(pred)));
    }

    default Seq<V> drop(int n) {
        Seq<V> seq = this;
        while (!seq.isEmpty() && n-- > 0) {
            seq = seq.rest();
        }
        return seq;
    }

    default Seq<V> dropWhile(Fn<V, Boolean> pred) {
        Seq<V> seq = this;
        while (!seq.isEmpty() && pred.apply(seq.first().force())) {
            seq = seq.rest();
        }
        return seq;
    }

    default <V2> Seq<V2> map(Fn<V, V2> transform) {
        if (isEmpty()) {
            return empty();
        }
        return LazySeq.of(() -> Pair.of(
                Maybe.some(transform.apply(first().force())),
                rest().map(transform))
        );
    }

    default <V2> Seq<V2> flatMap(Fn<V, Seq<V2>> transform) {
        if (isEmpty()) {
            return empty();
        }
        return concat(transform.apply(first().force()), LazySeq.of(() -> {
            Seq<V2> seq = rest().flatMap(transform);
            return Pair.of(seq.first(), seq.rest());
        }));
    }

    default Seq<V> filter(Fn<V, Boolean> pred) {
        if (isEmpty()) {
            return empty();
        }
        if (pred.apply(first().force())) {
            return LazySeq.of(() -> Pair.of(
                first(),
                rest().filter(pred)
            ));
        } else {
            return rest().filter(pred);
        }
    }

    default <R> R reduce(Fn2<R, V, R> reducer, R init) {
        for (V v : this) {
            init = reducer.apply(init, v);
        }
        return init;
    }

    default Seq<V> reverse() {
        // TODO with vec (index in reverse)
        Seq<V> seq = empty();
        for (V v : this) {
            seq = seq.prepend(v);
        }
        return seq;
    }

    default Seq<V> sort() {
        // TODO with vec
        return null;
    }

    default <C extends Comparable<C>> Seq<V> sortBy(Fn<V, C> selector) {
        // TODO with vec
        return null;
    }

    default Seq<V> slice(int start, int end) {
        start = normalizeIndex(start);
        end = normalizeIndex(end);
        Seq<V> seq = drop(start);
        return seq.take(end - start);
    }

    default Seq<Pair<Integer, V>> enumerate() {
        return zip(range(count()), this);
    }

    default Seq<Seq<V>> chunk(int n) {
        int i = 0;
        Seq<Seq<V>> chunks = empty();
        Seq<V> chunk = empty();
        for (V v : this) {
            if (i++ % n == 0) {
                if (!chunk.isEmpty()) {
                    chunks = chunks.append(chunk);
                    chunk = empty();
                }
            }
            chunk = chunk.append(v);
        }
        if (!chunk.isEmpty()) {
            chunks.append(chunk);
        }
        return chunks;
    }

    default Boolean any(Fn<V, Boolean> pred) {
        for (V v : this) {
            if (pred.apply(v)) {
                return true;
            }
        }
        return false;
    }

    default Boolean all(Fn<V, Boolean> pred) {
        for (V v : this) {
            if (!pred.apply(v)) {
                return false;
            }
        }
        return true;
    }

    default String join() {
        return join("");
    }

    default String join(String seperator) {
        int i = 0;
        StringBuilder builder = new StringBuilder();
        builder.append(first().match(Object::toString, ""));
        for (V v : rest()) {
            builder.append(seperator + v);
        }
        return builder.toString();
    }

}
