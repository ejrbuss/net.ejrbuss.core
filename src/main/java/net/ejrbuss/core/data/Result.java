package net.ejrbuss.core.data;

import net.ejrbuss.core.function.Eff;
import net.ejrbuss.core.function.Fn;
import net.ejrbuss.core.function.Thunk;

import java.util.NoSuchElementException;

public abstract class Result<V, E extends Throwable> {

    public static <V, E extends Throwable> Result<V, E> ok(V value) {
        return new Ok<>(value);
    }

    public static <V, E extends Throwable> Result<V, E> error(E error) {
        return new Error<>(error);
    }

    public static <V> Result<V, NoSuchElementException> from(Maybe<V> maybe) {
        try {
            return ok(maybe.force());
        } catch(NoSuchElementException error) {
            return error(error);
        }
    }

    public static <V, E extends Throwable> Result<V, E> from(Alt<V, E> alt) {
        return alt.match(
                Result::ok,
                Result::error
        );
    }

    public abstract boolean isOk();

    public abstract boolean isError();

    public abstract V get(V defaultValue);

    public abstract V get(Thunk<V> defaultValue);

    public abstract void each(Eff<V> eff);

    public abstract <V2> Result<V2, E> map(Fn<V, V2> transform);

    public abstract <C> C match(Fn<V,C> okMatch, Fn<E, C> errorMatch);

    public abstract void effect(Eff<V> okMatch, Eff<E> errorMatch);

    private static class Ok<V, E extends Throwable> extends Result<V, E> {

        private final V value;

        public Ok(V value) {
            this.value = value;
        }

        @Override
        public boolean isOk() {
            return true;
        }

        @Override
        public boolean isError() {
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
            return ok(transform.apply(value));
        }

        @Override
        public <C> C match(Fn<V, C> okMatch, Fn<E, C> errorMatch) {
            return okMatch.apply(value);
        }

        @Override
        public void effect(Eff<V> okMatch, Eff<E> errorMatch) {
            okMatch.cause(value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof Result.Ok) {
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

    private static class Error<V, E extends Throwable> extends Result<V, E> {

        private final E error;

        public Error(E error) {
            this.error = error;
        }

        @Override
        public boolean isOk() {
            return false;
        }

        @Override
        public boolean isError() {
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
        public <C> C match(Fn<V, C> okMatch, Fn<E, C> errorMatch) {
            return errorMatch.apply(error);
        }

        @Override
        public void effect(Eff<V> okMatch, Eff<E> errorMatch) {
            errorMatch.cause(error);
        }

        @Override
        public int hashCode() {
            return error.hashCode();
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof Result.Error) {
                Error<?, ?> otherError = (Error<?, ?>) other;
                return error.equals(otherError.error);
            }
            return false;
        }

        @Override
        public String toString() {
            return super.toString() + "(" + error + ")";
        }
    }

}
