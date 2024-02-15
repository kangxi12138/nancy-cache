package com.github.codeman.nancy.core.core;


import com.github.codeman.nancy.annotation.CacheInterceptor;
import com.github.codeman.nancy.api.*;
import com.github.codeman.nancy.core.constant.CacheRemoveType;
import com.github.codeman.nancy.core.exception.CacheRuntimeException;
import com.github.codeman.nancy.core.support.evict.CacheEvictContext;
import com.github.codeman.nancy.core.support.expire.CacheExpire;
import com.github.codeman.nancy.core.support.listener.remove.CacheRemoveListenerContext;
import com.github.codeman.nancy.core.support.persist.InnerCachePersist;
import com.github.codeman.nancy.core.support.proxy.CacheProxy;


import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;


public class Cache<K,V> implements ICache<K,V> {

    
    private volatile Map<K,V>[] map=(Map<K, V>[]) new Map<?, ?>[segmentSize];

    private static final  int segmentSize = 16;

    private final int segmentMask=segmentSize-1;

    private final ReentrantReadWriteLock[] locks=new ReentrantReadWriteLock[segmentSize];


    private volatile int sizeLimit;

    
    private volatile ICacheEvict<K,V> evict;


    private volatile ICacheExpire<K,V> expire;


    private volatile List<ICacheRemoveListener<K,V>> removeListeners;

    
    private volatile List<ICacheSlowListener> slowListeners;

    
    private volatile ICacheLoad<K,V> load;

    
    private volatile ICachePersist<K,V> persist;

    /**
     * 初始化保持单例
     */
    public Cache(){
           // 为数组的每个元素赋值为一个新的 HashMap 实例
           for (int i = 0; i < segmentSize; i++) {
               map[i] = new HashMap<>();
           }
           //初始化锁
           for (int i = 0; i < segmentSize; i++) {
               locks[i]=new ReentrantReadWriteLock();
           }

    }

    /**
     * 保持性能expire没有锁，需要Expire自行解决线程危机
     * @param key
     * @param timeInMills
     * @return
     */
    @Override
    @CacheInterceptor
    public ICache<K, V> expire(K key, long timeInMills) {
        long expireTime = System.currentTimeMillis() + timeInMills;
        return this.expireAt(key, expireTime);
    }
    @Override
    @CacheInterceptor(aof = true)
    public ICache<K, V> expireAt(K key, long timeInMills) {
        this.expire.expire(key, timeInMills);
        return this;
    }
    @Override
    @CacheInterceptor(aof = true, evict = true)
    public V put(K key, V value) {
        //尝试淘汰
        CacheEvictContext<K,V> context = new CacheEvictContext<>();
        context.key(key).size(sizeLimit).cache(this);
        //执行淘汰
        ICacheEntry<K,V> evictEntry = evict.evict(context);

        // 添加拦截器调用
        if(null != evictEntry) {
            // 执行淘汰监听器
            ICacheRemoveListenerContext<K,V> removeListenerContext = CacheRemoveListenerContext.<K,V>newInstance().key(evictEntry.key())
                    .value(evictEntry.value())
                    .type(CacheRemoveType.EVICT.code());
            for(ICacheRemoveListener<K,V> listener : context.cache().removeListeners()) {
                listener.listen(removeListenerContext);
            }
        }

        //判断驱除后的信息
        if(isSizeLimit()) {
            throw new CacheRuntimeException("当前队列已满，数据添加失败！");
        }

        //执行添加
        int segmentIndex=(key.hashCode()>>>16)&segmentMask;
        try {
            locks[segmentIndex].writeLock().lock();
            return map[segmentIndex].put(key,value);
        }finally {
            locks[segmentIndex].writeLock().unlock();
        }
    }

    @Override
    @CacheInterceptor(evict = true)
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        //1. 刷新所有过期信息
        K genericKey = (K) key;
        this.expire.refreshExpire(Collections.singletonList(genericKey));


