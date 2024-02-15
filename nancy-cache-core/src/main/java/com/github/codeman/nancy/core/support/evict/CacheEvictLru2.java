package com.github.codeman.nancy.core.support.evict;

import com.github.codeman.nancy.api.ICache;
import com.github.codeman.nancy.api.ICacheEntry;
import com.github.codeman.nancy.api.ICacheEvictContext;
import com.github.codeman.nancy.core.model.CacheEntry;
import com.github.codeman.nancy.core.support.struct.lru.ILruMap;
import com.github.codeman.nancy.core.support.struct.lru.impl.LruMapDoubleList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class CacheEvictLru2<K,V> extends AbstractCacheEvict<K,V> {

    private static final Logger log = LoggerFactory.getLogger(CacheEvictLru2.class);

    
    private final ILruMap<K,V> firstLruMap;

    
    private final ILruMap<K,V> moreLruMap;

    public CacheEvictLru2() {
        this.firstLruMap = new LruMapDoubleList<>();
        this.moreLruMap = new LruMapDoubleList<>();
    }

    @Override
    protected ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context) {
        ICacheEntry<K, V> result = null;
        final ICache<K,V> cache = context.cache();
        // 超过限制，移除队尾的元素
        if(cache.size() >= context.size()) {
            ICacheEntry<K,V>  evictEntry = null;

            //1. firstLruMap 不为空，优先移除队列中元素
            if(!firstLruMap.isEmpty()) {
                evictEntry = firstLruMap.removeEldest();
                log.debug("从 firstLruMap 中淘汰数据：{}", evictEntry);
            } else {
                //2. 否则从 moreLruMap 中淘汰数据
                evictEntry = moreLruMap.removeEldest();
                log.debug("从 moreLruMap 中淘汰数据：{}", evictEntry);
            }

            // 执行缓存移除操作
            final K evictKey = evictEntry.key();
            V evictValue = cache.remove(evictKey);
            result = new CacheEntry<>(evictKey, evictValue);
        }

        return result;
    }


    
    @Override
    public void updateKey(final K key) {
        //1. 元素已经在多次访问，或者第一次访问的 lru 中
        if(moreLruMap.contains(key)
            || firstLruMap.contains(key)) {
            //1.1 删除信息
            this.removeKey(key);

            //1.2 加入到多次 LRU 中
            moreLruMap.updateKey(key);
            log.debug("key: {} 多次访问，加入到 moreLruMap 中", key);
        } else {
            // 2. 加入到第一次访问 LRU 中
            firstLruMap.updateKey(key);
            log.debug("key: {} 为第一次访问，加入到 firstLruMap 中", key);
        }
    }

    
    @Override
    public void removeKey(final K key) {
        //1. 多次LRU 删除逻辑
        if(moreLruMap.contains(key)) {
            moreLruMap.removeKey(key);
            log.debug("key: {} 从 moreLruMap 中移除", key);
        } else {
            firstLruMap.removeKey(key);
            log.debug("key: {} 从 firstLruMap 中移除", key);
        }
    }

}
