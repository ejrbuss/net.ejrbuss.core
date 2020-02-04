package net.ejrbuss.core.data;

import net.ejrbuss.core.function.Eff;
import net.ejrbuss.core.function.Fn;
import net.ejrbuss.core.function.Thunk;
import net.ejrbuss.core.function.ThunkEff;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class None<V> implements Maybe<V> {

    @SuppressWarnings("rawtypes")
    public static None none = new None<>();

    @Override
    public boolean isNone() {
        return true;
    }

    @Override
    public boolean isSome() {
        return false;
    }

    @Override
    public V force() {
        throw new NoSuchElementException("force on None!");
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
    public <B> B match(Fn<? super V, B> someMatch, Thunk<B> noneMatch) {
        return noneMatch.get();
    }

    @Override
    public void effect(Eff<? super V> someEffect, ThunkEff noneEffect) {
        noneEffect.cause();
    }

    @Override
    public void each(Eff<? super V> eff) {}

    @Override
    @SuppressWarnings("unchecked")
    public <B> Maybe<B> map(Fn<? super V, B> transform) {
        return (Maybe<B>) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <B> Maybe<B> flatMap(Fn<? super V, Maybe<B>> transform) {
        return (Maybe<B>) this;
    }

    @Override
    public Maybe<V> filter(Fn<? super V, Boolean> predicate) {
        return this;
    }

    @Override
    public Iterator<V> iterator() {
        return Collections.emptyIterator();
    }

}
