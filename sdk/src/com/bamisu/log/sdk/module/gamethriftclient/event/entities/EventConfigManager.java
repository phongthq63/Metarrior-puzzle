package com.bamisu.log.sdk.module.gamethriftclient.event.entities;

import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;

public class EventConfigManager {
    private static EventConfigManager ourInstance = new EventConfigManager();

    public static EventConfigManager getInstance() {
        return ourInstance;
    }

    private EventConfigManager() {
        loadConfig();
    }

    private void loadConfig() {
        eventConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Event.FILE_PATH_CONFIG_EVENT_IN_GAME), EventConfig.class);
    }


    private EventConfig eventConfig;

    public int getTimeEndEvent(String idEvent){
        return eventConfig.event.getOrDefault(idEvent, Utils.getTimestampInSecond());
    }
}
