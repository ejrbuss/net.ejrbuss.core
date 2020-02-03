package net.ejrbuss.core.data;

import static net.ejrbuss.core.test.Dummy.*;

import net.ejrbuss.core.test.Dummy;
import org.junit.Assert;
import org.junit.Test;

import java.util.NoSuchElementException;

public class TestAlt {

    @Test
    public void testFrom() {
        Assert.assertEquals(Alt.left(A), Alt.from(Result.ok(A)));
    }

    @Test
    public void testIstLeft() {
        Assert.assertTrue(Alt.left(A).isLeft());
        Assert.assertFalse(Alt.right(A).isLeft());
    }

    @Test
    public void testIsRight() {
        Assert.assertTrue(Alt.right(A).isRight());
        Assert.assertFalse(Alt.left(A).isRight());
    }

    @Test
    public void testForceLeft() {
        Assert.assertEquals(A, Alt.left(A).forceLeft());
    }

    @Test(expected = NoSuchElementException.class)
    public void testForeLeftOnRight() {
        Alt.right(A).forceLeft();
    }

    @Test
    public void testForceRight() {
        Assert.assertEquals(A, Alt.right(A).forceRight());
    }

    @Test(expected = NoSuchElementException.class)
    public void testForceRightOnLeft() {
        Alt.left(A).forceRight();
    }

    @Test
    public void testGetLeft() {
        Assert.assertEquals(A, Alt.<Dummy, Dummy>left(A).getLeft(B));
        Assert.assertEquals(A, Alt.left(A).getLeft(panicThunk()));
        Assert.assertEquals(A, Alt.right(B).getLeft(A));
        Assert.assertEquals(A, Alt.right(B).getLeft(() -> A));
    }

    @Test
    public void testGetRight() {
        Assert.assertEquals(A, Alt.<Dummy, Dummy>right(A).getRight(B));
        Assert.assertEquals(A, Alt.right(A).getRight(() -> {
           throw new RuntimeException();
        }));
        Assert.assertEquals(A, Alt.left(B).getRight(A));
        Assert.assertEquals(A, Alt.left(B).getRight(() -> A));
    }

    @Test
    public void testMatch() {
        Assert.assertEquals(B, Alt.left(A).match(
                left -> B,
                panicFn()
        ));
        Assert.assertEquals(B, Alt.right(A).match(
                panicFn(),
                right -> B
        ));
    }

    @Test
    public void testEffect() {
        Ref<A> ref;
        ref = Ref.of(null);
        Alt.<A, A>left(A).effect(ref, ref);
        Assert.assertEquals(ref.get(), A);
        ref = Ref.of(null);
        Alt.<A, A>right(A).effect(ref, ref);
        Assert.assertEquals(ref.get(), A);
    }

    @Test
    public void testHashcode() {
        Assert.assertEquals(Alt.left(A).hashCode(), Alt.left(A).hashCode());
        Assert.assertEquals(Alt.right(A).hashCode(), Alt.right(A).hashCode());
        Assert.assertNotEquals(Alt.left(A).hashCode(), Alt.left(B).hashCode());
        Assert.assertNotEquals(Alt.right(A).hashCode(), Alt.right(B).hashCode());
        Assert.assertNotEquals(Alt.left(A).hashCode(), Alt.right(A).hashCode());
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(Alt.left(A), Alt.left(A));
        Assert.assertEquals(Alt.right(A), Alt.right(A));
        Assert.assertNotEquals(Alt.left(A), Alt.left(B));
        Assert.assertNotEquals(Alt.right(A), Alt.right(B));
        Assert.assertNotEquals(Alt.left(A), Alt.right(A));
        Assert.assertNotEquals(Alt.right(A), Alt.left(A));
    }

    @Test
    public void testToString() {
        Assert.assertTrue(Alt.left(A).toString().endsWith("(" + A + ")"));
        Assert.assertTrue(Alt.right(B).toString().endsWith("(" + B + ")"));
    }

}
