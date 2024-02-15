package com.github.codeman.nancy.core.model;


import com.github.codeman.nancy.api.ICacheEntry;

public class CacheEntry<K,V> implements ICacheEntry<K,V> {

    
    private final K key;

    
    private final V value;

    
    public static <K,V> CacheEntry<K,V> of(final K key,
                                           final V value) {
        return new CacheEntry<>(key, value);
    }

    public CacheEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K key() {
        return key;
    }

    @Override
    public V value() {
        return value;
    }

    @Override
    public String toString() {
        return "EvictEntry{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }

}
