package com.github.codeman.nancy.core.support.evict;


import com.github.codeman.nancy.api.ICache;
import com.github.codeman.nancy.api.ICacheEntry;
import com.github.codeman.nancy.api.ICacheEvictContext;
import com.github.codeman.nancy.core.model.CacheEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.LinkedList;
import java.util.List;

public class CacheEvictLru<K,V> extends AbstractCacheEvict<K,V> {

    private static final Logger log = LoggerFactory.getLogger(CacheEvictLru.class);

    
    private final List<K> list = new LinkedList<>();

    @Override
    protected ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context) {
        ICacheEntry<K, V> result = null;
        final ICache<K,V> cache = context.cache();
        // 超过限制，移除队尾的元素
        if(cache.size() >= context.size()) {
            K evictKey = list.get(list.size()-1);
            V evictValue = cache.remove(evictKey);
            result = new CacheEntry<>(evictKey, evictValue);
        }

        return result;
    }


    
    @Override
    public void updateKey(final K key) {
        this.list.remove(key);
        this.list.add(0, key);
    }

    
    @Override
    public void removeKey(final K key) {
        this.list.remove(key);
    }

}
