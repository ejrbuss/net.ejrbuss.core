package net.ejrbuss.core.data;

import net.ejrbuss.core.function.Eff;
import net.ejrbuss.core.function.Fn;
import net.ejrbuss.core.function.Thunk;

public class Fail<V, E extends Throwable> implements Result<V, E> {

    private final E error;

    public Fail(E error) {
        this.error = error;
    }

    @Override
    public boolean isOk() {
        return false;
    }

    @Override
    public boolean isFail() {
        return true;
    }

    @Override
    public V get(V defaultValue) {
        return defaultValue;
    }

    @Override
    public V get(Thunk<V> defaultValue) {
        return defaultValue.get();
    }

    @Override
    public void each(Eff<V> eff) {}

    @Override
    @SuppressWarnings("unchecked")
    public <V2> Result<V2, E> map(Fn<V, V2> transform) {
        return (Result<V2, E>) this;
    }

    @Override
    public <C> C match(Fn<V, C> okMatch, Fn<E, C> failMatch) {
        return failMatch.apply(error);
    }

    @Override
    public void effect(Eff<V> okMatch, Eff<E> failMatch) {
        failMatch.cause(error);
    }

    @Override
    public int hashCode() {
        return error.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Fail) {
            Fail<?, ?> otherFail = (Fail<?, ?>) other;
            return error.equals(otherFail.error);
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + error + ")";
    }

}
