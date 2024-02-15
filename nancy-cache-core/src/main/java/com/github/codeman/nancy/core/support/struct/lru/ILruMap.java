package com.github.codeman.nancy.core.support.struct.lru;


import com.github.codeman.nancy.api.ICacheEntry;

public interface ILruMap<K,V> {

    
    ICacheEntry<K, V> removeEldest();

    
    void updateKey(final K key);

    
    void removeKey(final K key);

    
    boolean isEmpty();

    
    boolean contains(final K key);

}
