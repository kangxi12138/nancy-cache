package com.github.codeman.nancy.core.support.evict;



import com.github.codeman.nancy.api.ICache;
import com.github.codeman.nancy.api.ICacheEvictContext;
import com.github.codeman.nancy.core.model.CacheEntry;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;


public class CacheEvictFifo<K,V> extends AbstractCacheEvict<K,V> {

    
    private final Queue<K> queue =  new ConcurrentLinkedDeque<>();

    @Override
    public CacheEntry<K,V> doEvict(ICacheEvictContext<K, V> context) {
        CacheEntry<K,V> result = null;

        final ICache<K,V> cache = context.cache();
        // 超过限制，执行移除
        if(cache.size() >= context.size()) {
            K evictKey = queue.remove();
            // 移除最开始的元素
            V evictValue = cache.remove(evictKey);
            result = new CacheEntry<>(evictKey, evictValue);
        }

        // 将新加的元素放入队尾
        final K key = context.key();
        queue.add(key);

        return result;
    }

}
