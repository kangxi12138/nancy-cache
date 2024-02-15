package com.github.codeman.nancy.core.support.listener.slow;


import com.github.codeman.nancy.api.ICacheSlowListenerContext;


public class CacheSlowListenerContext implements ICacheSlowListenerContext {

    
    private String methodName;

    
    private Object[] params;

    
    private Object result;

    
    private long startTimeMills;

    
    private long endTimeMills;

    
    private long costTimeMills;

    
    public static CacheSlowListenerContext newInstance() {
        return new CacheSlowListenerContext();
    }

    @Override
    public String methodName() {
        return methodName;
    }

    public CacheSlowListenerContext methodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    @Override
    public Object[] params() {
        return params;
    }

    public CacheSlowListenerContext params(Object[] params) {
        this.params = params;
        return this;
    }

    @Override
    public Object result() {
        return result;
    }

    public CacheSlowListenerContext result(Object result) {
        this.result = result;
        return this;
    }

    @Override
    public long startTimeMills() {
        return startTimeMills;
    }

    public CacheSlowListenerContext startTimeMills(long startTimeMills) {
        this.startTimeMills = startTimeMills;
        return this;
    }

    @Override
    public long endTimeMills() {
        return endTimeMills;
    }

    public CacheSlowListenerContext endTimeMills(long endTimeMills) {
        this.endTimeMills = endTimeMills;
        return this;
    }

    @Override
    public long costTimeMills() {
        return costTimeMills;
    }

    public CacheSlowListenerContext costTimeMills(long costTimeMills) {
        this.costTimeMills = costTimeMills;
        return this;
    }
}
