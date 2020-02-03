package net.ejrbuss.core.function;

import static net.ejrbuss.core.test.Dummy.*;

import org.junit.Assert;
import org.junit.Test;

public class TestThunk {

    @Test
    public void testOf() {
        Assert.assertEquals(A, Thunk.of(A).get());
    }

    @Test
    public void testThen() {
        Assert.assertEquals(B, Thunk.of(A).then(AtoB).get());
    }

    @Test
    public void testCached() {
        int initCount = AtoB.count;
        Thunk<B> thunk = AtoB.thunk(A).cached();
        Assert.assertEquals(B, thunk.get());
        Assert.assertEquals(initCount + 1, AtoB.count);
        Assert.assertEquals(B, thunk.get());
        Assert.assertEquals(initCount + 1, AtoB.count);
        Assert.assertEquals(thunk, thunk.cached());
    }

}
