package net.ejrbuss.core.function;

@FunctionalInterface
public interface Fn<A, R> {

    R apply(A arg);

    default Thunk<R> thunk(A arg) {
        return () -> apply(arg);
    }

    default <A2> Fn<A2, R> compose(Fn<A2, ? extends A> fn) {
        return a2 -> apply(fn.apply(a2));
    }

    default <R2> Fn<A, R2> then(Fn<? super R, R2> fn) {
        return a -> fn.apply(apply(a));
    }

}
