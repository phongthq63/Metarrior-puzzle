package com.bamisu.log.gameserver.datamodel.lucky;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.lucky.entities.LuckyInfo;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

public class LuckyUserModel extends DataModel {
    public long uid;
    public List<LuckyInfo> history = new ArrayList<>();
    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(this.uid), zone);
            return true;
        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static LuckyUserModel copyFromDBtoObject(long uId, Zone zone) {
        return copyFromDBtoObject(String.valueOf(uId), zone);
    }

    private static LuckyUserModel copyFromDBtoObject(String uId, Zone zone) {
        LuckyUserModel pInfo = null;
        try {
            String str = (String) getModel(uId, LuckyUserModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, LuckyUserModel.class);
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }

    public static LuckyUserModel createUserHistory(long uid, Zone zone) {
        LuckyUserModel userHisModel = new LuckyUserModel();
        userHisModel.uid = uid;
        userHisModel.history = new ArrayList<>();
        userHisModel.saveToDB(zone);
        return userHisModel;
    }

}
