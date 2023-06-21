package com.bamisu.gamelib.event;

import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.task.LizThreadManager;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.util.TaskScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Create by Popeye on 4:51 PM, 6/20/2020
 */
public class EventManager {
    Map<EEvent, List<EventHandler>> mapHandler = new ConcurrentHashMap<>();
    ScheduledExecutorService scheduler = LizThreadManager.getInstance().getFixExecutorServiceByName("server_event", 1);

    public synchronized void regiter(EventHandler eventHandler, EEvent event) {
        if(!mapHandler.containsKey(event)){
            mapHandler.put(event, new ArrayList<>());
        }
        List<EventHandler> list = mapHandler.get(event);
        if(!list.contains(eventHandler)) list.add(eventHandler);
    }

    public void onEvent(Map<String, Object> data){
        EEvent event = (EEvent) data.get(Params.EVENT);
        List<EventHandler> list = mapHandler.get(event);
        if(list != null){
            for(EventHandler handler : list){
                scheduler.schedule(()->{
                    handler.onEvent(data);
                },0, TimeUnit.SECONDS);
            }
        }
    }
}
