package com.bamisu.log.gameserver.datamodel.campaign;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.sql.game.dbo.RankLeagueDBO;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.IAP.TimeUtils;
import com.bamisu.log.gameserver.module.IAP.defind.ETimeType;
import com.bamisu.log.gameserver.sql.rank.dao.RankDAO;
import com.smartfoxserver.v2.entities.Zone;

import java.util.Objects;

/**
 * Created by Quach Thanh Phong
 * On 5/27/2022 - 12:42 AM
 */
public class UserCampaignRankModel extends DataModel {

    public long uid;
    public int countFight = 0;
    public int timestamp;

    private final Object lockSeason = new Object();


    public static UserCampaignRankModel createUserCampaignRankModel(long uid, Zone zone) {
        UserCampaignRankModel userCampaignRankModel = new UserCampaignRankModel();
        userCampaignRankModel.uid = uid;
        userCampaignRankModel.timestamp = Utils.getTimestampInSecond();
        userCampaignRankModel.saveToDB(zone);

        return userCampaignRankModel;
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

    public static UserCampaignRankModel copyFromDBtoObject(long uid, Zone zone) {
        UserCampaignRankModel userCampaignRankModel = copyFromDBtoObject(String.valueOf(uid), zone);
        if (userCampaignRankModel == null) {
            userCampaignRankModel = UserCampaignRankModel.createUserCampaignRankModel(uid, zone);
        }
        userCampaignRankModel.updateNewSeason(zone);

        return userCampaignRankModel;
    }

    public static UserCampaignRankModel copyFromDBtoObject(String uId, Zone zone) {
        UserCampaignRankModel pInfo = null;
        try {
            String str = (String) getModel(uId, UserCampaignRankModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserCampaignRankModel.class);
                if (pInfo != null) {

                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        return pInfo;
    }

    public boolean updateNewSeason(Zone zone){
        synchronized (lockSeason){
            if (TimeUtils.isTimeTo(ETimeType.NEW_WEEK, timestamp)) {
                countFight = 0;
                timestamp = Utils.getTimestampInSecond();
                return saveToDB(zone);
            }
            return true;
        }
    }

    public boolean updateCountFight(Zone zone){
        countFight = countFight + 1;
        return saveToDB(zone);
    }
}
