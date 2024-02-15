package com.github.codeman.nancy.api;


public interface ICacheEvictContext<K,V> {

    
    K key();

    
    ICache<K, V> cache();

    
    int size();

}
