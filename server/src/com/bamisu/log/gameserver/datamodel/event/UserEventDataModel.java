package com.bamisu.log.gameserver.datamodel.event;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.event.entities.EventDataInfo;
import com.smartfoxserver.v2.entities.Zone;

import java.util.HashMap;
import java.util.Map;

public class UserEventDataModel extends DataModel {

    public long uid;
    public Map<String, EventDataInfo> data = new HashMap<>();




    public static UserEventDataModel create(long uid, Zone zone){
        UserEventDataModel userEventDataModel = new UserEventDataModel();
        userEventDataModel.uid = uid;

        return userEventDataModel;
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

    public static UserEventDataModel copyFromDBtoObject(long uId, Zone zone) {
        UserEventDataModel userEventDataModel = copyFromDBtoObject(String.valueOf(uId), zone);
        if(userEventDataModel != null){
            return userEventDataModel;
        }
        return create(uId, zone);
    }

    private static UserEventDataModel copyFromDBtoObject(String uId, Zone zone) {
        UserEventDataModel pInfo = null;
        try {
            String str = (String) getModel(uId, UserEventDataModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserEventDataModel.class);
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
    public EventDataInfo readEventDataInfo(String idEvent){
        EventDataInfo dataInfo = data.get(idEvent);
        if(dataInfo == null){
            data.put(idEvent, EventDataInfo.create(idEvent));
        }
        return data.get(idEvent);
    }
}
