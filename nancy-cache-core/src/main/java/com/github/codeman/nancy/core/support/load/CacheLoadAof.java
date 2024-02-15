package com.github.codeman.nancy.core.support.load;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.github.codeman.nancy.annotation.CacheInterceptor;
import com.github.codeman.nancy.api.ICache;
import com.github.codeman.nancy.api.ICacheLoad;
import com.github.codeman.nancy.core.core.Cache;
import com.github.codeman.nancy.core.model.PersistAofEntry;
import com.github.codeman.nancy.core.util.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class CacheLoadAof<K,V> implements ICacheLoad<K,V> {

    private static final Logger log = LoggerFactory.getLogger(CacheLoadAof.class);

    
    private static final Map<String, Method> METHOD_MAP = new HashMap<>();

    static {
        Method[] methods = Cache.class.getMethods();

        for(Method method : methods){
            CacheInterceptor cacheInterceptor = method.getAnnotation(CacheInterceptor.class);

            if(cacheInterceptor != null) {
                // 暂时
                if(cacheInterceptor.aof()) {
                    String methodName = method.getName();

                    METHOD_MAP.put(methodName, method);
                }
            }
        }

    }

    
    private final String dbPath;

    public CacheLoadAof(String dbPath) {
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
            //复杂的这种反序列化会失败
            PersistAofEntry entry = JSON.parseObject(line, PersistAofEntry.class);

            final String methodName = entry.getMethodName();
            final Object[] objects = entry.getParams();

            final Method method = METHOD_MAP.get(methodName);


            try {
                method.invoke(cache, objects);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
