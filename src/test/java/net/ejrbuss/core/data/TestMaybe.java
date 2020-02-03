package net.ejrbuss.core.data;

import static net.ejrbuss.core.test.Dummy.*;

import net.ejrbuss.core.test.Dummy;
import org.junit.Assert;
import org.junit.Test;

import java.util.NoSuchElementException;

public class TestMaybe {

    @Test
    public void testGiven() {
        Assert.assertEquals(Maybe.some(A), Maybe.given(true, A));
        Assert.assertEquals(Maybe.some(A), Maybe.given(true, () -> A));
        Assert.assertEquals(Maybe.none(), Maybe.given(false, A));
        Assert.assertEquals(Maybe.none(), Maybe.given(false, () -> A));
    }

    @Test
    public void testFrom() {
        Assert.assertEquals(Maybe.none(), Maybe.from(Seq.empty()));
        Assert.assertEquals(Maybe.some(A), Maybe.from(Seq.of(A)));
        Assert.assertEquals(Maybe.none(), Maybe.from(Result.error(null)));
        Assert.assertEquals(Maybe.some(A), Maybe.from(Result.ok(A)));
    }

    @Test
    public void testOr() {
        Assert.assertEquals(Maybe.some(A), Maybe.or(Maybe.some(A), Maybe.none()));
        Assert.assertEquals(Maybe.some(A), Maybe.or(Maybe.none(), Maybe.some(A)));
        Assert.assertEquals(Maybe.none(), Maybe.or(Maybe.none(), Maybe.none()));
    }

    @Test
    public void testIsNone() {
        Assert.assertTrue(Maybe.none().isNone());
        Assert.assertFalse(Maybe.some(A).isNone());
    }

    @Test
    public void testIsSome() {
        Assert.assertTrue(Maybe.some(A).isSome());
        Assert.assertFalse(Maybe.none().isSome());
    }

    @Test
    public void testForce() {
        Assert.assertEquals(A, Maybe.some(A).force());
    }

    @Test(expected = NoSuchElementException.class)
    public void testForceOnNone() {
        Maybe.none().force();
    }

    @Test
    public void testGet() {
        Assert.assertEquals(A, Maybe.<Dummy>some(A).get(B));
        Assert.assertEquals(A, Maybe.some(A).get(() -> {
            throw new RuntimeException();
        }));
        Assert.assertEquals(A, Maybe.none().get(A));
        Assert.assertEquals(A, Maybe.none().get(() -> A));
    }

    @Test
    public void testMatch() {
        Assert.assertEquals(B, Maybe.some(A).match(
                a -> B,
                () -> C
        ));
        Assert.assertEquals(B, Maybe.none().match(
                a -> C,
                () -> B
        ));
    }

    @Test
    public void testEffect() {
        Ref<A> ref = Ref.of(null);
        Maybe.<A>none().effect(panicEff(), () -> {});
        Assert.assertNull(ref.get());
        Maybe.some(A).effect(ref, panicThunkEff());
    }

    @Test
    public void testEach() {
        Ref<A> ref;
        ref = Ref.of(null);
        Maybe.some(A).each(ref);
        Assert.assertEquals(A, ref.get());
        ref = Ref.of(null);
        Maybe.<A>none().each(ref);
        Assert.assertNull(ref.get());
    }

    @Test
    public void testMap() {
        Assert.assertEquals(Maybe.some(B), Maybe.some(A).map(AtoB));
        Assert.assertEquals(Maybe.none(), Maybe.<A>none().map(AtoB));
    }

    @Test
    public void testFlatMap() {
        Assert.assertEquals(Maybe.some(B), Maybe.some(A).flatMap(a -> Maybe.some(B)));
        Assert.assertEquals(Maybe.none(), Maybe.<A>none().flatMap(a -> Maybe.some(B)));
    }

    @Test
    public void testFilter() {
        Assert.assertEquals(Maybe.some(A), Maybe.some(A).filter(a -> true));
        Assert.assertEquals(Maybe.none(), Maybe.some(A).filter(a -> false));
        Assert.assertEquals(Maybe.none(), Maybe.none().filter(a -> true));
    }

    @Test
    public void testHashcode() {
        Assert.assertNotEquals(A.hashCode(), Maybe.some(B).hashCode());
        Assert.assertEquals(Maybe.none().hashCode(), Maybe.none().hashCode());
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(Maybe.none(), Maybe.none());
        Assert.assertEquals(Maybe.some(A), Maybe.some(A));
        Assert.assertNotEquals(Maybe.some(A), Maybe.none());
        Assert.assertNotEquals(Maybe.none(), Maybe.some(A));
        Assert.assertNotEquals(Maybe.some(A), Maybe.some(B));
    }

    @Test
    public void testToString() {
        Assert.assertTrue(Maybe.some(A).toString().endsWith("(" + A + ")"));
    }

}
