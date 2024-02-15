package com.github.codeman.nancy.core.support.load;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.github.codeman.nancy.api.ICache;
import com.github.codeman.nancy.api.ICacheLoad;
import com.github.codeman.nancy.core.model.PersistRdbEntry;

import com.github.codeman.nancy.core.util.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

public class CacheLoadDbJson<K,V> implements ICacheLoad<K,V> {

    private static final Logger log = LoggerFactory.getLogger(CacheLoadDbJson.class);

    
    private final String dbPath;

    public CacheLoadDbJson(String dbPath) {
        this.dbPath = dbPath;
    }

    @Override
    public void load(ICache<K, V> cache) {
        List<String> lines = FileUtil.readAllLines(dbPath);
        log.info("[load] 开始处理 path: {}", dbPath);
        if(CollectionUtil.isEmpty(lines)) {
            log.info("[load] path: {} 文件内容为空，直接返回", dbPath);
            return;
        }

        for(String line : lines) {
            if(StringUtils.isEmpty(line)) {
                continue;
            }

            // 执行
            // 简单的类型还行，复杂的这种反序列化会失败
            PersistRdbEntry<K,V> entry = JSON.parseObject(line, PersistRdbEntry.class);

            K key = entry.getKey();
            V value = entry.getValue();
            Long expire = entry.getExpire();

            cache.put(key, value);
            if(ObjectUtil.isNotNull(expire)) {
                cache.expireAt(key, expire);
            }
        }
        //nothing...
    }
}
