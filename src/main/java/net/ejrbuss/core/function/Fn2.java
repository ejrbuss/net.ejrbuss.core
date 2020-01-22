package net.ejrbuss.core.function;

import net.ejrbuss.core.data.Pair;

@FunctionalInterface
public interface Fn2<A1, A2, R> extends Fn<A1, Fn<A2, R>> {

    R apply(A1 arg1, A2 arg2);

    static <A1, A2, R> Fn2<A1, A2, R> from(Fn<Pair<A1, A2>, R> fn) {
        return (arg1, arg2) -> fn.apply(Pair.of(arg1, arg2));
    }

    @Override
    default Fn<A2, R> apply(A1 arg1) {
        return arg2 -> apply(arg1, arg2);
    }

    default Thunk<R> thunk(A1 arg1, A2 arg2) {
        return () -> apply(arg1, arg2);
    }

    default R spread(Pair<A1, A2> args) {
        return apply(args.left(), args.right());
    }

    default Fn2<A2, A1, R> swap() {
        return (arg2, arg1) -> apply(arg1, arg2);
    }

}

