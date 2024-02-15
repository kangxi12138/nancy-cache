package com.github.codeman.nancy.core.support.evict;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.codeman.nancy.api.ICache;
import com.github.codeman.nancy.api.ICacheEntry;
import com.github.codeman.nancy.api.ICacheEvictContext;
import com.github.codeman.nancy.core.exception.CacheRuntimeException;
import com.github.codeman.nancy.core.model.CacheEntry;
import com.github.codeman.nancy.core.model.FreqNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class CacheEvictLfu<K,V> extends AbstractCacheEvict<K,V> {

    private static final Logger log = LoggerFactory.getLogger(CacheEvictLfu.class);

    
    private final Map<K, FreqNode<K,V>> keyMap;

    
    private final Map<Integer, LinkedHashSet<FreqNode<K,V>>> freqMap;

    
    private int minFreq;

    public CacheEvictLfu() {
        this.keyMap = new HashMap<>();
        this.freqMap = new HashMap<>();
        this.minFreq = 1;
    }

    @Override
    protected ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context) {
        ICacheEntry<K, V> result = null;
        final ICache<K,V> cache = context.cache();
        // 超过限制，移除频次最低的元素
        if(cache.size() >= context.size()) {
            FreqNode<K,V> evictNode = this.getMinFreqNode();
            K evictKey = evictNode.key();
            V evictValue = cache.remove(evictKey);

            log.debug("淘汰最小频率信息, key: {}, value: {}, freq: {}",
                    evictKey, evictValue, evictNode.frequency());
            result = new CacheEntry<>(evictKey, evictValue);
        }

        return result;
    }

    
    private FreqNode<K, V> getMinFreqNode() {
        LinkedHashSet<FreqNode<K,V>> set = freqMap.get(minFreq);

        if(CollectionUtil.isNotEmpty(set)) {
            return set.iterator().next();
        }

        throw new CacheRuntimeException("未发现最小频率的 Key");
    }


    
    @Override
    public void updateKey(final K key) {
        FreqNode<K,V> freqNode = keyMap.get(key);

        //1. 已经存在
        if(ObjectUtil.isNotNull(freqNode)) {
            //1.1 移除原始的节点信息
            int frequency = freqNode.frequency();
            LinkedHashSet<FreqNode<K,V>> oldSet = freqMap.get(frequency);
            oldSet.remove(freqNode);
            //1.2 更新最小数据频率
            if (minFreq == frequency && oldSet.isEmpty()) {
                minFreq++;
                log.debug("minFreq 增加为：{}", minFreq);
            }
            //1.3 更新频率信息
            frequency++;
            freqNode.frequency(frequency);
            //1.4 放入新的集合
            this.addToFreqMap(frequency, freqNode);
        } else {
            //2. 不存在
            //2.1 构建新的元素
            FreqNode<K,V> newNode = new FreqNode<>(key);

            //2.2 固定放入到频率为1的列表中
            this.addToFreqMap(1, newNode);

            //2.3 更新 minFreq 信息
            this.minFreq = 1;

            //2.4 添加到 keyMap
            this.keyMap.put(key, newNode);
        }
    }

    
    private void addToFreqMap(final int frequency, FreqNode<K,V> freqNode) {
        LinkedHashSet<FreqNode<K,V>> set = freqMap.get(frequency);
        if (set == null) {
            set = new LinkedHashSet<>();
        }
        set.add(freqNode);
        freqMap.put(frequency, set);
        log.debug("freq={} 添加元素节点：{}", frequency, freqNode);
    }

    
    @Override
    public void removeKey(final K key) {
        FreqNode<K,V> freqNode = this.keyMap.remove(key);

        //1. 根据 key 获取频率
        int freq = freqNode.frequency();
        LinkedHashSet<FreqNode<K,V>> set = this.freqMap.get(freq);

        //2. 移除频率中对应的节点
        set.remove(freqNode);
        log.debug("freq={} 移除元素节点：{}", freq, freqNode);

        //3. 更新 minFreq
        if(CollectionUtil.isEmpty(set) && minFreq == freq) {
            minFreq--;
            log.debug("minFreq 降低为：{}", minFreq);
        }
    }

}
