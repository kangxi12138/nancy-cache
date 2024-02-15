

package com.github.codeman.nancy.core.support.proxy.none;



import com.github.codeman.nancy.core.support.proxy.ICacheProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


public class NoneProxy implements InvocationHandler, ICacheProxy {

    
    private final Object target;

    public NoneProxy(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(proxy, args);
    }

    
    @Override
    public Object proxy() {
        return this.target;
    }

}
