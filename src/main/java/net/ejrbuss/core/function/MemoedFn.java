package net.ejrbuss.core.function;

import java.util.HashMap;
import java.util.Map;

public class MemoedFn<A1, R> implements Fn<A1, R> {

    public static <A, B> MemoedFn<A, B> of(Fn<A, B> impl) {
        return new MemoedFn<>(impl);
    }

    private final Map<A1, R> memo;
    private final Fn<A1, R> impl;

    private MemoedFn(Fn<A1, R> impl) {
        this.memo = new HashMap<A1, R>();
        this.impl = impl;
    }

    @Override
    public R apply(A1 arg) {
        if (memo.containsKey(arg)) {
            return memo.get(arg);
        } else {
            R value = impl.apply(arg);
            memo.put(arg, value);
            return value;
        }
    }

}
