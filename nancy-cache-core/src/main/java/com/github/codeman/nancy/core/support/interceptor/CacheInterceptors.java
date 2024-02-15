package com.github.codeman.nancy.core.support.interceptor;

import com.github.codeman.nancy.api.ICacheInterceptor;
import com.github.codeman.nancy.core.support.interceptor.aof.CacheInterceptorAof;
import com.github.codeman.nancy.core.support.interceptor.common.CacheInterceptorCost;
import com.github.codeman.nancy.core.support.interceptor.evict.CacheInterceptorEvict;
import com.github.codeman.nancy.core.support.interceptor.refresh.CacheInterceptorRefresh;

import java.util.ArrayList;
import java.util.List;


public final class CacheInterceptors {

    
    @SuppressWarnings("all")
    public static List<ICacheInterceptor> defaultCommonList() {
        List<ICacheInterceptor> list = new ArrayList<>();
        list.add(new CacheInterceptorCost());
        return list;
    }

    
    @SuppressWarnings("all")
    public static List<ICacheInterceptor> defaultRefreshList() {
        List<ICacheInterceptor> list = new ArrayList<>();
        list.add(new CacheInterceptorRefresh());
        return list;
    }

    
    @SuppressWarnings("all")
    public static ICacheInterceptor aof() {
        return new CacheInterceptorAof();
    }

    
    @SuppressWarnings("all")
    public static ICacheInterceptor evict() {
        return new CacheInterceptorEvict();
    }

}
