package net.ejrbuss.core.data;

import org.junit.Assert;
import org.junit.Test;


public class TestPair {

    @Test
    public void testOf() {
        Pair<String, Integer> pair = Pair.of("test", 42);
        Assert.assertEquals("test", pair.left());
        Assert.assertEquals(Integer.valueOf(42), pair.right());
    }

    @Test
    public void testSwap() {
        Pair<Integer, String> pair = Pair.of("test", 42).swap();
        Assert.assertEquals("test", pair.right());
        Assert.assertEquals(Integer.valueOf(42), pair.left());
    }

    @Test
    public void testEquality() {
        Assert.assertEquals(Pair.of(1, 2), Pair.of(1, 2));
        Assert.assertNotEquals(Pair.of(2, 1), Pair.of(2, 2));
    }

    @Test
    public void testToString() {
        Assert.assertTrue(Pair.of(3, 4).toString().endsWith("(3, 4)"));
    }

}
