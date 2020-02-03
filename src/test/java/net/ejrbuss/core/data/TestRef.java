package net.ejrbuss.core.data;

import static net.ejrbuss.core.test.Dummy.*;

import net.ejrbuss.core.test.Dummy;
import org.junit.Assert;
import org.junit.Test;

public class TestRef {

    @Test
    public void testGetAndSet() {
        Ref<Dummy> ref = Ref.of(A);
        Assert.assertEquals(A, ref.get());
        ref.set(B);
        Assert.assertEquals(B, ref.get());
    }

    @Test
    public void testSwap() {
        Ref<Dummy> ref = Ref.of(A);
        Assert.assertEquals(A, ref.swap(B));
        Assert.assertEquals(B, ref.get());
    }

    @Test
    public void testCompareAndSet() {
        Ref<Dummy> ref = Ref.of(A);
        Assert.assertTrue(ref.compareAndSet(A, B));
        Assert.assertEquals(B, ref.get());
        Assert.assertFalse(ref.compareAndSet(A, C));
        Assert.assertEquals(B, ref.get());
        Assert.assertTrue(ref.compareAndSet(B, () -> C));
        Assert.assertEquals(C, ref.get());
        Assert.assertFalse(ref.compareAndSet(A, panicThunk()));
        Assert.assertEquals(C, ref.get());
    }

    @Test
    public void testEquals() {
        Ref<A> ref = Ref.of(A);
        Assert.assertNotEquals(Ref.of(A), Ref.of(A));
        Assert.assertNotEquals(Ref.of(A), Ref.of(B));
        Assert.assertEquals(ref, ref);
    }

    @Test
    public void testToString() {
        Assert.assertTrue(Ref.of(A).toString().endsWith("(" + A + ")"));
    }

}
