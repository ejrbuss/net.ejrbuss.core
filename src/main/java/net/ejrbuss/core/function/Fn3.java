package net.ejrbuss.core.function;

@FunctionalInterface
public interface Fn3<A1, A2, A3, R> extends Fn2<A1, A2, Fn<A3, R>> {

    R apply(A1 arg1, A2 arg2, A3 arg3);

    @Override
    default Fn<A3, R> apply(A1 arg1, A2 arg2) {
        return arg3 -> apply(arg1, arg2, arg3);
    }

    default Fn2<A2, A3, R> apply(A1 arg1) {
        return (arg2, arg3) -> apply(arg1, arg2, arg3);
    }

}
