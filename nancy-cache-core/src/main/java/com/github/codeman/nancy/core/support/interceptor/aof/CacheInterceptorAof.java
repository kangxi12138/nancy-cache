package com.github.codeman.nancy.core.support.interceptor.aof;


import com.alibaba.fastjson.JSON;
import com.github.codeman.nancy.api.ICache;
import com.github.codeman.nancy.api.ICacheInterceptor;
import com.github.codeman.nancy.api.ICacheInterceptorContext;
import com.github.codeman.nancy.api.ICachePersist;
import com.github.codeman.nancy.core.model.PersistAofEntry;
import com.github.codeman.nancy.core.support.persist.CachePersistAof;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheInterceptorAof<K,V> implements ICacheInterceptor<K, V> {

    private static final Logger log = LoggerFactory.getLogger(CacheInterceptorAof.class);

    @Override
    public void before(ICacheInterceptorContext<K,V> context) {
    }

    @Override
    public void after(ICacheInterceptorContext<K,V> context) {
        // 持久化类
        ICache<K,V> cache = context.cache();
        ICachePersist<K,V> persist = cache.persist();

        if(persist instanceof CachePersistAof) {
            CachePersistAof<K,V> cachePersistAof = (CachePersistAof<K,V>) persist;

            String methodName = context.method().getName();
            PersistAofEntry aofEntry = PersistAofEntry.newInstance();
            aofEntry.setMethodName(methodName);
            aofEntry.setParams(context.params());

            String json = JSON.toJSONString(aofEntry);

            // 直接持久化
            log.debug("AOF 开始追加文件内容：{}", json);
            cachePersistAof.append(json);
            log.debug("AOF 完成追加文件内容：{}", json);
        }
    }

}
