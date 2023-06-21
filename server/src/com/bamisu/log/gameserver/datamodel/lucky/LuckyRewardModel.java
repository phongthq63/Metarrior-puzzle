package com.bamisu.log.gameserver.datamodel.lucky;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

public class LuckyRewardModel extends DataModel {
    public long uid = 2;
    public long mewa;
    public long sog;
    public int timeStamp;

    public static LuckyRewardModel copyFromDBtoObject(long uId, Zone zone) {
        return copyFromDBtoObject(String.valueOf(2), zone);
    }

    private static LuckyRewardModel copyFromDBtoObject(String uId, Zone zone) {
        LuckyRewardModel pInfo = null;
        try {
            String str = (String) getModel(uId, LuckyRewardModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, LuckyRewardModel.class);
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }

    public static LuckyRewardModel createRewardModel(long uid, Zone zone) {
        LuckyRewardModel userHisModel = new LuckyRewardModel();
        int time = Utils.getTimestampInSecond();
        userHisModel.uid = 2;
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
