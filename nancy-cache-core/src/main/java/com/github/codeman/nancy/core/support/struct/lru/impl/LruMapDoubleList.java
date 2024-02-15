package com.github.codeman.nancy.core.support.struct.lru.impl;


import com.github.codeman.nancy.api.ICacheEntry;
import com.github.codeman.nancy.core.exception.CacheRuntimeException;
import com.github.codeman.nancy.core.model.CacheEntry;
import com.github.codeman.nancy.core.model.DoubleListNode;
import com.github.codeman.nancy.core.support.struct.lru.ILruMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public class LruMapDoubleList<K,V> implements ILruMap<K,V> {

    private static final Logger log = LoggerFactory.getLogger(LruMapDoubleList.class);

    
    private DoubleListNode<K,V> head;

    
    private DoubleListNode<K,V> tail;

    
    private Map<K, DoubleListNode<K,V>> indexMap;

    public LruMapDoubleList() {
        this.indexMap = new HashMap<>();
        this.head = new DoubleListNode<>();
        this.tail = new DoubleListNode<>();

        this.head.next(this.tail);
        this.tail.pre(this.head);
    }

    @Override
    public ICacheEntry<K, V> removeEldest() {
        // 获取尾巴节点的前一个元素
        DoubleListNode<K,V> tailPre = this.tail.pre();
        if(tailPre == this.head) {
            log.error("当前列表为空，无法进行删除");
            throw new CacheRuntimeException("不可删除头结点!");
        }

        K evictKey = tailPre.key();
        V evictValue = tailPre.value();

        // 执行删除
        this.removeKey(evictKey);

        return CacheEntry.of(evictKey, evictValue);
    }

    
    @Override
    public void updateKey(final K key) {
        //1. 执行删除
        this.removeKey(key);

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
        indexMap.put(key, newNode);
    }

    
    @Override
    public void removeKey(final K key) {
        DoubleListNode<K,V> node = indexMap.get(key);

        if(node==null) {
            return;
        }

        // 删除 list node
        // A<->B<->C
        // 删除 B，需要变成： A<->C
        DoubleListNode<K,V> pre = node.pre();
        DoubleListNode<K,V> next = node.next();

        pre.next(next);
        next.pre(pre);

        // 删除 map 中对应信息
        this.indexMap.remove(key);
        log.debug("从 LruMapDoubleList 中移除 key: {}", key);
    }

    @Override
    public boolean isEmpty() {
        return indexMap.isEmpty();
    }

    @Override
    public boolean contains(K key) {
        return indexMap.containsKey(key);
    }

}
