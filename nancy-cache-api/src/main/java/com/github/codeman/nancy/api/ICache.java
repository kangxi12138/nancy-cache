package com.github.codeman.nancy.api;

import java.util.List;
import java.util.Map;


public interface ICache<K, V> extends Map<K, V> {

    ICache<K, V> expire(final K key, final long timeInMills);


    ICache<K, V> expireAt(final K key, final long timeInMills);


    ICacheExpire<K,V> expire();


    List<ICacheRemoveListener<K,V>> removeListeners();

    List<ICacheSlowListener> slowListeners();


    ICacheLoad<K,V> load();


    ICachePersist<K,V> persist();


    ICacheEvict<K,V> evict();

}
