package com.bamisu.log.gameserver.module.GameEvent;

import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.GameEvent.entities.IGameEventListener;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.Zone;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.Map;

public abstract class BaseGameEvent implements IGameEventListener {

    protected Zone zone;

    public BaseGameEvent(Zone zone) {
        this.zone = zone;
        initEvent();
    }

    @WithSpan
    @Override
    public void handleGameEvent(EGameEvent event, Map<String, Object> data) {
        if ((int) data.get(Params.ZONE) != zone.getId()) return;
        handleGameEvent(event, (Long) data.get(Params.UID), data);
    }

    public abstract void handleGameEvent(EGameEvent event, long uid, Map<String, Object> data);

    public void registerEvent(EGameEvent event) {
        GameEventFactory.getInstance().handlerRegisterEvent(event, this);
    }

    public void unregisterEvent(EGameEvent event) {
        GameEventFactory.getInstance().handlerUnregisterEvent(event, this);
    }

    public abstract void initEvent();
}
