package com.github.codeman.nancy.core.support.evict;


import com.github.codeman.nancy.api.ICache;
import com.github.codeman.nancy.api.ICacheEvictContext;

public class CacheEvictContext<K,V> implements ICacheEvictContext<K,V> {

    
    private K key;

    
    private ICache<K,V> cache;

    
    private int size;

    @Override
    public K key() {
        return key;
    }

    public CacheEvictContext<K, V> key(K key) {
        this.key = key;
        return this;
    }

    @Override
    public ICache<K, V> cache() {
        return cache;
    }

    public CacheEvictContext<K, V> cache(ICache<K, V> cache) {
        this.cache = cache;
        return this;
    }

    @Override
    public int size() {
        return size;
    }

    public CacheEvictContext<K, V> size(int size) {
        this.size = size;
        return this;
    }
}
