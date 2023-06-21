package com.bamisu.log.gameserver.datamodel.lucky;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.lucky.entities.LuckyInfo;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

public class LuckyModel extends DataModel {
    public long uid = 1;
    public long mewa;
    public long sog;
    public int timeStamp;

    public static LuckyModel copyFromDBtoObject(long uId, Zone zone) {
        return copyFromDBtoObject(String.valueOf(1), zone);
    }

    private static LuckyModel copyFromDBtoObject(String uId, Zone zone) {
        LuckyModel pInfo = null;
        try {
            String str = (String) getModel(uId, LuckyModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, LuckyModel.class);
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }

    public static LuckyModel createJackpot(long uid, Zone zone) {
        LuckyModel userHisModel = new LuckyModel();
        int time = Utils.getTimestampInSecond();
        userHisModel.uid = 1;
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
