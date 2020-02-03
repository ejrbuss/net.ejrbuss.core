package net.ejrbuss.core.function;

import static net.ejrbuss.core.test.Dummy.*;

import org.junit.Assert;
import org.junit.Test;

public class TestFn {

    @Test
    public void testCompose() {
        Assert.assertEquals(C, Fn.compose(BtoC, AtoB).apply(A));
    }

    @Test
    public void testPipe() {
        Assert.assertEquals(C, Fn.pipe(AtoB, BtoC).apply(A));
        Assert.assertEquals(D, Fn.pipe(AtoB, BtoC, CtoD).apply(A));
    }

    @Test
    public void testThunk() {
        Assert.assertEquals(B, AtoB.thunk(A).get());
    }

    @Test
    public void testMemo() {
        Fn<A, B> mAtoB = AtoB.memo();
        int initCount = AtoB.count;
        mAtoB.apply(A);
        Assert.assertEquals(initCount + 1, AtoB.count);
        mAtoB.apply(A);
        Assert.assertEquals(initCount + 1, AtoB.count);
    }

    @Test
    public void testThen() {
        Assert.assertEquals(D, AtoB.then(BtoC).then(CtoD).apply(A));
    }

}
