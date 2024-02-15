package com.github.codeman.nancy.core.support.interceptor.evict;


import com.github.codeman.nancy.api.ICacheEvict;
import com.github.codeman.nancy.api.ICacheInterceptor;
import com.github.codeman.nancy.api.ICacheInterceptorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.lang.reflect.Method;

public class CacheInterceptorEvict<K,V> implements ICacheInterceptor<K, V> {

    private static final Logger log = LoggerFactory.getLogger(CacheInterceptorEvict.class);

    @Override
    public void before(ICacheInterceptorContext<K,V> context) {
    }

    @Override
    @SuppressWarnings("all")
    public void after(ICacheInterceptorContext<K,V> context) {
        ICacheEvict<K,V> evict = context.cache().evict();

        Method method = context.method();
        final K key = (K) context.params()[0];
        if("remove".equals(method.getName())) {
            evict.removeKey(key);
        } else {
            evict.updateKey(key);
        }
    }

}
