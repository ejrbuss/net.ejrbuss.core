package net.ejrbuss.core.data;

import net.ejrbuss.core.function.Eff;
import net.ejrbuss.core.function.Fn;
import net.ejrbuss.core.function.Thunk;
import net.ejrbuss.core.function.ThunkEff;

import java.util.Collections;
import java.util.Iterator;

public class Some<V> implements Maybe<V> {

    private final V value;

    public Some(V value) {
        this.value = value;
    }

    @Override
    public boolean isNone() {
        return false;
    }

    @Override
    public boolean isSome() {
        return true;
    }

    @Override
    public V force() {
        return value;
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
    public <B> B match(Fn<? super V, B> someMatch, Thunk<B> noneMatch) {
        return someMatch.apply(value);
    }

    @Override
    public void effect(Eff<? super V> someEffect, ThunkEff noneEffect) {
        someEffect.cause(value);
    }

    @Override
    public void each(Eff<? super V> eff) {
        eff.cause(value);
    }

    @Override
    public <B> Maybe<B> map(Fn<? super V, B> transform) {
        return new Some<>(transform.apply(value));
    }

    @Override
    public <B> Maybe<B> flatMap(Fn<? super V, Maybe<B>> transform) {
        return transform.apply(value);
    }

    @Override
    public Maybe<V> filter(Fn<? super V, Boolean> predicate) {
        if (predicate.apply(value)) {
            return this;
        } else {
            return Maybe.none();
        }
    }

    @Override
    public Iterator<V> iterator() {
        return Collections.singleton(value).iterator();
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Some) {
            Some<?> otherSome = (Some<?>) other;
            return value.equals(otherSome.value);
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + value + ")";
    }

}
