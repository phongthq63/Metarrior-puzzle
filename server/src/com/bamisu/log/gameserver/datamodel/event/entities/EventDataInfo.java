package com.bamisu.log.gameserver.datamodel.event.entities;

import com.bamisu.gamelib.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class EventDataInfo {

    public String id;
    public Map<String, Object> data = new HashMap<>();
    public int timeStamp;

    public static EventDataInfo create(String id){
        EventDataInfo data = new EventDataInfo();
        data.id = id;
        data.timeStamp = Utils.getTimestampInSecond();

        return data;
    }


    /*---------------------------------------------- CHRISTMAS -------------------------------------------------------*/
    public int readCountBuyChristmas(String id){
        return (int) data.getOrDefault(id, 0);
    }
    public void updateCountBuyChristmas(String id, int count){
        data.put(id, (int)data.getOrDefault(id, 0) + count);
    }
}
