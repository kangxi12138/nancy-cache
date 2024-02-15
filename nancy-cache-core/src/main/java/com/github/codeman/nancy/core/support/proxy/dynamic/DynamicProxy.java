

package com.github.codeman.nancy.core.support.proxy.dynamic;

import com.github.codeman.nancy.api.ICache;
import com.github.codeman.nancy.core.support.proxy.ICacheProxy;
import com.github.codeman.nancy.core.support.proxy.bs.CacheProxyBootstrap;
import com.github.codeman.nancy.core.support.proxy.bs.CacheProxyBootstrapContext;
import com.github.codeman.nancy.core.support.proxy.bs.ICacheProxyBootstrapContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;



public class DynamicProxy implements InvocationHandler, ICacheProxy {

    
    private final ICache target;

    public DynamicProxy(ICache target) {
        this.target = target;
    }

    
    @Override
    @SuppressWarnings("all")
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ICacheProxyBootstrapContext context = CacheProxyBootstrapContext.newInstance()
                .method(method).params(args).target(target);
        return CacheProxyBootstrap.newInstance().context(context).execute();
    }

    @Override
    public Object proxy() {
        // 我们要代理哪个真实对象，就将该对象传进去，最后是通过该真实对象来调用其方法的
        InvocationHandler handler = new DynamicProxy(target);

        return Proxy.newProxyInstance(handler.getClass().getClassLoader(),
                target.getClass().getInterfaces(), handler);
    }
}
