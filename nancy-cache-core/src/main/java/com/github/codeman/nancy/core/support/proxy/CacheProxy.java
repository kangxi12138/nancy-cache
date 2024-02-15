

package com.github.codeman.nancy.core.support.proxy;


import com.github.codeman.nancy.api.ICache;
import com.github.codeman.nancy.core.support.proxy.cglib.CglibProxy;
import com.github.codeman.nancy.core.support.proxy.dynamic.DynamicProxy;
import com.github.codeman.nancy.core.support.proxy.none.NoneProxy;

import java.lang.reflect.Proxy;


public final class CacheProxy {

    private CacheProxy(){}

    
    @SuppressWarnings("all")
    public static <K,V> ICache<K,V> getProxy(final ICache<K,V> cache) {
        if(null==cache) {
            return (ICache<K,V>) new NoneProxy(cache).proxy();
        }

        final Class clazz = cache.getClass();

        // 如果targetClass本身是个接口或者targetClass是JDK Proxy生成的,则使用JDK动态代理。
        // 参考 spring 的 AOP 判断
        if (clazz.isInterface() || Proxy.isProxyClass(clazz)) {
            return (ICache<K,V>) new DynamicProxy(cache).proxy();
        }

        return (ICache<K,V>) new CglibProxy(cache).proxy();
    }

}
