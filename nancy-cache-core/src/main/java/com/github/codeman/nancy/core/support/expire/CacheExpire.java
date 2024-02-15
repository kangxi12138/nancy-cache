package com.github.codeman.nancy.core.support.expire;


import com.github.codeman.nancy.api.ICache;
import com.github.codeman.nancy.api.ICacheExpire;
import com.github.codeman.nancy.api.ICacheRemoveListener;
import com.github.codeman.nancy.api.ICacheRemoveListenerContext;
import com.github.codeman.nancy.core.constant.CacheRemoveType;
import com.github.codeman.nancy.core.support.listener.remove.CacheRemoveListenerContext;

import java.util.Collection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CacheExpire<K,V> implements ICacheExpire<K,V> {

    
    private static final int LIMIT = 100;

    
    private final Map<K, Long> expireMap = new ConcurrentHashMap<>();

    
    private  ICache<K,V> cache;

    
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    public CacheExpire() {
    }

    @Override
    public void init(ICache<K,V> cache) {
        this.cache=cache;
        EXECUTOR_SERVICE.scheduleAtFixedRate(new ExpireThread(), 100, 100, TimeUnit.MILLISECONDS);
    }

    private class ExpireThread implements Runnable {
        @Override
        public void run() {
            //1.判断是否为空
            if(expireMap.isEmpty()) {
                return;
            }

            //2. 获取 key 进行处理
            int count = 0;
            for(Map.Entry<K, Long> entry : expireMap.entrySet()) {
                if(count >= LIMIT) {
                    return;
                }

                expireKey(entry.getKey(), entry.getValue());
                count++;
            }
        }
    }

    @Override
    public void expire(K key, long expireAt) {
        expireMap.put(key, expireAt);
    }

    @Override
    public void refreshExpire(Collection<K> keyList) {
        if(null == keyList || keyList.isEmpty()) {
            return;
        }

        // 判断大小，小的作为外循环。一般都是过期的 keys 比较小。
        if(keyList.size() <= expireMap.size()) {
            for(K key : keyList) {
                Long expireAt = expireMap.get(key);
                expireKey(key, expireAt);
            }
        } else {
            for(Map.Entry<K, Long> entry : expireMap.entrySet()) {
                this.expireKey(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public Long expireTime(K key) {
        return expireMap.get(key);
    }

    
    private void expireKey(final K key, final Long expireAt) {
        if(expireAt == null) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        if(currentTime >= expireAt) {
            expireMap.remove(key);
            // 再移除缓存，后续可以通过惰性删除做补偿
            V removeValue = cache.remove(key);

            // 执行淘汰监听器
            ICacheRemoveListenerContext<K,V> removeListenerContext = CacheRemoveListenerContext.<K,V>newInstance().key(key).value(removeValue).type(CacheRemoveType.EXPIRE.code());
            for(ICacheRemoveListener<K,V> listener : cache.removeListeners()) {
                listener.listen(removeListenerContext);
            }
        }
    }

}
