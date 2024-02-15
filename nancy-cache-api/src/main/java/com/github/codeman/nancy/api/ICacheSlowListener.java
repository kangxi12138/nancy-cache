package com.github.codeman.nancy.api;


public interface ICacheSlowListener {

    
    void listen(final ICacheSlowListenerContext context);

    
    long slowerThanMills();

}
