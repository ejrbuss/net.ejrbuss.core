package net.ejrbuss.core.data;

import java.util.Iterator;

public interface Seq<V> {

    Maybe<V> first();

    Seq<V> rest();

}
