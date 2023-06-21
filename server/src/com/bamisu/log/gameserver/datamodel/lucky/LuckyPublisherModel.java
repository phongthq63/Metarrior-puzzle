package com.bamisu.log.gameserver.datamodel.lucky;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

public class LuckyPublisherModel extends DataModel {
    public long uid = 3;
    public long mewa;
    public long sog;
    public int timeStamp;

    public static LuckyPublisherModel copyFromDBtoObject(long uId, Zone zone) {
        return copyFromDBtoObject(String.valueOf(3), zone);
    }

    private static LuckyPublisherModel copyFromDBtoObject(String uId, Zone zone) {
        LuckyPublisherModel pInfo = null;
        try {
            String str = (String) getModel(uId, LuckyPublisherModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, LuckyPublisherModel.class);
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }

    public static LuckyPublisherModel createPublisherModel(long uid, Zone zone) {
        LuckyPublisherModel userHisModel = new LuckyPublisherModel();
        int time = Utils.getTimestampInSecond();
        userHisModel.uid = 3;
        userHisModel.mewa = 0;
        userHisModel.sog = 0;
        userHisModel.timeStamp = time;
        userHisModel.saveToDB(zone);
        return userHisModel;
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
}
