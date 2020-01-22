package net.ejrbuss.core.data;

public final class Empty<V> implements Seq<V> {

    @SuppressWarnings("unchecked")
    public static <V> Empty<V> empty() {
        return (Empty<V>) empty;
    }

    private static final Empty<?> empty = new Empty<>();

    private Empty() {}

    @Override
    public Maybe<V> first() {
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



}
