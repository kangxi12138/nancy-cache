package com.github.codeman.nancy.core.support.proxy.cglib;


import com.github.codeman.nancy.api.ICache;
import com.github.codeman.nancy.core.support.proxy.ICacheProxy;
import com.github.codeman.nancy.core.support.proxy.bs.CacheProxyBootstrap;
import com.github.codeman.nancy.core.support.proxy.bs.CacheProxyBootstrapContext;
import com.github.codeman.nancy.core.support.proxy.bs.ICacheProxyBootstrapContext;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibProxy implements MethodInterceptor, ICacheProxy {

    
    private final ICache target;

    public CglibProxy(ICache target) {
        this.target = target;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] params, MethodProxy methodProxy) throws Throwable {
        ICacheProxyBootstrapContext context = CacheProxyBootstrapContext.newInstance()
                .method(method).params(params).target(target);

        return CacheProxyBootstrap.newInstance().context(context).execute();
    }

    @Override
    public Object proxy() {
        Enhancer enhancer = new Enhancer();
        //目标对象类
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(this);
        //通过字节码技术创建目标对象类的子类实例作为代理
        return enhancer.create();
    }

}
