package net.ejrbuss.core.test;

import net.ejrbuss.core.function.Eff;
import net.ejrbuss.core.function.Fn;
import net.ejrbuss.core.function.Thunk;
import net.ejrbuss.core.function.ThunkEff;

public abstract class Dummy {

    public int variation = 0;

    public Dummy() {}

    public Dummy(int variation) {
        this.variation = variation;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Dummy) {
            Dummy otherDummy = (Dummy) other;
            return otherDummy.getClass().getSimpleName().equals(getClass().getSimpleName())
                    && otherDummy.variation == variation;
        }
        return false;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + variation + ")";
    }

    public static class A extends Dummy {}
    public static class B extends Dummy {}
    public static class C extends Dummy {}
    public static class D extends Dummy {}

    public static A A = new A();
    public static B B = new B();
    public static C C = new C();
    public static D D = new D();

    public static A A(int variation) {
        A a = new A();
        a.variation = variation;
        return a;
    }

    public static class DummyFn<A, B> implements Fn<A, B> {

        public int count = 0;
        public A lastCall;
        public B value;

        public DummyFn(B value) {
            this.value = value;
        }

        @Override
        public B apply(A arg) {
            lastCall = arg;
            count++;
            return value;
        }

    }

    public static DummyFn<A, B> AtoB = new DummyFn<>(B);
    public static DummyFn<B, C> BtoC = new DummyFn<>(C);
    public static DummyFn<C, D> CtoD = new DummyFn<>(D);

    public static <A, B> Fn<A, B> panicFn() {
        return a -> { throw new RuntimeException("Panic!"); };
    }

    public static <A> Eff<A> panicEff() {
        return a -> { throw new RuntimeException("Panic!"); };
    }

    public static <A> Thunk<A> panicThunk() {
        return () -> { throw new RuntimeException("Panic!"); };
    }

    public static ThunkEff panicThunkEff() {
        return () -> { throw new RuntimeException("Panic!"); };
    }

}
