package com.github.codeman.nancy.core.support.listener.remove;


import com.github.codeman.nancy.api.ICacheRemoveListener;
import com.github.codeman.nancy.api.ICacheRemoveListenerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CacheRemoveListener<K,V> implements ICacheRemoveListener<K,V> {

    private static final Logger log = LoggerFactory.getLogger(CacheRemoveListener.class);

    @Override
    public void listen(ICacheRemoveListenerContext<K, V> context) {
        log.debug("Remove key: {}, value: {}, type: {}",
                context.key(), context.value(), context.type());
    }

}
