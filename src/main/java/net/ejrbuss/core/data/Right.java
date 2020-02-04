package net.ejrbuss.core.data;

import net.ejrbuss.core.function.Eff;
import net.ejrbuss.core.function.Fn;
import net.ejrbuss.core.function.Thunk;

import java.util.NoSuchElementException;

public class Right<L, R> implements Alt<L, R> {

    private final R right;

    public Right(R right) {
        this.right = right;
    }

    @Override
    public boolean isLeft() {
        return false;
    }

    @Override
    public boolean isRight() {
        return true;
    }

    @Override
    public L forceLeft() {
        throw new NoSuchElementException("forceLeft on RightAlt!");
    }

    @Override
    public R forceRight() {
        return right;
    }

    @Override
    public L getLeft(L defaultValue) {
        return defaultValue;
    }

    @Override
    public L getLeft(Thunk<? extends L> defaultValue) {
        return defaultValue.get();
    }

    @Override
    public R getRight(R defaultValue) {
        return right;
    }

    @Override
    public R getRight(Thunk<? extends R> defaultValue) {
        return right;
    }

    @Override
    public <C> C match(Fn<? super L, C> leftMatch, Fn<? super R, C> rightMatch) {
        return rightMatch.apply(right);
    }

    @Override
    public void effect(Eff<? super L> leftEfect, Eff<? super R> rightEffect) {
        rightEffect.cause(right);
    }

    @Override
    public int hashCode() {
        return "right".hashCode() ^ right.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Right) {
            Right<?, ?> otherRight = (Right<?, ?>) other;
            return right.equals(otherRight.right);
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + right + ")";
    }

}