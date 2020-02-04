package net.ejrbuss.core.data;

import net.ejrbuss.core.function.Eff;
import net.ejrbuss.core.function.Fn;
import net.ejrbuss.core.function.Thunk;

import java.util.NoSuchElementException;

public interface Result<V, E extends Throwable> {

    static <V, E extends Throwable> Result<V, E> ok(V value) {
        return new Ok<>(value);
    }

    static <V, E extends Throwable> Result<V, E> fail(E fail) {
        return new Fail<>(fail);
    }

    static <V> Result<V, NoSuchElementException> from(Maybe<V> maybe) {
        try {
            return ok(maybe.force());
        } catch(NoSuchElementException error) {
            return fail(error);
        }
    }

    static <V, E extends Throwable> Result<V, E> from(Alt<V, E> alt) {
        return alt.match(
                Result::ok,
                Result::fail
        );
    }

    boolean isOk();

    boolean isFail();

    V get(V defaultValue);

    V get(Thunk<V> defaultValue);

    void each(Eff<V> eff);

    <V2> Result<V2, E> map(Fn<V, V2> transform);

    <C> C match(Fn<V,C> okMatch, Fn<E, C> failMatch);

    void effect(Eff<V> okMatch, Eff<E> failMatch);

}
