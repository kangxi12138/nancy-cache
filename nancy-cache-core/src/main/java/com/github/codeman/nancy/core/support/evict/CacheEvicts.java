package com.github.codeman.nancy.core.support.evict;


import com.github.codeman.nancy.api.ICacheEvict;


public final class CacheEvicts {

    private CacheEvicts(){}

    
    public static <K, V> ICacheEvict<K, V> none() {
        return new CacheEvictNone<>();
    }

    
    public static <K, V> ICacheEvict<K, V> fifo() {
        return new CacheEvictFifo<>();
    }

    
    public static <K, V> ICacheEvict<K, V> lru() {
        return new CacheEvictLru<>();
    }

    
    public static <K, V> ICacheEvict<K, V> lruDoubleListMap() {
        return new CacheEvictLruDoubleListMap<>();
    }


    
    public static <K, V> ICacheEvict<K, V> lruLinkedHashMap() {
        return new CacheEvictLruLinkedHashMap<>();
    }

    
    public static <K, V> ICacheEvict<K, V> lru2Q() {
        return new CacheEvictLru2Q<>();
    }

    
    public static <K, V> ICacheEvict<K, V> lru2() {
        return new CacheEvictLru2<>();
    }

    
    public static <K, V> ICacheEvict<K, V> lfu() {
        return new CacheEvictLfu<>();
    }

    
    public static <K, V> ICacheEvict<K, V> clock() {
        return new CacheEvictClock<>();
    }

}
