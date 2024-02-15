package com.github.codeman.nancy.core.support.listener.slow;


import com.alibaba.fastjson.JSON;
import com.github.codeman.nancy.api.ICacheSlowListener;
import com.github.codeman.nancy.api.ICacheSlowListenerContext;
import com.github.codeman.nancy.core.support.interceptor.common.CacheInterceptorCost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CacheSlowListener implements ICacheSlowListener {

    private static final Logger log = LoggerFactory.getLogger(CacheInterceptorCost.class);

    @Override
    public void listen(ICacheSlowListenerContext context) {
        log.warn("[Slow] methodName: {}, params: {}, cost time: {}",
                context.methodName(), JSON.toJSON(context.params()), context.costTimeMills());
    }

    @Override
    public long slowerThanMills() {
        return 1000L;
    }

}
