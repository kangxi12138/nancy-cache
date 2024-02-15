package com.github.codeman.nancy.core.support.proxy.bs;


import com.github.codeman.nancy.annotation.CacheInterceptor;
import com.github.codeman.nancy.api.ICache;

import java.lang.reflect.Method;

public class CacheProxyBootstrapContext implements ICacheProxyBootstrapContext {

    
    private ICache target;

    
    private Object[] params;

    
    private Method method;

    
    private CacheInterceptor interceptor;

    
    public static CacheProxyBootstrapContext newInstance(){
        return new CacheProxyBootstrapContext();
    }

    @Override
    public ICache target() {
        return target;
    }

    @Override
    public CacheProxyBootstrapContext target(ICache target) {
        this.target = target;
        return this;
    }

    @Override
    public Object[] params() {
        return params;
    }

    public CacheProxyBootstrapContext params(Object[] params) {
        this.params = params;
        return this;
    }

    @Override
    public Method method() {
        return method;
    }

    @Override
    public Object process() throws Throwable {
        return this.method.invoke(target, params);
    }

    public CacheProxyBootstrapContext method(Method method) {
        this.method = method;
        this.interceptor = method.getAnnotation(CacheInterceptor.class);
        return this;
    }

    @Override
    public CacheInterceptor interceptor() {
        return interceptor;
    }
}
