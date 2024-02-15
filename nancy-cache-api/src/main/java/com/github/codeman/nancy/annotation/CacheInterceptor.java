package com.github.codeman.nancy.annotation;

import java.lang.annotation.*;

/**
 * 缓存拦截器
 * @author codeman
 */
@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheInterceptor {


    boolean common() default true;


    boolean refresh() default false;


    boolean aof() default false;


    boolean evict() default false;

}
