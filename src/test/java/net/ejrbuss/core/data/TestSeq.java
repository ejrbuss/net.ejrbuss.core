package net.ejrbuss.core.data;

import static net.ejrbuss.core.test.Dummy.*;

import net.ejrbuss.core.test.Dummy;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class TestSeq {

    @Test
    public void testFrom() {
        Assert.assertEquals(Seq.of(A), Seq.from(Maybe.some(A)));
        Assert.assertEquals(Seq.empty(), Seq.from(Maybe.none()));
        Assert.assertEquals(Seq.of(A, B, C), Seq.from(Arrays.asList(A, B, C)));
    }

    @Test
    public void testRange() {
        Assert.assertEquals(Seq.of(0, 1, 2), Seq.range(3));
        Assert.assertEquals(Seq.of(2, 3, 4), Seq.range(2, 5));
        Assert.assertEquals(Seq.of(5, 4, 3, 2, 1), Seq.range(5, 0));
        Assert.assertEquals(Seq.of(2, 4, 6), Seq.range(2, 8, 2));
        Assert.assertEquals(Seq.of(0.0, 0.5, 1.0, 1.5), Seq.range(0.0, 2.0, 0.5));
        Assert.assertEquals(Seq.of(2.0, 1.5, 1.0, 0.5), Seq.range(2.0, 0.0, -0.5));
    }

    @Test
    public void testZip() {
        Assert.assertEquals(Seq.empty(), Seq.zip(Seq.empty(), Seq.of(A, B, C)));
        Assert.assertEquals(Seq.empty(), Seq.zip(Seq.of(A, B, C), Seq.empty()));
        Assert.assertEquals(Seq.of(A, B, C).enumerate(), Seq.zip(
                Seq.range(3),
                Seq.of(A, B, C)
        ));
        Assert.assertEquals(Seq.of(0, 2, 4), Seq.zipWith(
                Integer::sum,
                Seq.range(3),
                Seq.range(3)
        ));
    }

    @Test
    public void testConcat() {
        Assert.assertEquals(Seq.of(A, B, C, D), Seq.concat(Seq.of(A, B), Seq.of(C, D)));
    }

    @Test
    public void testFirst() {
        Assert.assertEquals(Maybe.some(A), Seq.of(A).first());
        Assert.assertEquals(Maybe.some(A), Seq.of(A, B, C).first());
        Assert.assertEquals(Maybe.none(), Seq.empty().first());
        Assert.assertEquals(Maybe.none(), Seq.of().first());
    }

    @Test
    public void testLast() {
        Assert.assertEquals(Maybe.none(), Seq.empty().last());
        Assert.assertEquals(Maybe.none(), Seq.of().last());
        Assert.assertEquals(Maybe.none(), Seq.of().reverse().last());
        Assert.assertEquals(Maybe.some(A), Seq.of(A).last());
        Assert.assertEquals(Maybe.some(A), Seq.concat(Seq.of(B), Seq.of(A)).last());
        Assert.assertEquals(Maybe.some(C), Seq.of(A, B, C).last());
    }

    @Test
    public void testRest() {
        Assert.assertEquals(Seq.of(B, C), Seq.of(A, B, C).rest());
        Assert.assertEquals(Seq.empty(), Seq.of(A).rest());
        Assert.assertEquals(Seq.empty(), Seq.empty().rest());
        Assert.assertEquals(Seq.empty(), Seq.of().rest());
        Assert.assertEquals(Seq.empty(), Seq.of().reverse().rest());
    }

    @Test
    public void testIsEmpty() {
        Assert.assertTrue(Seq.empty().isEmpty());
        Assert.assertTrue(Seq.of().isEmpty());
        Assert.assertFalse(Seq.of(A).isEmpty());
    }

    @Test
    public void testNth() {
        Assert.assertEquals(Maybe.none(), Seq.empty().nth(0));
        Assert.assertEquals(Maybe.some(A), Seq.of(A).nth(0));
        Assert.assertEquals(Maybe.none(), Seq.of(A).nth(1));
        Assert.assertEquals(Maybe.some(A), Seq.of(A, B, C).nth(0));
        Assert.assertEquals(Maybe.some(C), Seq.of(A, B, C).nth(2));
        Assert.assertEquals(Maybe.some(C), Seq.of(A, B, C).nth(-1));
        Assert.assertEquals(Maybe.some(A), Seq.of(A, B, C).nth(-3));
        Assert.assertEquals(Maybe.none(), Seq.of(A, B, C).nth(3));
        Assert.assertEquals(Maybe.none(), Seq.of(A, B, C).nth(-4));
        Assert.assertEquals(Maybe.some(C), Seq.of(A, B, C).reverse().nth(0));
    }

    @Test
    public void testCount() {
        Assert.assertEquals(0, Seq.empty().count());
        Assert.assertEquals(1, Seq.of(1).count());
        Assert.assertEquals(3, Seq.of(A, B, C).count());
        Assert.assertEquals(100, Seq.range(100).count());
    }

    @Test
    public void testTake() {
        Assert.assertEquals(Seq.empty(), Seq.empty().take(5));
        Assert.assertEquals(Seq.empty(), Seq.of(A).take(0));
        Assert.assertEquals(Seq.of(A), Seq.of(A).take(1));
        Assert.assertEquals(Seq.of(A, B), Seq.of(A, B, C).take(2));
        Assert.assertEquals(Seq.range(50), Seq.range(100).take(50));
    }

    @Test
    public void testTakeWhile() {
        Assert.assertEquals(Seq.empty(), Seq.of(A).takeWhile(B::equals));
        Assert.assertEquals(Seq.empty(), Seq.of(A, B, C).takeWhile(D::equals));
        Assert.assertEquals(Seq.empty(), Seq.of().takeWhile(A::equals));
        Assert.assertEquals(Seq.empty(), Seq.empty().takeWhile(A::equals));
        Assert.assertEquals(Seq.empty(), Seq.concat(Seq.empty(), Seq.empty()).takeWhile(x -> true));
        Assert.assertEquals(Seq.of(A), Seq.of(A).takeWhile(A::equals));
        Assert.assertEquals(Seq.of(A, A, A), Seq.of(A, A, A, B, C).takeWhile(A::equals));

    }

    @Test
    public void testDrop() {
        Assert.assertEquals(Seq.empty(), Seq.empty().drop(6));
        Assert.assertEquals(Seq.empty(), Seq.of(A).drop(1));
        Assert.assertEquals(Seq.of(A), Seq.of(A).drop(0));
        Assert.assertEquals(Seq.of(C), Seq.of(A, B, C).drop(2));
        Assert.assertEquals(Seq.range(50, 100), Seq.range(100).drop(50));
    }

    @Test
    public void testDropWhile() {
        Assert.assertEquals(Seq.empty(), Seq.of(A).dropWhile(A::equals));
        Assert.assertEquals(Seq.empty(), Seq.of().dropWhile(A::equals));
        Assert.assertEquals(Seq.empty(), Seq.empty().dropWhile(A::equals));
        Assert.assertEquals(Seq.empty(), Seq.concat(Seq.empty(), Seq.empty()).dropWhile(x -> true));
        Assert.assertEquals(Seq.of(A), Seq.of(A).dropWhile(B::equals));
        Assert.assertEquals(Seq.of(B, C), Seq.of(A, A, A, B, C).dropWhile(A::equals));
        Assert.assertEquals(Seq.of(A, B, C), Seq.of(A, B, C).dropWhile(D::equals));
    }

    @Test
    public void testStrict() {
        Assert.assertEquals(Seq.empty(), Seq.empty().strict());
        Assert.assertEquals(Seq.of(A), Seq.of(A).strict());
        Assert.assertEquals(Seq.of(1, 2, 3), Seq.of(1, 2, 3).strict());
        Assert.assertEquals(Seq.range(100), Seq.range(100).strict());
        Assert.assertEquals(Seq.of(A, B, C), Seq.of(C, B, A).reverse().strict());
    }

    @Test
    public void testReverse() {
        Assert.assertEquals(Seq.empty(), Seq.empty().reverse());
        Assert.assertEquals(Seq.of(A), Seq.of(A).reverse());
        Assert.assertEquals(Seq.of(A, B, C), Seq.of(C, B, A).reverse());
        Assert.assertEquals(Seq.of(A, B, C), Seq.of(A, B, C).reverse().reverse());
        Assert.assertEquals(Seq.of(B, A), Seq.concat(Seq.of(A), Seq.of(B)).reverse());
    }

    @Test
    public void testContains() {
        Assert.assertTrue(Seq.of(A, B, C).contains(A));
        Assert.assertFalse(Seq.of(A, B, C).contains(D));
        Assert.assertTrue(Seq.of(A).contains(A));
        Assert.assertFalse(Seq.empty().contains(A));
    }

    @Test
    public void testEach() {
        Ref<Integer> ref = Ref.of(0);
        Seq.range(100).each(n -> {
            Assert.assertEquals(ref.get(), n);
            ref.set(ref.get() + 1);
        });
        Seq.empty().each(panicEff());
    }

    @Test
    public void testAppend() {
        Assert.assertEquals(Seq.of(A, B, C), Seq.of(A, B).append(C));
        Assert.assertEquals(Seq.of(A), Seq.empty().append(A));
    }

    @Test
    public void testPrepend() {
        Assert.assertEquals(Seq.of(A, B, C), Seq.of(B, C).prepend(A));
        Assert.assertEquals(Seq.of(A), Seq.empty().prepend(A));
    }

    @Test
    public void testPush() {
        Assert.assertEquals(Seq.of(A, B, C), Seq.of(B, C).push(A));
        Assert.assertEquals(Seq.of(A), Seq.empty().push(A));
    }

    @Test
    public void testPeek() {
        Assert.assertEquals(Maybe.some(A), Seq.of(A).peek());
        Assert.assertEquals(Maybe.some(A), Seq.of(A, B, C).peek());
        Assert.assertEquals(Maybe.none(), Seq.empty().peek());
    }

    @Test
    public void testPop() {
        Assert.assertEquals(Seq.of(B, C), Seq.of(A, B, C).pop());
        Assert.assertEquals(Seq.empty(), Seq.of(A).pop());
        Assert.assertEquals(Seq.empty(), Seq.empty().pop());
    }

    @Test
    public void testIndex() {
        Assert.assertEquals(Maybe.some(0), Seq.of(A).index(A));
        Assert.assertEquals(Maybe.none(), Seq.<Dummy>of(A).index(B));
        Assert.assertEquals(Maybe.some(0), Seq.of(A, B, C).index(A));
        Assert.assertEquals(Maybe.some(1), Seq.of(A, B, C).index(B));
        Assert.assertEquals(Maybe.none(), Seq.of(A, B, C).index(D));
        Assert.assertEquals(Maybe.none(), Seq.empty().index(A));
    }

    @Test
    public void testIndexBy() {
        Assert.assertEquals(Maybe.some(0), Seq.of(A, B, C).indexBy(A::equals));
        Assert.assertEquals(Maybe.some(1), Seq.of(A, B, C).indexBy(B::equals));
        Assert.assertEquals(Maybe.none(), Seq.of(A, B, C).indexBy(D::equals));
        Assert.assertEquals(Maybe.none(), Seq.empty().indexBy(A::equals));
    }

    @Test
    public void testFind() {
        Assert.assertEquals(Maybe.some(A), Seq.of(A).find(A::equals));
        Assert.assertEquals(Maybe.none(), Seq.of(A).find(B::equals));
        Assert.assertEquals(Maybe.some(A), Seq.of(A, B, C).find(A::equals));
        Assert.assertEquals(Maybe.some(B), Seq.of(A, B, C).find(B::equals));
        Assert.assertEquals(Maybe.none(), Seq.of(A, B, C).find(D::equals));
        Assert.assertEquals(Maybe.none(), Seq.empty().find(A::equals));
    }

    @Test
    public void testMap() {
        Assert.assertEquals(Seq.of(B), Seq.of(A).map(AtoB));
        Assert.assertEquals(Seq.range(0, 10, 2), Seq.range(5).map(n -> n * 2));
        Assert.assertEquals(Seq.empty(), Seq.<A>of().map(AtoB));
        Assert.assertEquals(Seq.empty(), Seq.<A>empty().map(AtoB));
    }

    @Test
    public void testFlatMap() {
        Assert.assertEquals(Seq.of(A, B, C), Seq.of(A).flatMap(x -> Seq.of(A, B, C)));
        Assert.assertEquals(Seq.of(A, A, B, B), Seq.of(A, B).flatMap(d -> Seq.of(d, d)));
        Assert.assertEquals(Seq.empty(), Seq.empty().flatMap(d -> Seq.of(d, d)));
    }

    @Test
    public void testFilter() {
        Assert.assertEquals(Seq.of(A), Seq.of(A).filter(A::equals));
        Assert.assertEquals(Seq.empty(), Seq.of(A).filter(B::equals));
        Assert.assertEquals(Seq.of(A, A), Seq.of(A, B, C, A, D).filter(A::equals));
        Assert.assertEquals(Seq.empty(), Seq.of(A, B, C).filter(D::equals));
        Assert.assertEquals(Seq.empty(), Seq.empty().filter(A::equals));
    }

    @Test
    public void testReduce() {
        Assert.assertEquals(Integer.valueOf(17), Seq.<Integer>empty().reduce(Integer::sum, 17));
        Assert.assertEquals(Integer.valueOf(10), Seq.of(10).reduce(Integer::sum, 0));
        Assert.assertEquals(Integer.valueOf(10), Seq.range(1, 5).reduce(Integer::sum));
        Assert.assertEquals(Integer.valueOf(10), Seq.range(1, 5).reduce(Integer::sum, 0));
        Assert.assertEquals(Integer.valueOf(10), Seq.range(5).reverse().reduce(Integer::sum));
    }

    @Test
    public void testSort() {
        Assert.assertEquals(Seq.empty(), Seq.<Integer>empty().sort(Integer::compareTo));
        Assert.assertEquals(Seq.of(1), Seq.of(1).sortBy(x -> x));
        Assert.assertEquals(Seq.of(1, 2, 3), Seq.of(3, 1, 2).sort(Integer::compareTo));
        Assert.assertEquals(Seq.of(1, 2), Seq.of(2).append(1).sort(Integer::compareTo));
    }

    @Test
    public void testSortBy() {
        Assert.assertEquals(Seq.empty(), Seq.<Integer>empty().sortBy(x -> x));
        Assert.assertEquals(
                Seq.of(Pair.of(A, 1), Pair.of(B, 2), Pair.of(C, 3)),
                Seq.of(Pair.of(B, 2), Pair.of(A, 1), Pair.of(C, 3)).sortBy(Pair::right)
        );
    }

    @Test
    public void testSlice() {
        Assert.assertEquals(Seq.of(B, C), Seq.of(A, B, C, D).slice(1, 3));
    }

    @Test
    public void testEnumerate() {
        Assert.assertEquals(Seq.empty(), Seq.empty().enumerate());
        Assert.assertEquals(
                Seq.of(Pair.of(0, A), Pair.of(1, B), Pair.of(2, C)),
                Seq.of(A, B, C).enumerate()
        );
    }

    @Test
    public void testChunk() {
        Assert.assertEquals(Seq.of(Seq.of(A, B), Seq.of(C)), Seq.of(A, B, C).chunk(2));
        Assert.assertEquals(Seq.of(Seq.of(D, C), Seq.of(B, A)), Seq.of(A, B, C, D).chunk(-2));
        Assert.assertEquals(Seq.empty(), Seq.empty().chunk(7));
        Assert.assertEquals(Seq.empty(), Seq.of().chunk(3));
    }

    @Test
    public void testAny() {
        Assert.assertFalse(Seq.empty().any(x -> true));
        Assert.assertTrue(Seq.of(A, B, C).any(x -> true));
        Assert.assertTrue(Seq.of(A, B, C).any(A::equals));
        Assert.assertFalse(Seq.of(A, B, C).any(x -> false));
    }

    @Test
    public void testAll() {
        Assert.assertTrue(Seq.empty().all(x -> false));
        Assert.assertTrue(Seq.of(A, B, C).all(x -> true));
        Assert.assertFalse(Seq.of(A, B, C).all(A::equals));
        Assert.assertFalse(Seq.of(A, B, C).all(x -> false));
    }

    @Test
    public void testJoin() {
        Assert.assertEquals("", Seq.empty().join());
        Assert.assertEquals("" + A + B + C, Seq.of(A, B, C).join());
        Assert.assertEquals(A + "," + B + "," + C, Seq.of(A, B, C).join(","));
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(Seq.of(A, B, C), Seq.of(A, B, C));
        Assert.assertNotEquals(Seq.of(A, B, C), Seq.empty());
        Assert.assertNotEquals(Seq.empty(), Seq.of(A, B, C));
        Assert.assertNotEquals(Seq.of(A, B, C), Seq.of(A, B));
        Assert.assertNotEquals(Seq.of(A, B), Seq.of(A, B, C));
        Assert.assertNotEquals(Seq.of(A, B, C), Seq.of(A, A, C));
        Assert.assertNotEquals(Seq.of(A, B, C), A);
        Assert.assertNotEquals(Seq.of(A), A);
        Assert.assertNotEquals(Seq.empty(), A);
    }

    @Test
    public void testToString() {
        Assert.assertTrue(Seq.of(A, B).toString().endsWith("(" + A + ", " + B + ")"));
    }

    @Test
    public void testIterator() {
        for (A a : Seq.of(A)) {
            Assert.assertEquals(A, a);
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void testIteratorOnEmpty() {
        Seq.empty().iterator().next();
    }

}
