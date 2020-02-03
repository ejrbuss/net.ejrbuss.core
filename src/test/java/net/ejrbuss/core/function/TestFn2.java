package net.ejrbuss.core.function;

import static net.ejrbuss.core.test.Dummy.*;

import net.ejrbuss.core.data.Pair;
import org.junit.Assert;
import org.junit.Test;

public class TestFn2 {

    public final Fn<Pair<A, B>, C> ABtoC = ab -> C;
    public final Fn2<A, B, C> AtoBtoC = (a, b) -> C;


    @Test
    public void from() {
        Assert.assertEquals(C, Fn2.from(ABtoC).apply(A, B));
        Assert.assertEquals(C, Fn2.from(ABtoC).apply(A).apply(B));
    }

    @Test
    public void testSpread() {
        Assert.assertEquals(C, AtoBtoC.spread(Pair.of(A, B)));
    }

    @Test
    public void testSwap() {
        Assert.assertEquals(C, AtoBtoC.swap().apply(B, A));
    }

    @Test
    public void testThunk() {
        Assert.assertEquals(C, AtoBtoC.thunk(A, B).get());
        Assert.assertEquals(C, AtoBtoC.apply(A).thunk(B).get());
    }

}
