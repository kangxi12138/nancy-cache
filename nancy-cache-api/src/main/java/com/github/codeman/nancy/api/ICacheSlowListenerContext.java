package com.github.codeman.nancy.api;


public interface ICacheSlowListenerContext {

    
    String methodName();

    
    Object[] params();

    
    Object result();

    
    long startTimeMills();

    
    long endTimeMills();

    
    long costTimeMills();

}
