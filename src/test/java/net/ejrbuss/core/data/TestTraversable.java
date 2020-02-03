package net.ejrbuss.core.data;

import static net.ejrbuss.core.test.Dummy.*;

import org.junit.Assert;
import org.junit.Test;

public class TestTraversable {

    @Test
    public void testIterator() {
        int i = 0;
        for (A a : new MiniTraversable(144)) {
            Assert.assertEquals(A, a);
            i++;
        }
        for (A a : new MiniTraversable(144)) {
            Assert.assertEquals(A, a);
            i++;
        }
        Assert.assertEquals(288, i);
    }

    static class MiniTraversable implements Traversable<A> {

        private final int count;

        public MiniTraversable(int count) {
            this.count = count;
        }

        @Override
        public Maybe<A> first() {
            return Maybe.given(count > 0, A);
        }

        @Override
        public Traversable<A> rest() {
            return new MiniTraversable(count - 1);
        }
    }

}
