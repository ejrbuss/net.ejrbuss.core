package net.ejrbuss.core.data;

import net.ejrbuss.core.function.Fn;
import net.ejrbuss.core.function.Thunk;

public abstract class Result<V, E extends Throwable> {

    public static <V, E extends Throwable> Result<V, E> ok(V value) {
        return new OkResult<>(value);
    }

    public static <V, E extends Throwable> Result<V, E> error(E error) {
        return new ErrorResult<>(error);
    }

    public abstract boolean isOk();

    public abstract boolean isError();

    public abstract V get(V defaultValue);

    public abstract V get(Thunk<V> defaultValue);

    public abstract <C> C match(Fn<V,C> okMatch, Fn<E, C> errorMatch);

    private static class OkResult<V, E extends Throwable> extends Result<V, E> {

        private final V value;

        public OkResult(V value) {
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
        public <C> C match(Fn<V, C> okMatch, Fn<E, C> errorMatch) {
            return okMatch.apply(value);
        }

        @Override
        public String toString() {
            return super.toString() + "(" + value + ")";
        }
    }

    private static class ErrorResult<V, E extends Throwable> extends Result<V, E> {

        private final E error;

        public ErrorResult(E error) {
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
            return defaultValue.apply();
        }

        @Override
        public <C> C match(Fn<V, C> okMatch, Fn<E, C> errorMatch) {
            return errorMatch.apply(error);
        }

        @Override
        public String toString() {
            return super.toString() + "(" + error + ")";
        }
    }

}
