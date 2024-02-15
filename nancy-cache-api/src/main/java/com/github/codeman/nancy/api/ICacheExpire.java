package com.github.codeman.nancy.api;

import java.util.Collection;


public interface ICacheExpire<K,V> {

    void expire(final K key, final long expireAt);

    void refreshExpire(final Collection<K> keyList);

    Long expireTime(final K key);

    void init(ICache<K,V> cache);


}
