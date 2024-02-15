package com.github.codeman.nancy.core.support.proxy.bs;


import com.github.codeman.nancy.annotation.CacheInterceptor;
import com.github.codeman.nancy.api.ICache;

import java.lang.reflect.Method;

public interface ICacheProxyBootstrapContext {

    
    CacheInterceptor interceptor();

    
    ICache target();

    
    ICacheProxyBootstrapContext target(final ICache target);

    
    Object[] params();

    
    Method method();

    
    Object process() throws Throwable;

}
