package net.ejrbuss.core.function;

public class CachedThunk<V> implements Thunk<V> {

    public static <V> CachedThunk<V> of(Thunk<V> thunk) {
        return new CachedThunk<>(thunk);
    }

    private Thunk<V> thunk;
    private V value;

    private CachedThunk(Thunk<V> thunk) {
        this.thunk = thunk;
    }

    @Override
    public synchronized V get() {
        if (value == null) {
            value = thunk.get();
            thunk = null; // Explicitly drop reference
        }
        return value;
    }

    @Override
    public CachedThunk<V> cached() {
        return this;
    }

}
