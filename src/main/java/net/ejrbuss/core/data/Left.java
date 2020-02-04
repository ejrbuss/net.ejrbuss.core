package net.ejrbuss.core.data;

import net.ejrbuss.core.function.Eff;
import net.ejrbuss.core.function.Fn;
import net.ejrbuss.core.function.Thunk;

import java.util.NoSuchElementException;

public class Left<L, R> implements Alt<L, R> {

    private final L left;

    public Left(L left) {
        this.left = left;
    }

    @Override
    public boolean isLeft() {
        return true;
    }

    @Override
    public boolean isRight() {
        return false;
    }

    @Override
    public L forceLeft() {
        return left;
    }

    @Override
    public R forceRight() {
        throw new NoSuchElementException("forceRight on LeftAlt!");
    }

    @Override
    public L getLeft(L defaultValue) {
        return left;
    }

    @Override
    public L getLeft(Thunk<? extends L> defaultValue) {
        return left;
    }

    @Override
    public R getRight(R defaultValue) {
        return defaultValue;
    }

    @Override
    public R getRight(Thunk<? extends R> defaultValue) {
        return defaultValue.get();
    }

    @Override
    public <C> C match(Fn<? super L, C> leftMatch, Fn<? super R, C> rightMatch) {
        return leftMatch.apply(left);
    }

    @Override
    public void effect(Eff<? super L> leftEfect, Eff<? super R> rightEffect) {
        leftEfect.cause(left);
    }

    @Override
    public int hashCode() {
        return "left".hashCode() ^ left.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Left) {
            Left<?, ?> otherLeft = (Left<?, ?>) other;
            return left.equals(otherLeft.left);
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + left + ")";
    }

}