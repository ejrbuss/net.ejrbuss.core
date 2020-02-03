package net.ejrbuss.core.function;

@FunctionalInterface
public interface Fn<A, R> {

    R apply(A arg);

    static <A, R1, R2> Fn<A, R2> compose(Fn<R1, R2> f, Fn<A, R1> g) {
        return a -> f.apply(g.apply(a));
    }

    static <A, R1, R2> Fn<A, R2> pipe(Fn<A, R1> g, Fn<R1, R2> f) {
        return a -> f.apply(g.apply(a));
    }

    static <A, R1, R2, R3> Fn<A, R3> pipe(Fn<A, R1> h, Fn<R1, R2> g, Fn<R2, R3> f) {
        return a -> f.apply(g.apply((h.apply(a))));
    }

    default Thunk<R> thunk(A arg) {
        return () -> apply(arg);
    }

    default MemoedFn<A, R> memo() {
        return MemoedFn.of(this);
    }

    default <R2> Fn<A, R2> then(Fn<? super R, R2> fn) {
        return a -> fn.apply(apply(a));
    }

}
