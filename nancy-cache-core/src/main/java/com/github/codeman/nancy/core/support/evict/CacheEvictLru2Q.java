package com.github.codeman.nancy.core.support.evict;


import cn.hutool.core.util.ObjectUtil;
import com.github.codeman.nancy.api.ICache;
import com.github.codeman.nancy.api.ICacheEntry;
import com.github.codeman.nancy.api.ICacheEvictContext;
import com.github.codeman.nancy.core.exception.CacheRuntimeException;
import com.github.codeman.nancy.core.model.CacheEntry;
import com.github.codeman.nancy.core.model.DoubleListNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class CacheEvictLru2Q<K,V> extends AbstractCacheEvict<K,V> {

    private static final Logger log = LoggerFactory.getLogger(CacheEvictLru2Q.class);

    
    private static final int LIMIT_QUEUE_SIZE = 1024;

    
    private Queue<K> firstQueue;

    
    private DoubleListNode<K,V> head;

    
    private DoubleListNode<K,V> tail;

    
    private Map<K, DoubleListNode<K,V>> lruIndexMap;

    public CacheEvictLru2Q() {
        this.firstQueue = new LinkedList<>();
        this.lruIndexMap = new HashMap<>();
        this.head = new DoubleListNode<>();
        this.tail = new DoubleListNode<>();

        this.head.next(this.tail);
        this.tail.pre(this.head);
    }

    @Override
    protected ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context) {
        ICacheEntry<K, V> result = null;
        final ICache<K,V> cache = context.cache();
        // 超过限制，移除队尾的元素
        if(cache.size() >= context.size()) {
            K evictKey = null;

            //1. firstQueue 不为空，优先移除队列中元素
            if(!firstQueue.isEmpty()) {
                evictKey = firstQueue.remove();
            } else {
                // 获取尾巴节点的前一个元素
                DoubleListNode<K,V> tailPre = this.tail.pre();
                if(tailPre == this.head) {
                    log.error("当前列表为空，无法进行删除");
                    throw new CacheRuntimeException("不可删除头结点!");
                }

                evictKey = tailPre.key();
            }

            // 执行移除操作
            V evictValue = cache.remove(evictKey);
            result = new CacheEntry<>(evictKey, evictValue);
        }

        return result;
    }


    
    @Override
    public void updateKey(final K key) {
        //1.1 是否在 LRU MAP 中
        //1.2 是否在 firstQueue 中
        DoubleListNode<K,V> node = lruIndexMap.get(key);
        if(ObjectUtil.isNotNull(node)
            || firstQueue.contains(key)) {
            //1.3 删除信息
            this.removeKey(key);

            //1.4 加入到 LRU 中
            this.addToLruMapHead(key);
            return;
        }

        //2. 直接加入到 firstQueue 队尾
//        if(firstQueue.size() >= LIMIT_QUEUE_SIZE) {
//            // 避免第一次访问的列表一直增长，移除队头的元素
//            firstQueue.remove();
//        }
        firstQueue.add(key);
    }

    
    private void addToLruMapHead(final K key) {
        //2. 新元素插入到头部
        //head<->next
        //变成：head<->new<->next
        DoubleListNode<K,V> newNode = new DoubleListNode<>();
        newNode.key(key);

        DoubleListNode<K,V> next = this.head.next();
        this.head.next(newNode);
        newNode.pre(this.head);
        next.pre(newNode);
        newNode.next(next);

        //2.2 插入到 map 中
        lruIndexMap.put(key, newNode);
    }

    
    @Override
    public void removeKey(final K key) {
        DoubleListNode<K,V> node = lruIndexMap.get(key);

        //1. LRU 删除逻辑
        if(ObjectUtil.isNotNull(node)) {
            // A<->B<->C
            // 删除 B，需要变成： A<->C
            DoubleListNode<K,V> pre = node.pre();
            DoubleListNode<K,V> next = node.next();

            pre.next(next);
            next.pre(pre);

            // 删除 map 中对应信息
            this.lruIndexMap.remove(node.key());
        } else {
            //2. FIFO 删除逻辑（O(n) 时间复杂度）
            firstQueue.remove(key);
        }
    }

}
