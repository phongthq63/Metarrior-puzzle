package com.bamisu.gamelib.event;

/**
 * Create by Popeye on 5:17 PM, 6/20/2020
 */
public interface IEventer {
    EventManager getEventManager();
    void registerEvent(EventHandler eventHandler, EEvent event);
}
