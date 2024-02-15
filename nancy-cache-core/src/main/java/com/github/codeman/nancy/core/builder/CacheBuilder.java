package com.github.codeman.nancy.core.builder;



import com.github.codeman.nancy.api.*;
import com.github.codeman.nancy.core.core.Cache;
import com.github.codeman.nancy.core.support.evict.CacheEvicts;
import com.github.codeman.nancy.core.support.expire.CacheExpire;
import com.github.codeman.nancy.core.support.listener.remove.CacheRemoveListeners;
import com.github.codeman.nancy.core.support.listener.slow.CacheSlowListeners;
import com.github.codeman.nancy.core.support.load.CacheLoads;
import com.github.codeman.nancy.core.support.persist.CachePersists;
import com.github.codeman.nancy.core.support.proxy.CacheProxy;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class CacheBuilder<K,V> {

    private CacheBuilder(){}

    
    public static <K,V> CacheBuilder<K,V> newBuilder() {
        return new CacheBuilder<>();
    }

    

    
    private int size = Integer.MAX_VALUE;

    
    private ICacheEvict<K,V> evict = CacheEvicts.fifo();
    
    private final List<ICacheRemoveListener<K,V>> removeListeners = CacheRemoveListeners.defaults();

    
    private final List<ICacheSlowListener> slowListeners = CacheSlowListeners.none();

    
    private ICacheLoad<K,V> load = CacheLoads.none();

    
    private ICachePersist<K,V> persist = CachePersists.none();

    private ICacheExpire<K,V> expire=new CacheExpire<>();
    

    
    public CacheBuilder<K, V> size(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size must be >= 0!");
        }
        this.size = size;
        return this;
    }

    
    public CacheBuilder<K, V> evict(ICacheEvict<K, V> evict) {
        if (null == evict) {
            throw new IllegalArgumentException("evict can not be null!");
        }
        this.evict = evict;
        return this;
    }

    
    public CacheBuilder<K, V> load(ICacheLoad<K, V> load) {
        if (null == load) {
            throw new IllegalArgumentException("load can not be null!");
        }
        this.load = load;
        return this;
    }

    
    public CacheBuilder<K, V> addRemoveListener(ICacheRemoveListener<K,V> removeListener) {
        if (null == removeListener) {
            throw new IllegalArgumentException("removeListener can not be null!");
        }
        this.removeListeners.add(removeListener);
        return this;
    }

    
    public CacheBuilder<K, V> addSlowListener(ICacheSlowListener slowListener) {
        if(null == slowListener){
            throw new IllegalArgumentException("slowListener can not be null!");
        }
        this.slowListeners.add(slowListener);
        return this;
    }

    
    public CacheBuilder<K, V> persist(ICachePersist<K, V> persist) {
        this.persist = persist;
        return this;
    }

    public CacheBuilder<K,V> expire(ICacheExpire<K,V> expire){
        this.expire=expire;
        return this;
    }
    public ICache<K,V> build() {
        Cache<K,V> cache = new Cache<>();
        cache.evict(evict);
        cache.sizeLimit(size);
        cache.removeListeners(removeListeners);
        cache.load(load);
        cache.persist(persist);
        cache.slowListeners(slowListeners);
        cache.expire(expire);
        // 初始化
        cache.init();
        return CacheProxy.getProxy(cache);
    }

}
