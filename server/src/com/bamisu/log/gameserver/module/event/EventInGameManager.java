package com.bamisu.log.gameserver.module.event;

import com.bamisu.log.gameserver.datamodel.event.EventManagerModel;
import com.bamisu.log.gameserver.datamodel.event.UserEventDataModel;
import com.bamisu.log.gameserver.datamodel.event.UserEventModel;
import com.bamisu.log.gameserver.datamodel.event.entities.EventDataInfo;
import com.bamisu.log.gameserver.module.IAPBuy.config.IAPPackageConfig;
import com.bamisu.log.gameserver.module.event.config.EventConfig;
import com.bamisu.log.gameserver.module.event.config.entities.EventInGameVO;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.event.defind.EEventInGame;
import com.bamisu.log.gameserver.module.notification.entities.InfoEventNoti;
import com.bamisu.log.gameserver.module.quest.config.QuestChestConfig;
import com.smartfoxserver.v2.entities.Zone;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventInGameManager {

    private EventConfig eventConfig;

    private IAPPackageConfig iapPackageConfig;
    private QuestChestConfig questChestConfig;


    private static EventInGameManager ourInstance = new EventInGameManager();

    public static EventInGameManager getInstance() {
        return ourInstance;
    }

    private EventInGameManager() {
        //Load config
        loadConfig();
    }

    private void loadConfig(){
        eventConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Event.FILE_PATH_CONFIG_EVENT_IN_GAME), EventConfig.class);

        iapPackageConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Event.FILE_PATH_CONFIG_IAP_PACKAGE_EVENT), IAPPackageConfig.class);
        questChestConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Event.FILE_PATH_CONFIG_QUEST_CHEST_EVENT), QuestChestConfig.class);
    }




    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    public EventManagerModel getEventManagerModel(Zone zone){
        return EventManagerModel.copyFromDBtoObject(zone);
    }

    public Map<String,Integer> getEventGeneral(Zone zone){
        return getEventManagerModel(zone).readEvent(zone);
    }

    public boolean addEventGeneral(InfoEventNoti infoEventNoti, Zone zone){
        return getEventManagerModel(zone).addEvent(infoEventNoti, zone);
    }

    public boolean removeEventGeneral(List<String> listId, Zone zone){
        return getEventManagerModel(zone).removeEvent(listId, zone);
    }

    public Map<String,Integer> getEventSpecial(Zone zone){
        return getEventManagerModel(zone).readSpecialEvent(zone);
    }

    public boolean addEventSpecial(InfoEventNoti infoEventNoti, Zone zone){
        return getEventManagerModel(zone).addSpecialEvent(infoEventNoti, zone);
    }

    public boolean removeEventSpecial(List<String> listId, Zone zone){
        return getEventManagerModel(zone).removeSpecialEvent(listId, zone);
    }



    /*-------------------------------------------------- USER --------------------------------------------------------*/
    public UserEventModel getUserEventModel(long uid, Zone zone){
        UserEventModel userEventModel = UserEventModel.copyFromDBtoObject(uid, zone);
        if(userEventModel == null){
            userEventModel = UserEventModel.createUserEventModel(uid, zone);
        }
        return userEventModel;
    }

    public UserEventDataModel getUserEventDataModel(long uid, Zone zone){
        UserEventDataModel userEventDataModel = UserEventDataModel.copyFromDBtoObject(uid, zone);
        if(userEventDataModel == null){
            userEventDataModel = UserEventDataModel.create(uid, zone);
        }
        return userEventDataModel;
    }

    public EventDataInfo getEventData(long uid, Zone zone, EEventInGame event){
        return getUserEventDataModel(uid, zone).readEventDataInfo(event.getId());
    }

    public Map<String,Integer> getListCurrentEvent(long uid, Zone zone){
        Map<String,Integer> mapEvent = new HashMap<>();
        mapEvent.putAll(getUserEventModel(uid, zone).readListEvent());
        mapEvent.putAll(getEventGeneral(zone));
        return mapEvent;
    }




    /*----------------------------------------------------   CONFIG  -------------------------------------------------*/
    /**
     * Lay event config
     * @return
     */
    public List<EventInGameVO> getEventConfig(){
        return eventConfig.list;
    }
    public EventInGameVO getEventConfig(String id){
        for(EventInGameVO event : getEventConfig()){
            if(event.id.equals(id)){
                return event;
            }
        }
        return null;
    }

    public IAPPackageConfig getIapPackageEventConfig(){
        return iapPackageConfig;
    }
    public QuestChestConfig getQuestChestConfig(){
        return questChestConfig;
    }
}
