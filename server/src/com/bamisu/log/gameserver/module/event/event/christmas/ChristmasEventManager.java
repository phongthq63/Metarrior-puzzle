package com.bamisu.log.gameserver.module.event.event.christmas;

import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.event.UserEventDataModel;
import com.bamisu.log.gameserver.datamodel.event.entities.EventDataInfo;
import com.bamisu.log.gameserver.entities.EModule;
import com.bamisu.log.gameserver.manager.ServerManager;
import com.bamisu.log.gameserver.module.event.EventInGameManager;
import com.bamisu.log.gameserver.module.event.defind.EActionEvent;
import com.bamisu.log.gameserver.module.event.defind.EEventInGame;
import com.bamisu.log.gameserver.module.event.event.christmas.config.ChristmasConfig;
import com.bamisu.log.gameserver.module.event.event.christmas.config.entities.ExchangeChristmasVO;
import com.smartfoxserver.v2.entities.Zone;

import java.util.List;
import java.util.Map;

public class ChristmasEventManager {

    private ChristmasConfig christmasConfig;


    private static ChristmasEventManager ourInstance = new ChristmasEventManager();

    public static ChristmasEventManager getInstance() {
        return ourInstance;
    }

    private ChristmasEventManager() {
        loadConfig();
    }
    private void loadConfig(){
        christmasConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Event.FILE_PATH_CONFIG_EVENT_CHRISTMAS), ChristmasConfig.class);
    }


    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    public EventDataInfo getDataEventChristmas(long uid, Zone zone){
        return EventInGameManager.getInstance().getEventData(uid, zone, EEventInGame.CHRISSMATE);
    }

    public boolean updateDataEventChristmas(long uid, Zone zone, EActionEvent action, Map<String, Object> data){
        UserEventDataModel userEventDataModel = EventInGameManager.getInstance().getUserEventDataModel(uid, zone);
        EventDataInfo dataEvent = userEventDataModel.readEventDataInfo(EEventInGame.CHRISSMATE.getId());
        switch (action){
            case BUY:
                for(String key : data.keySet()){
                    dataEvent.updateCountBuyChristmas(key, (Integer) data.getOrDefault(key, 0));
                }
                break;
        }
        return userEventDataModel.saveToDB(zone);
    }









    /*----------------------------------------------------------------------------------------------------------------*/
    public int getTimeEndEvent(Zone zone){
        return EventInGameManager.getInstance().getEventGeneral(zone).getOrDefault(EEventInGame.CHRISSMATE.getId(), 0);
    }

    public boolean isTimeEndEvent(Zone zone){
        int timeEnd = getTimeEndEvent(zone);
        if(timeEnd < 0) return false;
        return timeEnd < Utils.getTimestampInSecond();
    }



    /*------------------------------------------------- CONFIG -------------------------------------------------------*/
    public List<ExchangeChristmasVO> getShopChristmasConfig(){
        return christmasConfig.exchange;
    }

    public ExchangeChristmasVO getShopChristmasConfig(String id){
        for(ExchangeChristmasVO index : getShopChristmasConfig()){
            if(index.id.equals(id)){
                return index;
            }
        }
        return null;
    }
}
