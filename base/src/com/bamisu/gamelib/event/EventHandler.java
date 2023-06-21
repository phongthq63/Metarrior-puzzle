package com.bamisu.gamelib.event;

import java.util.Map;

/**
 * Create by Popeye on 4:52 PM, 6/20/2020
 */
public abstract class EventHandler {
    public IEventer eventer;

    public EventHandler(IEventer eventer){
        this.eventer = eventer;
    }

    public void register(EEvent event){
        if(eventer != null){
            eventer.registerEvent(this, event);
        }
    }

    public abstract void onEvent(Map<String, Object> data);
}
