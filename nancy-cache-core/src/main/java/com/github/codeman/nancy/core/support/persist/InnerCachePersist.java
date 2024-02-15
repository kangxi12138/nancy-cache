package com.github.codeman.nancy.core.support.persist;


import com.github.codeman.nancy.api.ICache;
import com.github.codeman.nancy.api.ICachePersist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class InnerCachePersist<K,V> {

    private static final Logger log = LoggerFactory.getLogger(InnerCachePersist.class);

    
    private final ICache<K,V> cache;

    
    private final ICachePersist<K,V> persist;

    
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    public InnerCachePersist(ICache<K, V> cache, ICachePersist<K, V> persist) {
        this.cache = cache;
        this.persist = persist;

        // 初始化
        this.init();
    }

    
    private void init() {
        EXECUTOR_SERVICE.scheduleAtFixedRate(() ->{
            try {
                log.debug("开始持久化缓存信息");
                persist.persist(cache);
                log.debug("完成持久化缓存信息");
            } catch (Exception exception) {
                log.error("文件持久化异常", exception);
            }
        }, persist.delay(), persist.period(), persist.timeUnit());
    }

}
