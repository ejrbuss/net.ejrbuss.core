package net.ejrbuss.core.data;

import net.ejrbuss.core.function.Eff;
import net.ejrbuss.core.function.Fn;
import net.ejrbuss.core.function.Thunk;
import net.ejrbuss.core.function.ThunkEff;

public interface Maybe<V> extends Iterable<V> {

    static <V> Maybe<V> some(V value) {
        return new Some<>(value);
    }

    @SuppressWarnings("unchecked")
    static <V> Maybe<V> none() {
        return None.none;
    }

    static <V> Maybe<V> given(boolean condition, V value) {
        if (condition) {
            return some(value);
        } else {
            return none();
        }
    }

    static <V> Maybe<V> given(boolean condition, Thunk<V> value) {
        if (condition) {
            return some(value.get());
        } else {
            return none();
        }
    }

    static <V> Maybe<V> from(Result<V, ?> result) {
        return result.match(
                Maybe::some,
                error -> none()
        );
    }

    static <V> Maybe<V> from(List<V> list) {
        return list.first();
    }

    static <V> Maybe<V> or(Maybe<V> maybe1, Maybe<V> maybe2) {
        if (maybe1.isNone()) {
            return maybe2;
        } else {
            return maybe1;
        }
    }



    boolean isNone();

    boolean isSome();

    V force();

    V get(V defaultValue);

    V get(Thunk<V> defaultValue);

    <R> R match(Fn<? super V, R> someMatch, Thunk<R> noneMatch);

    void effect(Eff<? super V> someEffect, ThunkEff noneEffect);

    void each(Eff<? super V> eff);

    <V2> Maybe<V2> map(Fn<? super V, V2> transform);

    <V2> Maybe<V2> flatMap(Fn<? super V, Maybe<V2>> transform);

    Maybe<V> filter(Fn<? super V, Boolean> predicate);

}
