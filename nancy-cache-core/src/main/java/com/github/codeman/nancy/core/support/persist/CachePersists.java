package com.github.codeman.nancy.core.support.persist;


import com.github.codeman.nancy.api.ICachePersist;

public final class CachePersists {

    private CachePersists(){}

    
    public static <K,V> ICachePersist<K,V> none() {
        return new CachePersistNone<>();
    }

    
    public static <K,V> ICachePersist<K,V> dbJson(final String path) {
        return new CachePersistDbJson<>(path);
    }

    
    public static <K,V> ICachePersist<K,V> aof(final String path) {
        return new CachePersistAof<>(path);
    }

}
