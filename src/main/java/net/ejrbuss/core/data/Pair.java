package net.ejrbuss.core.data;

public class Pair<L, R> {

    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<>(left, right);
    }

    private final L left;
    private final R right;

    private Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L left() {
        return left;
    }

    public R right() {
        return right;
    }

    public Pair<R, L> swap() {
        return new Pair<>(right, left);
    }

    @Override
    public int hashCode() {
        return left.hashCode() ^ right.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Pair) {
            Pair<?, ?> otherPair = (Pair<?, ?>) other;
            return left.equals(otherPair.left) && right.equals(otherPair.right);
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + left + ", " + right + ")";
    }

}
