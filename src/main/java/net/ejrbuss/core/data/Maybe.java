package net.ejrbuss.core.data;

import net.ejrbuss.core.function.Eff;
import net.ejrbuss.core.function.Fn;
import net.ejrbuss.core.function.Thunk;
import net.ejrbuss.core.function.ThunkEff;

import java.util.NoSuchElementException;

public abstract class Maybe<V> implements Traversable<V> {

    public static <V> Maybe<V> some(V value) {
        return new Some<>(value);
    }

    @SuppressWarnings("unchecked")
    public static <V> Maybe<V> none() {
        return None.none;
    }

    public static <V> Maybe<V> given(boolean condition, V value) {
        if (condition) {
            return some(value);
        } else {
            return none();
        }
    }

    public static <V> Maybe<V> given(boolean condition, Thunk<V> value) {
        if (condition) {
            return some(value.get());
        } else {
            return none();
        }
    }

    public static <V> Maybe<V> from(Result<V, ?> result) {
        return result.match(
                Maybe::some,
                error -> none()
        );
    }

    public static <V> Maybe<V> from(Seq<V> seq) {
        return seq.first();
    }

    public static <V> Maybe<V> or(Maybe<V> maybe1, Maybe<V> maybe2) {
        if (maybe1.isNone()) {
            return maybe2;
        } else {
            return maybe1;
        }
    }



    public abstract boolean isNone();

    public abstract boolean isSome();

    public abstract V force();

    public abstract V get(V defaultValue);

    public abstract V get(Thunk<V> defaultValue);

    public abstract <R> R match(Fn<? super V, R> someMatch, Thunk<R> noneMatch);

    public abstract void effect(Eff<? super V> someEffect, ThunkEff noneEffect);

    public abstract void each(Eff<? super V> eff);

    public abstract <V2> Maybe<V2> map(Fn<? super V, V2> transform);

    public abstract <V2> Maybe<V2> flatMap(Fn<? super V, Maybe<V2>> transform);

    public abstract Maybe<V> filter(Fn<? super V, Boolean> predicate);



    private static class Some<V> extends Maybe<V> {

        private final V value;

        public Some(V value) {
            this.value = value;
        }

        @Override
        public Maybe<V> first() {
            return this;
        }

        @Override
        public Traversable<V> rest() {
            return none();
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
                return none();
            }
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



    private static class None<V> extends Maybe<V> {

        @SuppressWarnings("rawtypes")
        public static None none = new None<>();

        @Override
        public Maybe<V> first() {
            return this;
        }

        @Override
        public Traversable<V> rest() {
            return this;
        }

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

    }

}
