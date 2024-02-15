package com.github.codeman.nancy.core.support.evict;


import com.github.codeman.nancy.api.ICache;
import com.github.codeman.nancy.api.ICacheEntry;
import com.github.codeman.nancy.api.ICacheEvictContext;
import com.github.codeman.nancy.core.model.CacheEntry;
import com.github.codeman.nancy.core.support.struct.lru.ILruMap;
import com.github.codeman.nancy.core.support.struct.lru.impl.LruMapCircleList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class CacheEvictClock<K,V> extends AbstractCacheEvict<K,V> {

    private static final Logger log = LoggerFactory.getLogger(CacheEvictClock.class);

    
    private final ILruMap<K,V> circleList;

    public CacheEvictClock() {
        this.circleList = new LruMapCircleList<>();
    }

    @Override
    protected ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context) {
        ICacheEntry<K, V> result = null;
        final ICache<K,V> cache = context.cache();
        // 超过限制，移除队尾的元素
        if(cache.size() >= context.size()) {
            ICacheEntry<K,V>  evictEntry = circleList.removeEldest();;
            // 执行缓存移除操作
            final K evictKey = evictEntry.key();
            V evictValue = cache.remove(evictKey);

            log.debug("基于 clock 算法淘汰 key：{}, value: {}", evictKey, evictValue);
            result = new CacheEntry<>(evictKey, evictValue);
        }

        return result;
    }


    
    @Override
    public void updateKey(final K key) {
        this.circleList.updateKey(key);
    }

    
    @Override
    public void removeKey(final K key) {
        this.circleList.removeKey(key);
    }

}
