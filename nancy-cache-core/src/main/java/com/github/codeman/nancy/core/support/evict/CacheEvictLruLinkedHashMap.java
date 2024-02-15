package com.github.codeman.nancy.core.support.evict;


import com.github.codeman.nancy.api.ICache;
import com.github.codeman.nancy.api.ICacheEntry;
import com.github.codeman.nancy.api.ICacheEvict;
import com.github.codeman.nancy.api.ICacheEvictContext;
import com.github.codeman.nancy.core.model.CacheEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.LinkedHashMap;
import java.util.Map;

public class CacheEvictLruLinkedHashMap<K,V> extends LinkedHashMap<K,V>
    implements ICacheEvict<K,V> {

    private static final Logger log = LoggerFactory.getLogger(CacheEvictLruDoubleListMap.class);

    
    private volatile boolean removeFlag = false;

    
    private transient Map.Entry<K, V> eldest = null;

    public CacheEvictLruLinkedHashMap() {
        super(16, 0.75f, true);
    }

    @Override
    public ICacheEntry<K, V> evict(ICacheEvictContext<K, V> context) {
        ICacheEntry<K, V> result = null;
        final ICache<K,V> cache = context.cache();
        // 超过限制，移除队尾的元素
        if(cache.size() >= context.size()) {
            removeFlag = true;

            // 执行 put 操作
            super.put(context.key(), null);

            // 构建淘汰的元素
            K evictKey = eldest.getKey();
            V evictValue = cache.remove(evictKey);
            result = new CacheEntry<>(evictKey, evictValue);
        } else {
            removeFlag = false;
        }

        return result;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        this.eldest = eldest;
        return removeFlag;
    }

    @Override
    public void updateKey(K key) {
        super.put(key, null);
    }

    @Override
    public void removeKey(K key) {
        super.remove(key);
    }

}
