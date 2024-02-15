package com.github.codeman.nancy.api;


public interface ICacheEvict<K, V> {

    
    ICacheEntry<K,V> evict(final ICacheEvictContext<K, V> context);

    
    void updateKey(final K key);

    
    void removeKey(final K key);

}
