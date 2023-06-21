package com.bamisu.log.gameserver.module.GameEvent.entities;

import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;

import java.util.Map;

public interface IGameEventListener {

    void handleGameEvent(EGameEvent event, Map<String,Object> data);
}
