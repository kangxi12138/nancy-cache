package com.github.codeman.nancy.core.support.listener.slow;


import com.github.codeman.nancy.api.ICacheSlowListener;

import java.util.ArrayList;
import java.util.List;

public final class CacheSlowListeners {

    private CacheSlowListeners(){}

    
    public static List<ICacheSlowListener> none() {
        return new ArrayList<>();
    }

    
    public static ICacheSlowListener defaults() {
        return new CacheSlowListener();
    }

}
