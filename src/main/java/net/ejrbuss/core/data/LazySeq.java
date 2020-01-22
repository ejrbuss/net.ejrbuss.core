package net.ejrbuss.core.data;

import net.ejrbuss.core.function.Thunk;

public class LazySeq<V> implements Seq<V> {

    public static <V> Seq<V> of(Thunk<Pair<Maybe<V>, Seq<V>>> next) {
        return new LazySeq<>(next);
    }

    private final Thunk<Pair<Maybe<V>, Seq<V>>> next;

    private LazySeq(Thunk<Pair<Maybe<V>, Seq<V>>> next) {
        this.next = next.cached();
    }

    @Override
    public Maybe<V> first() {
        return next.apply().left();
    }

    @Override
    public Seq<V> rest() {
        return next.apply().right();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Seq) {
            return Seq.equal(this, (Seq<?>) other);
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + join(", ") + ")";
    }

}
