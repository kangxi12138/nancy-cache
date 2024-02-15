package com.github.codeman.nancy.core.support.persist;


import com.github.codeman.nancy.api.ICache;
import com.github.codeman.nancy.core.util.FileUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CachePersistAof<K,V> extends CachePersistAdaptor<K,V> {

    private static final Logger log = LoggerFactory.getLogger(CachePersistAof.class);

    
    private final List<String> bufferList = new ArrayList<>();

    
    private final String dbPath;

    public CachePersistAof(String dbPath) {
        this.dbPath = dbPath;
    }

    
    @Override
    public void persist(ICache<K, V> cache) {
        log.debug("开始 AOF 持久化到文件");
        // 1. 创建文件
        if(!FileUtil.exists(dbPath)) {
            FileUtil.createFile(dbPath);
        }
        // 2. 持久化追加到文件中
        FileUtil.append(dbPath, bufferList);

        // 3. 清空 buffer 列表
        bufferList.clear();
        log.debug("完成 AOF 持久化到文件");
    }

    @Override
    public long delay() {
        return 1;
    }

    @Override
    public long period() {
        return 1;
    }

    @Override
    public TimeUnit timeUnit() {
        return TimeUnit.SECONDS;
    }

    
    public void append(final String json) {
        if(StringUtils.isNotEmpty(json)) {
            bufferList.add(json);
        }
    }

}
