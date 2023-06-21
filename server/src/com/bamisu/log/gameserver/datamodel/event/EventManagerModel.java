package com.bamisu.log.gameserver.datamodel.event;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.notification.entities.InfoEventNoti;
import com.smartfoxserver.v2.entities.Zone;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EventManagerModel extends DataModel {
    private static final long id = 0;

    public Map<String,Integer> mapEvent = new HashMap<>();
    public Map<String,Integer> mapSpecialEvent = new HashMap<>();

    private Object lockEvent = new Object();



    public static EventManagerModel createEventManagerModel(Zone zone){
        EventManagerModel notificationModel = new EventManagerModel();
        notificationModel.saveToDB(zone);

        return notificationModel;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(id), zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static EventManagerModel copyFromDBtoObject(Zone zone) {
        EventManagerModel pInfo = null;
        try {
            String str = (String) getModel(String.valueOf(id), EventManagerModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, EventManagerModel.class);
                if (pInfo != null) {
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        if(pInfo == null){
            pInfo = createEventManagerModel(zone);
        }

        return pInfo;
    }



    /*----------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------*/
    public Map<String,Integer> readEvent(Zone zone){
        int now = Utils.getTimestampInSecond();
        boolean haveSave = false;

        synchronized (lockEvent){
            Iterator<Map.Entry<String, Integer>> iterator = mapEvent.entrySet().iterator();
            Map.Entry<String, Integer> entry;
            while (iterator.hasNext()) {
                entry = iterator.next();

                if(now >= entry.getValue() && entry.getValue() != -1) {
                    iterator.remove();
                    haveSave = true;
                }
            }
            if(haveSave) saveToDB(zone);

            return mapEvent;
        }
    }

    public boolean addEvent(InfoEventNoti infoEventNoti, Zone zone){
        synchronized (lockEvent){
            mapEvent.put(infoEventNoti.id, infoEventNoti.time);
            return saveToDB(zone);
        }
    }

    public boolean removeEvent(List<String> listId, Zone zone){
        synchronized (lockEvent){
            for(String id : listId){
                mapEvent.remove(id);
            }
            return saveToDB(zone);
        }
    }

    public Map<String,Integer> readSpecialEvent(Zone zone){
        int now = Utils.getTimestampInSecond();
        boolean haveSave = false;

        synchronized (lockEvent){
            Iterator<Map.Entry<String, Integer>> iterator = mapSpecialEvent.entrySet().iterator();
            Map.Entry<String, Integer> entry;
            while (iterator.hasNext()) {
                entry = iterator.next();

                if(now >= entry.getValue() && entry.getValue() != -1) {
                    iterator.remove();
                    haveSave = true;
                }
            }
            if(haveSave) saveToDB(zone);

            return mapSpecialEvent;
        }
    }

    public boolean addSpecialEvent(InfoEventNoti infoEventNoti, Zone zone){
        synchronized (lockEvent){
            mapSpecialEvent.put(infoEventNoti.id, infoEventNoti.time);
            return saveToDB(zone);
        }
    }

    public boolean removeSpecialEvent(List<String> listId, Zone zone){
        synchronized (lockEvent){
            for(String id : listId){
                mapSpecialEvent.remove(id);
            }
            return saveToDB(zone);
        }
    }
}
