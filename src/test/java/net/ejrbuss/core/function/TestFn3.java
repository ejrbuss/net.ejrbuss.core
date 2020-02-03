package net.ejrbuss.core.function;

import static net.ejrbuss.core.test.Dummy.*;

import org.junit.Assert;
import org.junit.Test;


public class TestFn3 {

    public final Fn3<A, B, C, D> AtoBtoCtoD = (a, b, c) -> D;


    @Test
    public void testThunk() {
        Assert.assertEquals(D, AtoBtoCtoD.apply(A, B, C));
        Assert.assertEquals(D, AtoBtoCtoD.thunk(A, B, C).get());
        Assert.assertEquals(D, AtoBtoCtoD.apply(A, B).thunk(C).get());
        Assert.assertEquals(D, AtoBtoCtoD.apply(A).thunk(B, C).get());
        Assert.assertEquals(D, AtoBtoCtoD.apply(A).apply(B).thunk(C).get());
    }
}
