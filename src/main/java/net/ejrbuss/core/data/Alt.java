package net.ejrbuss.core.data;

import net.ejrbuss.core.function.Eff;
import net.ejrbuss.core.function.Fn;
import net.ejrbuss.core.function.Thunk;

import java.util.NoSuchElementException;

public abstract class Alt<L, R> {

    public static <L, R> Alt<L, R> left(L left) {
        return new Left<>(left);
    }

    public static <L, R> Alt<L, R> right(R right) {
        return new Right<>(right);
    }

    public static <L, R extends Throwable> Alt<L, R> from(Result<L, R> result) {
        return result.match(
                Left::new,
                Right::new
        );
    }



    public abstract boolean isLeft();

    public abstract boolean isRight();

    public abstract L forceLeft();

    public abstract R forceRight();

    public abstract L getLeft(L defaultValue);

    public abstract L getLeft(Thunk<? extends L> defaultValue);

    public abstract R getRight(R defaultValue);

    public abstract R getRight(Thunk<? extends R> defaultValue);

    public abstract <C> C match(Fn<? super L, C> leftMatch, Fn<? super R, C> rightMatch);

    public abstract void effect(Eff<? super L> leftEfect, Eff<? super R> rightEffect);



    private static class Left<L, R> extends Alt<L, R> {

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
            if (other instanceof Alt.Left) {
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



    private static class Right<L, R> extends Alt<L, R> {

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
            if (other instanceof Alt.Right) {
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

}
