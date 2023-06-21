package com.bamisu.log.gameserver.datamodel.league;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.league.entities.UserRankInfo;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Quach Thanh Phong
 * On 1/27/2023 - 12:13 AM
 */
public class RankCampaignModel extends DataModel {

    public Integer season;

    public Long leagueId;

    public Integer type = 0;

    public List<UserRankInfo> rank = new ArrayList<>();


    public static RankCampaignModel createRankCampaignModel(int season, long leagueId, int type, Zone zone){
        RankCampaignModel rankCampaignModel = new RankCampaignModel();
        rankCampaignModel.season = season;
        rankCampaignModel.leagueId = leagueId;
        rankCampaignModel.type = type;
        rankCampaignModel.saveToDB(zone);

        return rankCampaignModel;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(season).concat("_").concat(String.valueOf(leagueId)), zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static RankCampaignModel copyFromDBtoObject(int season, long leagueId, Zone zone) {
        RankCampaignModel pInfo = null;
        try {
            String str = (String) getModel(String.valueOf(season).concat("_").concat(String.valueOf(leagueId)), RankCampaignModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, RankCampaignModel.class);
                if (pInfo != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }

}
