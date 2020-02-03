package net.ejrbuss.core.function;

import static net.ejrbuss.core.test.Dummy.*;

import net.ejrbuss.core.data.Ref;
import org.junit.Assert;
import org.junit.Test;

public class TestEff {

    @Test
    public void testNothing() {
        Eff.nothing().cause(A);
        Eff.nothing().cause(B);
    }

    @Test
    public void testCompose() {
        Ref<B> eff = Ref.of(null);
        Eff.compose(eff, AtoB).cause(A);
        Assert.assertEquals(B, eff.get());
    }

    @Test
    public void testPipe() {
        Ref<C> eff;

        eff = Ref.of(null);
        Eff.pipe(BtoC, eff).cause(B);
        Assert.assertEquals(C, eff.get());

        eff = Ref.of(null);
        Eff.pipe(AtoB, BtoC, eff).cause(A);
        Assert.assertEquals(C, eff.get());
    }

}
