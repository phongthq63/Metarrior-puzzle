package com.bamisu.log.gameserver.datamodel.luckydraw;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.luckydraw.entities.RankUser;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

public class RankTopUserModel extends DataModel {
    public long uid = 1;
    public int time;
    public List<RankUser> topUserRankCampaign1;
    public List<RankUser> topUserRankCampaign2;
    public List<RankUser> topUserRankDarkrealm1;
    public List<RankUser> topUserRankDarkrealm2;
    public List<RankUser> topUserRankEndlessnight1;
    public List<RankUser> topUserRankEndlessnight2;

    public static RankTopUserModel copyFromDBtoObject(Zone zone) {
        return copyFromDBtoObject(String.valueOf(1), zone);
    }

    public static RankTopUserModel copyFromDBtoObject(String uId, Zone zone) {
        RankTopUserModel pInfo = null;
        try {
            String str = (String) getModel(uId, RankTopUserModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, RankTopUserModel.class);
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
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

    public static RankTopUserModel createRankTopUserModel( Zone zone) {
        RankTopUserModel rankTopUserModel = new RankTopUserModel();
        rankTopUserModel.uid = 1;
        rankTopUserModel.time = Utils.getTimestampInSecond();
        rankTopUserModel.topUserRankCampaign1 = new ArrayList<>();
        rankTopUserModel.topUserRankCampaign2 = new ArrayList<>();
        rankTopUserModel.topUserRankDarkrealm1 = new ArrayList<>();
        rankTopUserModel.topUserRankDarkrealm2 = new ArrayList<>();
        rankTopUserModel.topUserRankEndlessnight1 = new ArrayList<>();
        rankTopUserModel.topUserRankEndlessnight2 = new ArrayList<>();
        rankTopUserModel.saveToDB(zone);
        return rankTopUserModel;
    }
}
