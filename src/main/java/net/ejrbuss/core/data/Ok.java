package net.ejrbuss.core.data;

import net.ejrbuss.core.function.Eff;
import net.ejrbuss.core.function.Fn;
import net.ejrbuss.core.function.Thunk;

public class Ok<V, E extends Throwable> implements Result<V, E> {

    private final V value;

    public Ok(V value) {
        this.value = value;
    }

    @Override
    public boolean isOk() {
        return true;
    }

    @Override
    public boolean isFail() {
        return false;
    }

    @Override
    public V get(V defaultValue) {
        return value;
    }

    @Override
    public V get(Thunk<V> defaultValue) {
        return value;
    }

    @Override
    public void each(Eff<V> eff) {
        eff.cause(value);
    }

    @Override
    public <V2> Result<V2, E> map(Fn<V, V2> transform) {
        return new Ok<>(transform.apply(value));
    }

    @Override
    public <C> C match(Fn<V, C> okMatch, Fn<E, C> failMatch) {
        return okMatch.apply(value);
    }

    @Override
    public void effect(Eff<V> okMatch, Eff<E> failMatch) {
        okMatch.cause(value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Ok) {
            Ok<?, ?> otherOk = (Ok<?, ?>) other;
            return value.equals(otherOk.value);
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + value + ")";
    }

}
