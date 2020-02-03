package net.ejrbuss.core.function;

@FunctionalInterface
public interface Eff<A> {

    void cause(A arg);

    static <A> Eff<A> nothing() {
        return arg -> {};
    }

    static <A1, A2> Eff<A1> compose(Eff<A2> f, Fn<A1, A2> g) {
        return a -> f.cause(g.apply(a));
    }

    static <A, R1> Eff<A> pipe(Fn<A, R1> g, Eff<R1> f) {
        return a -> f.cause(g.apply(a));
    }

    static <A, R1, R2> Eff<A> pipe(Fn<A, R1> h, Fn<R1, R2> g, Eff<R2> f) {
        return a -> f.cause(g.apply(h.apply(a)));
    }

}
