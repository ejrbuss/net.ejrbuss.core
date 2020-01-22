package net.ejrbuss.core.function;

@FunctionalInterface
public interface Eff<A> {

    void apply(A arg);

    static Eff<?> nothing() {
        return arg -> {};
    }

    default <A2> Eff<A2> of(Fn<A2, ? extends A> fn) {
        return arg -> apply(fn.apply(arg));
    }

}
