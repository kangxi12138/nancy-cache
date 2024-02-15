package com.github.codeman.nancy.core.support.evict;


import com.github.codeman.nancy.api.ICacheEntry;
import com.github.codeman.nancy.api.ICacheEvictContext;


public class CacheEvictNone<K,V> extends AbstractCacheEvict<K,V> {

    @Override
    protected ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context) {
        return null;
    }

}
