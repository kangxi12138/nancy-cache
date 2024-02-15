package com.github.codeman.nancy.core.support.listener.remove;


import com.github.codeman.nancy.api.ICacheRemoveListener;

import java.util.ArrayList;
import java.util.List;

public class CacheRemoveListeners {

    private CacheRemoveListeners(){}

    
    @SuppressWarnings("all")
    public static <K,V> List<ICacheRemoveListener<K,V>> defaults() {
        List<ICacheRemoveListener<K,V>> listeners = new ArrayList<>();
        listeners.add(new CacheRemoveListener());
        return listeners;
    }

}
