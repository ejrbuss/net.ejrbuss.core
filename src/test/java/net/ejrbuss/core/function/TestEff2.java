package net.ejrbuss.core.function;

import static net.ejrbuss.core.test.Dummy.*;

import net.ejrbuss.core.data.Pair;
import net.ejrbuss.core.data.Ref;
import org.junit.Assert;
import org.junit.Test;

public class TestEff2 {

    @Test
    public void testFrom() {
        Ref<Pair<A, B>> eff;

        eff = Ref.of(null);
        Eff2.from(eff).cause(A, B);
        Assert.assertEquals(Pair.of(A, B), eff.get());

        eff = Ref.of(null);
        Eff2.from(eff).apply(A).cause(B);
        Assert.assertEquals(Pair.of(A, B), eff.get());
    }

    @Test
    public void testSpread() {
        Ref<Pair<A, B>> eff = Ref.of(null);
        Eff2.from(eff).spread(Pair.of(A, B));
        Assert.assertEquals(Pair.of(A, B), eff.get());
    }

    @Test
    public void testSwap() {
        Ref<Pair<A, B>> eff = Ref.of(null);
        Eff2.from(eff).swap().cause(B, A);
        Assert.assertEquals(Pair.of(A, B), eff.get());
    }

}
