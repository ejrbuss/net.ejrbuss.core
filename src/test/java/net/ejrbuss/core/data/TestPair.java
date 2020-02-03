package net.ejrbuss.core.data;

import static net.ejrbuss.core.test.Dummy.*;

import org.junit.Assert;
import org.junit.Test;

public class TestPair {

    @Test
    public void testOf() {
        Pair<A, B> pair = Pair.of(A, B);
        Assert.assertEquals(A, pair.left());
        Assert.assertEquals(B, pair.right());
    }

    @Test
    public void testSwap() {
        Pair<B, A> pair = Pair.of(A, B).swap();
        Assert.assertEquals(B, pair.left());
        Assert.assertEquals(A, pair.right());
    }

    @Test
    public void testMatch() {
        Assert.assertEquals(C, Pair.of(A, B).match((a, b) -> {
            Assert.assertEquals(A, a);
            Assert.assertEquals(B, b);
            return C;
        }));
    }

    @Test
    public void testEffect() {
        Pair.of(A, B).effect((a, b) -> {
            Assert.assertEquals(A, a);
            Assert.assertEquals(B, b);
        });
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(Pair.of(A, B), Pair.of(A, B));
        Assert.assertNotEquals(Pair.of(A, B), Pair.of(C, B));
        Assert.assertNotEquals(Pair.of(A, B), Pair.of(A, C));
        Assert.assertNotEquals(Pair.of(A, B), null);
    }

    @Test
    public void testToString() {
        Assert.assertTrue(Pair.of(A, B).toString().endsWith("(" + A + ", " + B + ")"));
    }

}
