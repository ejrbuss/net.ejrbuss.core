package net.ejrbuss.core.data;

import static net.ejrbuss.core.test.Dummy.*;

import net.ejrbuss.core.test.Dummy;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class TestList {

    @Test
    public void testFrom() {
        Assert.assertEquals(List.of(A), List.from(Maybe.some(A)));
        Assert.assertEquals(List.empty(), List.from(Maybe.none()));
        Assert.assertEquals(List.of(A, B, C), List.from(Arrays.asList(A, B, C)));
    }

    @Test
    public void testRepeat() {
        Assert.assertEquals(List.of(A, A, A, A), List.repeat(A).take(4));
    }

    @Test
    public void testIterate() {
        Assert.assertEquals(List.of(A, A, A, A), List.iterate(() -> A).take(4));
        Assert.assertEquals(List.range(100), List.iterate(n -> n + 1, 0).take(100));
    }

    @Test
    public void testRange() {
        Assert.assertEquals(List.of(0, 1, 2), List.range(3));
        Assert.assertEquals(List.of(2, 3, 4), List.range(2, 5));
        Assert.assertEquals(List.of(5, 4, 3, 2, 1), List.range(5, 0));
        Assert.assertEquals(List.of(2, 4, 6), List.range(2, 8, 2));
        Assert.assertEquals(List.of(0.0, 0.5, 1.0, 1.5), List.range(0.0, 2.0, 0.5));
        Assert.assertEquals(List.of(2.0, 1.5, 1.0, 0.5), List.range(2.0, 0.0, -0.5));
    }

    @Test
    public void testZip() {
        Assert.assertEquals(List.empty(), List.zip(List.empty(), List.of(A, B, C)));
        Assert.assertEquals(List.empty(), List.zip(List.of(A, B, C), List.empty()));
        Assert.assertEquals(List.of(A, B, C).enumerate(), List.zip(
                List.range(3),
                List.of(A, B, C)
        ));
        Assert.assertEquals(List.of(0, 2, 4), List.zipWith(
                Integer::sum,
                List.range(3),
                List.range(3)
        ));
    }

    @Test
    public void testConcat() {
        Assert.assertEquals(List.of(A, B, C, D), List.concat(List.of(A, B), List.of(C, D)));
    }

    @Test
    public void testFirst() {
        Assert.assertEquals(Maybe.some(A), List.of(A).first());
        Assert.assertEquals(Maybe.some(A), List.of(A, B, C).first());
        Assert.assertEquals(Maybe.none(), List.empty().first());
        Assert.assertEquals(Maybe.none(), List.of().first());
    }

    @Test
    public void testLast() {
        Assert.assertEquals(Maybe.none(), List.empty().last());
        Assert.assertEquals(Maybe.none(), List.of().last());
        Assert.assertEquals(Maybe.none(), List.of().reverse().last());
        Assert.assertEquals(Maybe.some(A), List.of(A).last());
        Assert.assertEquals(Maybe.some(A), List.concat(List.of(B), List.of(A)).last());
        Assert.assertEquals(Maybe.some(C), List.of(A, B, C).last());
    }

    @Test
    public void testRest() {
        Assert.assertEquals(List.of(B, C), List.of(A, B, C).rest());
        Assert.assertEquals(List.empty(), List.of(A).rest());
        Assert.assertEquals(List.empty(), List.empty().rest());
        Assert.assertEquals(List.empty(), List.of().rest());
        Assert.assertEquals(List.empty(), List.of().reverse().rest());
    }

    @Test
    public void testIsEmpty() {
        Assert.assertTrue(List.empty().isEmpty());
        Assert.assertTrue(List.of().isEmpty());
        Assert.assertFalse(List.of(A).isEmpty());
    }

    @Test
    public void testNth() {
        Assert.assertEquals(Maybe.none(), List.empty().nth(0));
        Assert.assertEquals(Maybe.some(A), List.of(A).nth(0));
        Assert.assertEquals(Maybe.none(), List.of(A).nth(1));
        Assert.assertEquals(Maybe.some(A), List.of(A, B, C).nth(0));
        Assert.assertEquals(Maybe.some(C), List.of(A, B, C).nth(2));
        Assert.assertEquals(Maybe.some(C), List.of(A, B, C).nth(-1));
        Assert.assertEquals(Maybe.some(A), List.of(A, B, C).nth(-3));
        Assert.assertEquals(Maybe.none(), List.of(A, B, C).nth(3));
        Assert.assertEquals(Maybe.none(), List.of(A, B, C).nth(-4));
        Assert.assertEquals(Maybe.some(C), List.of(A, B, C).reverse().nth(0));
    }

    @Test
    public void testCount() {
        Assert.assertEquals(0, List.empty().count());
        Assert.assertEquals(1, List.of(1).count());
        Assert.assertEquals(3, List.of(A, B, C).count());
        Assert.assertEquals(100, List.range(100).count());
    }

    @Test
    public void testTake() {
        Assert.assertEquals(List.empty(), List.empty().take(5));
        Assert.assertEquals(List.empty(), List.of(A).take(0));
        Assert.assertEquals(List.of(A), List.of(A).take(1));
        Assert.assertEquals(List.of(A, B), List.of(A, B, C).take(2));
        Assert.assertEquals(List.range(50), List.range(100).take(50));
    }

    @Test
    public void testTakeWhile() {
        Assert.assertEquals(List.empty(), List.of(A).takeWhile(B::equals));
        Assert.assertEquals(List.empty(), List.of(A, B, C).takeWhile(D::equals));
        Assert.assertEquals(List.empty(), List.of().takeWhile(A::equals));
        Assert.assertEquals(List.empty(), List.empty().takeWhile(A::equals));
        Assert.assertEquals(List.empty(), List.concat(List.empty(), List.empty()).takeWhile(x -> true));
        Assert.assertEquals(List.of(A), List.of(A).takeWhile(A::equals));
        Assert.assertEquals(List.of(A, A, A), List.of(A, A, A, B, C).takeWhile(A::equals));

    }

    @Test
    public void testDrop() {
        Assert.assertEquals(List.empty(), List.empty().drop(6));
        Assert.assertEquals(List.empty(), List.of(A).drop(1));
        Assert.assertEquals(List.of(A), List.of(A).drop(0));
        Assert.assertEquals(List.of(C), List.of(A, B, C).drop(2));
        Assert.assertEquals(List.range(50, 100), List.range(100).drop(50));
    }

    @Test
    public void testDropWhile() {
        Assert.assertEquals(List.empty(), List.of(A).dropWhile(A::equals));
        Assert.assertEquals(List.empty(), List.of().dropWhile(A::equals));
        Assert.assertEquals(List.empty(), List.empty().dropWhile(A::equals));
        Assert.assertEquals(List.empty(), List.concat(List.empty(), List.empty()).dropWhile(x -> true));
        Assert.assertEquals(List.of(A), List.of(A).dropWhile(B::equals));
        Assert.assertEquals(List.of(B, C), List.of(A, A, A, B, C).dropWhile(A::equals));
        Assert.assertEquals(List.of(A, B, C), List.of(A, B, C).dropWhile(D::equals));
    }

    @Test
    public void testStrict() {
        Assert.assertEquals(List.empty(), List.empty().strict());
        Assert.assertEquals(List.of(A), List.of(A).strict());
        Assert.assertEquals(List.of(1, 2, 3), List.of(1, 2, 3).strict());
        Assert.assertEquals(List.range(100), List.range(100).strict());
        Assert.assertEquals(List.of(A, B, C), List.of(C, B, A).reverse().strict());
    }

    @Test
    public void testReverse() {
        Assert.assertEquals(List.empty(), List.empty().reverse());
        Assert.assertEquals(List.of(A), List.of(A).reverse());
        Assert.assertEquals(List.of(A, B, C), List.of(C, B, A).reverse());
        Assert.assertEquals(List.of(A, B, C), List.of(A, B, C).reverse().reverse());
        Assert.assertEquals(List.of(B, A), List.concat(List.of(A), List.of(B)).reverse());
    }

    @Test
    public void testContains() {
        Assert.assertTrue(List.of(A, B, C).contains(A));
        Assert.assertFalse(List.of(A, B, C).contains(D));
        Assert.assertTrue(List.of(A).contains(A));
        Assert.assertFalse(List.empty().contains(A));
    }

    @Test
    public void testEach() {
        Ref<Integer> ref = Ref.of(0);
        List.range(100).each(n -> {
            Assert.assertEquals(ref.get(), n);
            ref.set(ref.get() + 1);
        });
        List.empty().each(panicEff());
    }

    @Test
    public void testAppend() {
        Assert.assertEquals(List.of(A, B, C), List.of(A, B).append(C));
        Assert.assertEquals(List.of(A), List.empty().append(A));
    }

    @Test
    public void testPrepend() {
        Assert.assertEquals(List.of(A, B, C), List.of(B, C).prepend(A));
        Assert.assertEquals(List.of(A), List.empty().prepend(A));
    }

    @Test
    public void testPush() {
        Assert.assertEquals(List.of(A, B, C), List.of(B, C).push(A));
        Assert.assertEquals(List.of(A), List.empty().push(A));
    }

    @Test
    public void testPeek() {
        Assert.assertEquals(Maybe.some(A), List.of(A).peek());
        Assert.assertEquals(Maybe.some(A), List.of(A, B, C).peek());
        Assert.assertEquals(Maybe.none(), List.empty().peek());
    }

    @Test
    public void testPop() {
        Assert.assertEquals(List.of(B, C), List.of(A, B, C).pop());
        Assert.assertEquals(List.empty(), List.of(A).pop());
        Assert.assertEquals(List.empty(), List.empty().pop());
    }

    @Test
    public void testIndex() {
        Assert.assertEquals(Maybe.some(0), List.of(A).index(A));
        Assert.assertEquals(Maybe.none(), List.<Dummy>of(A).index(B));
        Assert.assertEquals(Maybe.some(0), List.of(A, B, C).index(A));
        Assert.assertEquals(Maybe.some(1), List.of(A, B, C).index(B));
        Assert.assertEquals(Maybe.none(), List.of(A, B, C).index(D));
        Assert.assertEquals(Maybe.none(), List.empty().index(A));
    }

    @Test
    public void testIndexBy() {
        Assert.assertEquals(Maybe.some(0), List.of(A, B, C).indexBy(A::equals));
        Assert.assertEquals(Maybe.some(1), List.of(A, B, C).indexBy(B::equals));
        Assert.assertEquals(Maybe.none(), List.of(A, B, C).indexBy(D::equals));
        Assert.assertEquals(Maybe.none(), List.empty().indexBy(A::equals));
    }

    @Test
    public void testFind() {
        Assert.assertEquals(Maybe.some(A), List.of(A).find(A::equals));
        Assert.assertEquals(Maybe.none(), List.of(A).find(B::equals));
        Assert.assertEquals(Maybe.some(A), List.of(A, B, C).find(A::equals));
        Assert.assertEquals(Maybe.some(B), List.of(A, B, C).find(B::equals));
        Assert.assertEquals(Maybe.none(), List.of(A, B, C).find(D::equals));
        Assert.assertEquals(Maybe.none(), List.empty().find(A::equals));
    }

    @Test
    public void testMap() {
        Assert.assertEquals(List.of(B), List.of(A).map(AtoB));
        Assert.assertEquals(List.range(0, 10, 2), List.range(5).map(n -> n * 2));
        Assert.assertEquals(List.empty(), List.<A>of().map(AtoB));
        Assert.assertEquals(List.empty(), List.<A>empty().map(AtoB));
    }

    @Test
    public void testFlatMap() {
        Assert.assertEquals(List.of(A, B, C), List.of(A).flatMap(x -> List.of(A, B, C)));
        Assert.assertEquals(List.of(A, A, B, B), List.of(A, B).flatMap(d -> List.of(d, d)));
        Assert.assertEquals(List.empty(), List.empty().flatMap(d -> List.of(d, d)));
    }

    @Test
    public void testFilter() {
        Assert.assertEquals(List.of(A), List.of(A).filter(A::equals));
        Assert.assertEquals(List.empty(), List.of(A).filter(B::equals));
        Assert.assertEquals(List.of(A, A), List.of(A, B, C, A, D).filter(A::equals));
        Assert.assertEquals(List.empty(), List.of(A, B, C).filter(D::equals));
        Assert.assertEquals(List.empty(), List.empty().filter(A::equals));
    }

    @Test
    public void testReduce() {
        Assert.assertEquals(Integer.valueOf(17), List.<Integer>empty().reduce(Integer::sum, 17));
        Assert.assertEquals(Integer.valueOf(10), List.of(10).reduce(Integer::sum, 0));
        Assert.assertEquals(Integer.valueOf(10), List.range(1, 5).reduce(Integer::sum));
        Assert.assertEquals(Integer.valueOf(10), List.range(1, 5).reduce(Integer::sum, 0));
        Assert.assertEquals(Integer.valueOf(10), List.range(5).reverse().reduce(Integer::sum));
    }

    @Test
    public void testSort() {
        Assert.assertEquals(List.empty(), List.<Integer>empty().sort(Integer::compareTo));
        Assert.assertEquals(List.of(1), List.of(1).sortBy(x -> x));
        Assert.assertEquals(List.of(1, 2, 3), List.of(3, 1, 2).sort(Integer::compareTo));
        Assert.assertEquals(List.of(1, 2), List.of(2).append(1).sort(Integer::compareTo));
    }

    @Test
    public void testSortBy() {
        Assert.assertEquals(List.empty(), List.<Integer>empty().sortBy(x -> x));
        Assert.assertEquals(
                List.of(Pair.of(A, 1), Pair.of(B, 2), Pair.of(C, 3)),
                List.of(Pair.of(B, 2), Pair.of(A, 1), Pair.of(C, 3)).sortBy(Pair::right)
        );
    }

    @Test
    public void testSlice() {
        Assert.assertEquals(List.of(B, C), List.of(A, B, C, D).slice(1, 3));
    }

    @Test
    public void testEnumerate() {
        Assert.assertEquals(List.empty(), List.empty().enumerate());
        Assert.assertEquals(
                List.of(Pair.of(0, A), Pair.of(1, B), Pair.of(2, C)),
                List.of(A, B, C).enumerate()
        );
    }

    @Test
    public void testChunk() {
        Assert.assertEquals(List.of(List.of(A, B), List.of(C)), List.of(A, B, C).chunk(2));
        Assert.assertEquals(List.of(List.of(D, C), List.of(B, A)), List.of(A, B, C, D).chunk(-2));
        Assert.assertEquals(List.empty(), List.empty().chunk(7));
        Assert.assertEquals(List.empty(), List.of().chunk(3));
    }

    @Test
    public void testAny() {
        Assert.assertFalse(List.empty().any(x -> true));
        Assert.assertTrue(List.of(A, B, C).any(x -> true));
        Assert.assertTrue(List.of(A, B, C).any(A::equals));
        Assert.assertFalse(List.of(A, B, C).any(x -> false));
    }

    @Test
    public void testAll() {
        Assert.assertTrue(List.empty().all(x -> false));
        Assert.assertTrue(List.of(A, B, C).all(x -> true));
        Assert.assertFalse(List.of(A, B, C).all(A::equals));
        Assert.assertFalse(List.of(A, B, C).all(x -> false));
    }

    @Test
    public void testJoin() {
        Assert.assertEquals("", List.empty().join());
        Assert.assertEquals("" + A + B + C, List.of(A, B, C).join());
        Assert.assertEquals(A + "," + B + "," + C, List.of(A, B, C).join(","));
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(List.of(A, B, C), List.of(A, B, C));
        Assert.assertNotEquals(List.of(A, B, C), List.empty());
        Assert.assertNotEquals(List.empty(), List.of(A, B, C));
        Assert.assertNotEquals(List.of(A, B, C), List.of(A, B));
        Assert.assertNotEquals(List.of(A, B), List.of(A, B, C));
        Assert.assertNotEquals(List.of(A, B, C), List.of(A, A, C));
        Assert.assertNotEquals(List.of(A, B, C), A);
        Assert.assertNotEquals(List.of(A), A);
        Assert.assertNotEquals(List.empty(), A);
    }

    @Test
    public void testToString() {
        Assert.assertTrue(List.of(A, B).toString().endsWith("(" + A + ", " + B + ")"));
    }

    @Test
    public void testIterator() {
        for (A a : List.of(A)) {
            Assert.assertEquals(A, a);
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void testIteratorOnEmpty() {
        List.empty().iterator().next();
    }

}
