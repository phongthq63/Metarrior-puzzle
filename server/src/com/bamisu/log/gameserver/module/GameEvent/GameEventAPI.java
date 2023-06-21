package com.bamisu.log.gameserver.module.GameEvent;

import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.smartfoxserver.v2.entities.Zone;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.Map;

public class GameEventAPI {

    @WithSpan
    public static void ariseGameEvent(EGameEvent event, long uid, Map<String,Object> data, Zone zone){
        GameEventFactory.getInstance().onGameEvent(event, uid, data, zone);
    }
}
