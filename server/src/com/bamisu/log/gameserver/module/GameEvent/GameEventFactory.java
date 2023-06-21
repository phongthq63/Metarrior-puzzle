package com.bamisu.log.gameserver.module.GameEvent;

import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.task.LizThreadManager;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class GameEventFactory {
    private ExecutorService executor;
    private Map<EGameEvent, List<BaseGameEvent>> mapEvent = new ConcurrentHashMap<>();

    private static GameEventFactory ourInstance = new GameEventFactory();

    public static GameEventFactory getInstance() {
        return ourInstance;
    }

    private GameEventFactory() {
        executor = LizThreadManager.getInstance().getFixExecutorServiceByName("game_event", 2);
    }

    @WithSpan
    private void onGameEvent(EGameEvent event, Map<String, Object> data) {
        if (mapEvent.containsKey(event)) {
            mapEvent.get(event).parallelStream().forEach(trigger -> {
                executor.execute(() -> {
                    try {
                        trigger.handleGameEvent(event, data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            });
        }
    }

    @WithSpan
    public void onGameEvent(EGameEvent event, long uid, Map<String, Object> data, Zone zone) {
        data.put(Params.UID, uid);
        data.put(Params.ZONE, zone.getId());
        onGameEvent(event, data);
    }

    public void handlerRegisterEvent(EGameEvent event, BaseGameEvent baseGameEvent) {
        if (!mapEvent.containsKey(event)) mapEvent.put(event, new ArrayList<>());
        mapEvent.get(event).add(baseGameEvent);
    }

    public void handlerUnregisterEvent(EGameEvent event, BaseGameEvent baseGameEvent) {
        if (!mapEvent.containsKey(event)) return;
        List<BaseGameEvent> obj = mapEvent.get(event);
        obj.add(baseGameEvent);
    }
}
