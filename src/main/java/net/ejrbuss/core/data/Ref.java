package net.ejrbuss.core.data;

import net.ejrbuss.core.function.Eff;
import net.ejrbuss.core.function.Thunk;

public class Ref<V> implements Thunk<V>, Eff<V> {

    public static <V> Ref<V> of(V value) {
        return new Ref(value);
    }

    private V value;

    private Ref(V value) {
        this.value = value;
    }

    public V get() {
        return value;
    }

    public V set(V newValue) {
        return value = newValue;
    }

    public synchronized V swap(V newValue) {
        V oldValue = value;
        value = newValue;
        return oldValue;
    }

    public synchronized boolean compareAndSet(V expected, V newValue) {
        if (value != expected) {
            return false;
        }
        set(newValue);
        return true;
    }

    public synchronized boolean compareAndSet(V expected, Thunk<V> thunk) {
        if (value != expected) {
            return false;
        }
        set(thunk.get());
        return true;
    }

    @Override
    public void cause(V value) {
        set(value);
    }

    @Override
    public String toString() {
        return super.toString() + "(" + get() + ")";
    }

}
