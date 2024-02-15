package com.github.codeman.nancy.core.support.load;


import com.github.codeman.nancy.api.ICacheLoad;

public final class CacheLoads {

    private CacheLoads(){}

    
    public static <K,V> ICacheLoad<K,V> none() {
        return new CacheLoadNone<>();
    }

    
    public static <K,V> ICacheLoad<K,V> dbJson(final String dbPath) {
        return new CacheLoadDbJson<>(dbPath);
    }

    
    public static <K,V> ICacheLoad<K,V> aof(final String dbPath) {
        return new CacheLoadAof<>(dbPath);
    }

}