        int segmentIndex=(key.hashCode()>>>16)&segmentMask;
        try {
            locks[segmentIndex].readLock().lock();
            return map[segmentIndex].get(key);
        }finally {
            locks[segmentIndex].readLock().unlock();
        }
    }

    @Override
    @CacheInterceptor
    public ICacheExpire<K, V> expire() {
        return this.expire;
    }
    @CacheInterceptor
    public synchronized void expire(ICacheExpire<K,V> expire){
        if(this.expire!=null){
            throw new IllegalArgumentException("无法更改expire");
        }
        this.expire=expire;
    }
    public Cache<K, V> sizeLimit(int sizeLimit) {
        this.sizeLimit=sizeLimit;
        return this;
    }
    public Cache<K, V> evict(ICacheEvict<K, V> cacheEvict) {
        this.evict = cacheEvict;
        return this;
    }
    @Override
    public ICacheEvict<K, V> evict() {
        return this.evict;
    }
    @Override
    public ICachePersist<K, V> persist() {
        return persist;
    }
    @CacheInterceptor
    public synchronized void persist(ICachePersist<K, V> persist) {
        if(this.persist!=null)
            throw new IllegalArgumentException("无法更改persist");
        this.persist = persist;
    }
    @Override
    public List<ICacheRemoveListener<K, V>> removeListeners() {
        return removeListeners;
    }
    public Cache<K, V> removeListeners(List<ICacheRemoveListener<K, V>> removeListeners) {
        this.removeListeners = removeListeners;
        return this;
    }
    @Override
    public List<ICacheSlowListener> slowListeners() {
        return slowListeners;
    }
    public Cache<K, V> slowListeners(List<ICacheSlowListener> slowListeners) {
        this.slowListeners = slowListeners;
        return this;
    }
    @Override
    public ICacheLoad<K, V> load() {
        return load;
    }
    public Cache<K, V> load(ICacheLoad<K, V> load) {
        this.load = load;
        return this;
    }
    public void init() {
        //开启过期线程
        this.expire.init(this);
        //加载
        this.load.load(this);

        // 开启持久化线程
        if(this.persist != null) {
            new InnerCachePersist<>(this, persist);
        }
    }
    private boolean isSizeLimit() {
        final  int  currentSize = this.size();
        return currentSize >= this.sizeLimit;
    }
    @Override
    @CacheInterceptor(refresh = true, aof = true)
    public void clear() {
        HashMap<K, V>[] mapArray = Stream.generate(HashMap<K, V>::new)
                .limit(segmentSize)
                .toArray(HashMap[]::new);
        map=mapArray;
    }
    @Override
    @CacheInterceptor(refresh = true)
    public boolean isEmpty() {
        boolean flag0=true;
        for (Map<K, V> kvMap : map) {
            if(!kvMap.isEmpty())
                return false;
        }
        return flag0;
    }
    /*-----------------------------------------------------------------*/
    @Override
    @CacheInterceptor(refresh = true)
    public  int size() {
        int size0=0;
        for (int i = 0; i < segmentSize; i++) {
            try {
                locks[i].readLock().lock();
                size0+=map[i].size();
            }finally {
                locks[i].readLock().unlock();
            }
        }
        return size0;
    }
    @Override
    @CacheInterceptor(refresh = true)
    public boolean containsValue(Object value) {
        boolean flag0 = false;
        for (int i = 0; i < segmentSize; i++) {
            try {
                locks[i].readLock().lock();
                if(map[i].containsValue(value))
                    return true;
            }finally {
                locks[i].readLock().unlock();
            }
        }
        return flag0;
    }


    @Override
    @CacheInterceptor(refresh = true, evict = true)
    public boolean containsKey(Object key) {
        int segmentIndex=(key.hashCode()>>>16)&segmentMask;
        boolean flag0=false;
        try {
            locks[segmentIndex].readLock().lock();
            flag0=map[segmentIndex].containsKey(key);
        }finally {
            locks[segmentIndex].readLock().unlock();
        }
        return flag0;
    }

    /*-----------------------------------------------------------*/
    @Override
    @CacheInterceptor(aof = true, evict = true)
    public V remove(Object key) {
      int segmentIndex=(key.hashCode()>>>16)&segmentMask;
      try {
          locks[segmentIndex].writeLock().lock();
          return map[segmentIndex].remove(key);
      }finally {
          locks[segmentIndex].writeLock().unlock();
      }
    }



    @Override
    @CacheInterceptor(refresh = true)
    public Set<K> keySet() {
        Set<K> res=new HashSet<>();
        for (int i = 0; i < segmentSize; i++) {
            try {
                locks[i].readLock().lock();
                res.addAll(map[i].keySet());
            }finally {
                locks[i].readLock().unlock();
            }
        }
        return res;
    }

    @Override
    @CacheInterceptor(refresh = true)
    public Collection<V> values() {
        Collection<V> res=new HashSet<>();
        for (int i = 0; i < segmentSize; i++) {
            try {
                locks[i].readLock().lock();
                res.addAll(map[i].values());
            }finally {
                locks[i].readLock().unlock();
            }
        }
        return res;
    }

    @Override
    @CacheInterceptor(refresh = true)
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K,V>>set=null;
        for (int i = 0; i < segmentSize; i++) {
            try {
                locks[i].readLock().lock();
                if(i==0){
                    set=map[i].entrySet();
                }else{
                    set.addAll(map[i].entrySet());
                }

            }finally {
                locks[i].readLock().unlock();
            }
        }
        return set;
    }

    @Override
    @CacheInterceptor(aof = true)
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            K key=entry.getKey();
            V val=entry.getValue();
            put(key,val);
        }
    }
    


}
