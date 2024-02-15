package com.github.codeman.nancy.api;


public interface ICacheRemoveListenerContext<K,V> {

    
    K key();

    
    V value();

    
    String type();

}
