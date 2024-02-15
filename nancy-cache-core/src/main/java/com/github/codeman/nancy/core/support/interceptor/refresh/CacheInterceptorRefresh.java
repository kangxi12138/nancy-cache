package com.github.codeman.nancy.core.support.interceptor.refresh;


import com.github.codeman.nancy.api.ICache;
import com.github.codeman.nancy.api.ICacheInterceptor;
import com.github.codeman.nancy.api.ICacheInterceptorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CacheInterceptorRefresh<K,V> implements ICacheInterceptor<K, V> {

    private static final Logger log = LoggerFactory.getLogger(CacheInterceptorRefresh.class);

    @Override
    public void before(ICacheInterceptorContext<K,V> context) {
        log.debug("Refresh start");
        final ICache<K,V> cache = context.cache();
        cache.expire().refreshExpire(cache.keySet());
    }

    @Override
    public void after(ICacheInterceptorContext<K,V> context) {
    }

}
