package com.github.codeman.nancy.core.support.persist;


import com.alibaba.fastjson.JSON;
import com.github.codeman.nancy.api.ICache;
import com.github.codeman.nancy.core.model.PersistRdbEntry;
import com.github.codeman.nancy.core.util.FileUtil;

import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class CachePersistDbJson<K,V> extends CachePersistAdaptor<K,V> {

    
    private final String dbPath;

    public CachePersistDbJson(String dbPath) {
        this.dbPath = dbPath;
    }

    
    @Override
    public void persist(ICache<K, V> cache) {
        Set<Map.Entry<K,V>> entrySet = cache.entrySet();

        // 创建文件
        FileUtil.createFile(dbPath);
        // 清空文件
        FileUtil.truncate(dbPath);

        for(Map.Entry<K,V> entry : entrySet) {
            K key = entry.getKey();
            Long expireTime = cache.expire().expireTime(key);
            PersistRdbEntry<K,V> persistRdbEntry = new PersistRdbEntry<>();
            persistRdbEntry.setKey(key);
            persistRdbEntry.setValue(entry.getValue());
            persistRdbEntry.setExpire(expireTime);

            String line = JSON.toJSONString(persistRdbEntry);
            FileUtil.write(dbPath, line, StandardOpenOption.APPEND);
        }
    }

    @Override
    public long delay() {
        return 5;
    }

    @Override
    public long period() {
        return 5;
    }

    @Override
    public TimeUnit timeUnit() {
        return TimeUnit.MINUTES;
    }

}
