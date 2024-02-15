package com.github.codeman.nancy.core.support.proxy.bs;


import com.github.codeman.nancy.annotation.CacheInterceptor;
import com.github.codeman.nancy.api.ICache;
import com.github.codeman.nancy.api.ICacheInterceptor;
import com.github.codeman.nancy.api.ICachePersist;
import com.github.codeman.nancy.core.support.interceptor.CacheInterceptorContext;
import com.github.codeman.nancy.core.support.interceptor.CacheInterceptors;
import com.github.codeman.nancy.core.support.persist.CachePersistAof;

import java.util.List;

public final class CacheProxyBootstrap {

    private CacheProxyBootstrap(){}

    
    private ICacheProxyBootstrapContext context;

    
    @SuppressWarnings("all")
    private final List<ICacheInterceptor> commonInterceptors = CacheInterceptors.defaultCommonList();

    
    @SuppressWarnings("all")
    private final List<ICacheInterceptor> refreshInterceptors = CacheInterceptors.defaultRefreshList();

    
    @SuppressWarnings("all")
    private final ICacheInterceptor persistInterceptors = CacheInterceptors.aof();

    
    @SuppressWarnings("all")
    private final ICacheInterceptor evictInterceptors = CacheInterceptors.evict();

    
    public static CacheProxyBootstrap newInstance() {
        return new CacheProxyBootstrap();
    }

    public CacheProxyBootstrap context(ICacheProxyBootstrapContext context) {
        this.context = context;
        return this;
    }

    
    @SuppressWarnings("all")
    public Object execute() throws Throwable {
        //1. 开始的时间
        final long startMills = System.currentTimeMillis();
        final ICache cache = context.target();
        CacheInterceptorContext interceptorContext = CacheInterceptorContext.newInstance()
                .startMills(startMills)
                .method(context.method())
                .params(context.params())
                .cache(context.target())
                ;

        //1. 获取刷新注解信息
        CacheInterceptor cacheInterceptor = context.interceptor();
        this.interceptorHandler(cacheInterceptor, interceptorContext, cache, true);

        //2. 正常执行
        Object result = context.process();

        final long endMills = System.currentTimeMillis();
        interceptorContext.endMills(endMills).result(result);

        // 方法执行完成
        this.interceptorHandler(cacheInterceptor, interceptorContext, cache, false);
        return result;
    }

    
    @SuppressWarnings("all")
    private void interceptorHandler(CacheInterceptor cacheInterceptor,
                                    CacheInterceptorContext interceptorContext,
                                    ICache cache,
                                    boolean before) {
        if(cacheInterceptor != null) {
            //1. 通用
            if(cacheInterceptor.common()) {
                for(ICacheInterceptor interceptor : commonInterceptors) {
                    if(before) {
                        interceptor.before(interceptorContext);
                    } else {
                        interceptor.after(interceptorContext);
                    }
                }
            }

            //2. 刷新
            if(cacheInterceptor.refresh()) {
                for(ICacheInterceptor interceptor : refreshInterceptors) {
                    if(before) {
                        interceptor.before(interceptorContext);
                    } else {
                        interceptor.after(interceptorContext);
                    }
                }
            }

            //3. AOF 追加
            final ICachePersist cachePersist = cache.persist();
            if(cacheInterceptor.aof() && (cachePersist instanceof CachePersistAof)) {
                if(before) {
                    persistInterceptors.before(interceptorContext);
                } else {
                    persistInterceptors.after(interceptorContext);
                }
            }

            //4. 驱除策略更新
            if(cacheInterceptor.evict()) {
                if(before) {
                    evictInterceptors.before(interceptorContext);
                } else {
                    evictInterceptors.after(interceptorContext);
                }
            }
        }
    }

}
