package com.github.codeman.nancy.core.support.expire;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import com.github.codeman.nancy.api.ICache;
import com.github.codeman.nancy.api.ICacheExpire;
import net.sf.cglib.core.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CacheExpireSort<K,V> implements ICacheExpire<K,V> {

    
    private static final int LIMIT = 100;

    
    private final Map<Long, List<K>> sortMap = new TreeMap<>(new Comparator<Long>() {
        @Override
        public int compare(Long o1, Long o2) {
            return (int) (o1-o2);
        }
    });

    
    private final Map<K, Long> expireMap = new ConcurrentHashMap<>();

    
    private  ICache<K,V> cache;


    private final Lock lock=new ReentrantLock();

    
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();


    @Override
    public  void init(ICache<K,V> cache) {
        this.cache = cache;
        EXECUTOR_SERVICE.scheduleAtFixedRate(new ExpireThread(), 100, 100, TimeUnit.MILLISECONDS);
    }

    
    private class ExpireThread implements Runnable {
        @Override
        public void run() {
            //1.判断是否为空
            if(MapUtil.isEmpty(sortMap)) {
                return;
            }

            //2. 获取 key 进行处理
            int count = 0;
            for(Map.Entry<Long, List<K>> entry : sortMap.entrySet()) {
                final Long expireAt = entry.getKey();
                List<K> expireKeys = entry.getValue();

                // 判断队列是否为空
                if(CollectionUtil.isEmpty(expireKeys)) {
                    sortMap.remove(expireAt);
                    continue;
                }
                if(count >= LIMIT) {
                    return;
                }

                // 删除的逻辑处理
                long currentTime = System.currentTimeMillis();
                if(currentTime >= expireAt) {
                    Iterator<K> iterator = expireKeys.iterator();
                    while (iterator.hasNext()) {
                        K key = iterator.next();
                        // 先移除本身
                        iterator.remove();
                        expireMap.remove(key);

                        // 再移除缓存，后续可以通过惰性删除做补偿
                        cache.remove(key);

                        count++;
                    }
                } else {
                    // 直接跳过，没有过期的信息
                    return;
                }
            }
        }
    }

    @Override
    public void expire(K key, long expireAt) {
        lock.lock();
        try{
            List<K> keys = sortMap.get(expireAt);
            if(keys == null) {
                keys = new ArrayList<>();
            }
            keys.add(key);

            // 设置对应的信息
            sortMap.put(expireAt, keys);
            expireMap.put(key, expireAt);
        }finally {
            lock.unlock();
        }
    }

    @Override
    public void refreshExpire(Collection<K> keyList) {
        if(CollectionUtil.isEmpty(keyList)) {
            return;
        }

        // 这样维护两套的代价太大，后续优化，暂时不用。
        // 判断大小，小的作为外循环
        final int expireSize = expireMap.size();
        if(expireSize <= keyList.size()) {
            // 一般过期的数量都是较少的
            for(Map.Entry<K,Long> entry : expireMap.entrySet()) {
                K key = entry.getKey();

                // 这里直接执行过期处理，不再判断是否存在于集合中。
                // 因为基于集合的判断，时间复杂度为 O(n)
                this.removeExpireKey(key);
            }
        } else {
            for(K key : keyList) {
                this.removeExpireKey(key);
            }
        }
    }

    
    private void removeExpireKey(final K key) {
        Long expireTime = expireMap.get(key);
        if(expireTime != null) {
            final long currentTime = System.currentTimeMillis();
            if(currentTime >= expireTime) {
                expireMap.remove(key);

                List<K> expireKeys = sortMap.get(expireTime);
                expireKeys.remove(key);
                sortMap.put(expireTime, expireKeys);
            }
        }
    }

    @Override
    public Long expireTime(K key) {
        return expireMap.get(key);
    }

}
