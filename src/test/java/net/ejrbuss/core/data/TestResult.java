package net.ejrbuss.core.data;

import static net.ejrbuss.core.test.Dummy.*;

import net.ejrbuss.core.test.Dummy;
import org.junit.Assert;
import org.junit.Test;

import java.util.NoSuchElementException;

public class TestResult {

    public Error E = new Error();

    @Test
    public void testFrom() {
        Assert.assertEquals(Result.ok(A), Result.from(Alt.left(A)));
        Assert.assertEquals(Result.error(E), Result.from(Alt.right(E)));
        Assert.assertEquals(Result.ok(A), Result.from(Maybe.some(A)));
        Assert.assertTrue(Result.from(Maybe.none()).isError());
    }

    @Test
    public void testIsOk() {
        Assert.assertTrue(Result.ok(A).isOk());
        Assert.assertFalse(Result.error(E).isOk());
    }

    @Test
    public void testIsError() {
        Assert.assertTrue(Result.error(E).isError());
        Assert.assertFalse(Result.ok(A).isError());
    }

    @Test
    public void testGet() {
        Assert.assertEquals(A, Result.<Dummy, Error>ok(A).get(B));
        Assert.assertEquals(A, Result.ok(A).get(panicThunk()));
        Assert.assertEquals(A, Result.error(E).get(A));
        Assert.assertEquals(A, Result.error(E).get(() -> A));
    }

    @Test
    public void testEach() {
        Ref<A> ref = Ref.of(null);
        Result.ok(A).each(ref);
        Assert.assertEquals(A, ref.get());
        Result.error(E).each(panicEff());
    }

    @Test
    public void testMap() {
        Assert.assertEquals(Result.ok(B), Result.ok(A).map(AtoB));
        Assert.assertEquals(Result.error(E), Result.error(E).map(panicFn()));
    }

    @Test
    public void testMatch() {
        Assert.assertEquals(B, Result.ok(A).match(
            ok -> B,
            panicFn()
        ));
        Assert.assertEquals(B, Result.error(E).match(
            panicFn(),
            error -> B
        ));
    }

    @Test
    public void testEffect() {
        Ref<A> ref = Ref.of(null);
        Result.ok(A).effect(ref, panicEff());
        Assert.assertEquals(A, ref.get());
        Ref<Error> refE = Ref.of(null);
        Result.error(E).effect(panicEff(), refE);
        Assert.assertEquals(E, refE.get());
    }

    @Test
    public void testHashcode() {
        Assert.assertEquals(Result.ok(A).hashCode(), Result.ok(A).hashCode());
        Assert.assertEquals(Result.error(E).hashCode(), Result.error(E).hashCode());
        Assert.assertNotEquals(Result.ok(A).hashCode(), Result.ok(B).hashCode());
        Assert.assertNotEquals(Result.error(E).hashCode(), Result.error(new RuntimeException()).hashCode());
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(Result.ok(A), Result.ok(A));
        Assert.assertEquals(Result.error(E), Result.error(E));
        Assert.assertNotEquals(Result.ok(A), Result.ok(B));
        Assert.assertNotEquals(Result.error(E), Result.error(new RuntimeException()));
        Assert.assertNotEquals(Result.ok(E), Result.error(E));
        Assert.assertNotEquals(Result.error(E), Result.ok(E));
    }

    @Test
    public void testToString() {
        Assert.assertTrue(Result.ok(A).toString().endsWith("(" + A + ")"));
        Assert.assertTrue(Result.error(E).toString().endsWith("(" + E + ")"));
    }

}
