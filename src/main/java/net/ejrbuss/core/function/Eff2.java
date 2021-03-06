package net.ejrbuss.core.function;

import net.ejrbuss.core.data.Pair;

@FunctionalInterface
public interface Eff2<A1, A2> extends Fn<A1, Eff<A2>> {

    void cause(A1 arg1, A2 arg2);

    static <A1, A2> Eff2<A1, A2> from(Eff<Pair<A1, A2>> eff) {
        return (arg1, arg2) -> eff.cause(Pair.of(arg1, arg2));
    }

    @Override
    default Eff<A2> apply(A1 arg1) {
        return arg2 -> cause(arg1, arg2);
    }

    default void spread(Pair<A1, A2> args) {
        cause(args.left(), args.right());
    }

    default Eff2<A2, A1> swap() {
        return (arg2, arg1) -> cause(arg1, arg2);
    }

}
