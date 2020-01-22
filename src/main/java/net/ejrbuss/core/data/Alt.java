package net.ejrbuss.core.data;

import net.ejrbuss.core.function.Fn;
import net.ejrbuss.core.function.Thunk;

import java.util.NoSuchElementException;

public abstract class Alt<L, R> {

    public static <L, R> Alt<L, R> left(L left) {
        return new LeftAlt<>(left);
    }

    public static <L, R> Alt<L, R> right(R right) {
        return new RightAlt<>(right);
    }

    public abstract boolean isLeft();

    public abstract boolean isRight();

    public abstract L forceLeft();

    public abstract R forceRight();

    public abstract L getLeft(L defaultValue);

    public abstract L getLeft(Thunk<L> defaultValue);

    public abstract R getRight(R defaultValue);

    public abstract R getRight(Thunk<R> defaultValue);

    public abstract <C> C match(Fn<L,C> leftMatch, Fn<R, C> rightMatch);

    private static class LeftAlt<A, B> extends Alt<A, B> {

        private final A left;

        public LeftAlt(A left) {
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
        public A forceLeft() {
            return left;
        }

        @Override
        public B forceRight() {
            throw new NoSuchElementException("forceRight on LeftAlt!");
        }

        @Override
        public A getLeft(A defaultValue) {
            return left;
        }

        @Override
        public A getLeft(Thunk<A> defaultValue) {
            return defaultValue.apply();
        }

        @Override
        public B getRight(B defaultValue) {
            return defaultValue;
        }

        @Override
        public B getRight(Thunk<B> defaultValue) {
            return defaultValue.apply();
        }

        @Override
        public <C> C match(Fn<A, C> leftMatch, Fn<B, C> rightMatch) {
            return leftMatch.apply(left);
        }

        @Override
        public String toString() {
            return super.toString() + "(" + left + ")";
        }

    }

    private static class RightAlt<A, B> extends Alt<A, B> {

        private final B right;

        public RightAlt(B right) {
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
        public A forceLeft() {
            throw new NoSuchElementException("forceLeft on RightAlt!");
        }

        @Override
        public B forceRight() {
            return right;
        }

        @Override
        public A getLeft(A defaultValue) {
            return defaultValue;
        }

        @Override
        public A getLeft(Thunk<A> defaultValue) {
            return defaultValue.apply();
        }

        @Override
        public B getRight(B defaultValue) {
            return right;
        }

        @Override
        public B getRight(Thunk<B> defaultValue) {
            return right;
        }

        @Override
        public <C> C match(Fn<A, C> leftMatch, Fn<B, C> rightMatch) {
            return rightMatch.apply(right);
        }

        @Override
        public String toString() {
            return super.toString() + "(" + right + ")";
        }

    }

}
