package com.github.codeman.nancy.core.support.interceptor.common;

import cn.hutool.core.collection.CollectionUtil;
import com.github.codeman.nancy.api.ICacheInterceptor;
import com.github.codeman.nancy.api.ICacheInterceptorContext;
import com.github.codeman.nancy.api.ICacheSlowListener;
import com.github.codeman.nancy.core.support.listener.slow.CacheSlowListenerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CacheInterceptorCost<K,V> implements ICacheInterceptor<K,V> {

    private static final Logger log = LoggerFactory.getLogger(CacheInterceptorCost.class);

    @Override
    public void before(ICacheInterceptorContext<K,V> context) {
        log.debug("Cost start, method: {}", context.method().getName());
    }

    @Override
    public void after(ICacheInterceptorContext<K,V> context) {
        long costMills = context.endMills()-context.startMills();
        final String methodName = context.method().getName();
        log.debug("Cost end, method: {}, cost: {}ms", methodName, costMills);

        // 添加慢日志操作
        List<ICacheSlowListener> slowListeners = context.cache().slowListeners();
        if(CollectionUtil.isNotEmpty(slowListeners)) {
            CacheSlowListenerContext listenerContext = CacheSlowListenerContext.newInstance().startTimeMills(context.startMills())
                    .endTimeMills(context.endMills())
                    .costTimeMills(costMills)
                    .methodName(methodName)
                    .params(context.params())
                    .result(context.result())
                    ;

            // 设置多个，可以考虑不同的慢日志级别，做不同的处理
            for(ICacheSlowListener slowListener : slowListeners) {
                long slowThanMills = slowListener.slowerThanMills();
                if(costMills >= slowThanMills) {
                    slowListener.listen(listenerContext);
                }
            }
        }
    }

}
