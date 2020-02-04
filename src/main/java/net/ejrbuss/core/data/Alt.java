package net.ejrbuss.core.data;

import net.ejrbuss.core.function.Eff;
import net.ejrbuss.core.function.Fn;
import net.ejrbuss.core.function.Thunk;

public interface Alt<L, R> {

    static <L, R> Alt<L, R> left(L left) {
        return new Left<>(left);
    }

    static <L, R> Alt<L, R> right(R right) {
        return new Right<>(right);
    }

    static <L, R extends Throwable> Alt<L, R> from(Result<L, R> result) {
        return result.match(
                Left::new,
                Right::new
        );
    }



    boolean isLeft();

    boolean isRight();

    L forceLeft();

    R forceRight();

    L getLeft(L defaultValue);

    L getLeft(Thunk<? extends L> defaultValue);

    R getRight(R defaultValue);

    R getRight(Thunk<? extends R> defaultValue);

    <C> C match(Fn<? super L, C> leftMatch, Fn<? super R, C> rightMatch);

    void effect(Eff<? super L> leftEfect, Eff<? super R> rightEffect);

}
