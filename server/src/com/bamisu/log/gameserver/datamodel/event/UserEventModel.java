package com.bamisu.log.gameserver.datamodel.event;

import com.bamisu.log.gameserver.module.IAP.TimeUtils;
import com.bamisu.log.gameserver.module.IAP.defind.ETimeType;
import com.bamisu.log.gameserver.module.event.EventInGameManager;
import com.bamisu.log.gameserver.module.event.config.entities.EventInGameVO;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UserEventModel extends DataModel {

    public long uid;
    public Map<String,Integer> mapEvent = new HashMap<>();



    private void initEvent(long uid, Zone zone){
        int timeCurrent = Utils.getTimestampInSecond();

        for(EventInGameVO event : EventInGameManager.getInstance().getEventConfig()){
            mapEvent.put(event.id, timeCurrent);
        }
        GrandOpeningCheckInModel.copyFromDBtoObject(uid, zone);
    }

    public static UserEventModel createUserEventModel(long uid, Zone zone){
        UserEventModel userEventModel = new UserEventModel();
        userEventModel.uid = uid;
        userEventModel.initEvent(uid, zone);
        userEventModel.saveToDB(zone);

        return userEventModel;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(this.uid), zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static UserEventModel copyFromDBtoObject(long uId, Zone zone) {
        UserEventModel userEventModel = copyFromDBtoObject(String.valueOf(uId), zone);
        if(userEventModel != null){
            return userEventModel;
        }
        return createUserEventModel(uId, zone);
    }

    private static UserEventModel copyFromDBtoObject(String uId, Zone zone) {
        UserEventModel pInfo = null;
        try {
            String str = (String) getModel(uId, UserEventModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserEventModel.class);
                if (pInfo != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }




    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    public Map<String,Integer> readListEvent(){
        return mapEvent.entrySet().parallelStream().
                filter(obj -> {
                    int time = TimeUtils.getDeltaTimeToTime(ETimeType.fromID(EventInGameManager.getInstance().getEventConfig(obj.getKey()).timeEnd), obj.getValue());
                    return time == -1 || time > 0;}).
                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public boolean checkEndEvent(String idEvent){
        return TimeUtils.isTimeTo(ETimeType.fromID(EventInGameManager.getInstance().getEventConfig(idEvent).timeEnd), mapEvent.get(idEvent));
    }
}
