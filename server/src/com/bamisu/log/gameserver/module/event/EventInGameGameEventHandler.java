package com.bamisu.log.gameserver.module.event;

import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.module.GameEvent.BaseGameEvent;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.IAPBuy.defind.EIAPClaimType;
import com.bamisu.log.gameserver.module.event.event.black_friday.BlackFridayEventManager;
import com.smartfoxserver.v2.entities.Zone;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.Map;

public class EventInGameGameEventHandler extends BaseGameEvent {

    public EventInGameGameEventHandler(Zone zone) {
        super(zone);
    }

    @WithSpan
    @Override
    public void handleGameEvent(EGameEvent event, long uid, Map<String, Object> data) {
        switch (event){
            case CLAIM_IAP_PACKAGE:
                handlerClaimIAPPackage(uid, data);
                break;
            case CLAIM_IAP_CHALLENGE:
                handlerClaimIAPChallenge(uid, data);
                break;
        }
    }

    @Override
    public void initEvent() {
        this.registerEvent(EGameEvent.CLAIM_IAP_PACKAGE);
        this.registerEvent(EGameEvent.CLAIM_IAP_CHALLENGE);
    }



    /*-----------------------------------------------------------------------------------------------------*/
    /*-----------------------------------------------------------------------------------------------------*/
    @WithSpan
    private void handlerClaimIAPPackage(long uid, Map<String, Object> data){
        String id = String.valueOf(data.getOrDefault(Params.ID, ""));
        if(id.isEmpty()) return;

        //Event black friday
        BlackFridayEventManager.getInstance().sendGiftEvent(uid, id, zone);
    }

    @WithSpan
    private void handlerClaimIAPChallenge(long uid, Map<String, Object> data){
        String id = String.valueOf(data.getOrDefault(Params.ID, ""));
        String claimType = String.valueOf(data.getOrDefault(Params.TYPE, ""));
        if(id.isEmpty()) return;

        switch (EIAPClaimType.fromID(claimType)){
            case ACTIVE_PREDIUM:
                //Event black friday
                BlackFridayEventManager.getInstance().sendGiftEvent(uid, id, zone);
                break;
        }
    }
}
