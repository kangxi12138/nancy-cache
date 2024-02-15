package com.github.codeman.nancy.api;

import java.util.Map;


public interface ICacheContext<K, V> {

    
    Map<K, V> map();

    
    int size();

    
    ICacheEvict<K,V> cacheEvict();

}
