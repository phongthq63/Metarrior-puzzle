package com.bamisu.log.gameserver.datamodel.luckydraw;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

public class LuckyDrawRewardModel extends DataModel {
    public long uid = 1;
    public double busdTopuser1;
    public double busdTopuser2;
    public double busdTopuser3;
    public double busdTopuser4;
    public double busdTopuser5;
    public double busdTopuser6;
    public int timeStamp;

    public static LuckyDrawRewardModel copyFromDBtoObject(long uId, Zone zone) {
        return copyFromDBtoObject(String.valueOf(1), zone);
    }

    private static LuckyDrawRewardModel copyFromDBtoObject(String uId, Zone zone) {
        LuckyDrawRewardModel pInfo = null;
        try {
            String str = (String) getModel(uId, LuckyDrawRewardModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, LuckyDrawRewardModel.class);
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }

    public static LuckyDrawRewardModel createLuckyDrawReward(long uid, Zone zone) {
        LuckyDrawRewardModel luckyDrawRewardModel = new LuckyDrawRewardModel();
        int time = Utils.getTimestampInSecond();
        luckyDrawRewardModel.uid = 1;
        luckyDrawRewardModel.busdTopuser1 = 0;
        luckyDrawRewardModel.busdTopuser2 = 0;
        luckyDrawRewardModel.busdTopuser3 = 0;
        luckyDrawRewardModel.busdTopuser4 = 0;
        luckyDrawRewardModel.busdTopuser5 = 0;
        luckyDrawRewardModel.busdTopuser6 = 0;
        luckyDrawRewardModel.timeStamp = time;
        luckyDrawRewardModel.saveToDB(zone);
        return luckyDrawRewardModel;
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
