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
public class RankDarkrealmModel extends DataModel {

    public Integer season;

    public Long leagueId;

    public Integer type = 0;

    public List<UserRankInfo> rank = new ArrayList<>();


    public static RankDarkrealmModel createRankDarkrealmModel(int season, long leagueId, int type, Zone zone){
        RankDarkrealmModel rankDarkrealmModel = new RankDarkrealmModel();
        rankDarkrealmModel.season = season;
        rankDarkrealmModel.leagueId = leagueId;
        rankDarkrealmModel.type = type;
        rankDarkrealmModel.saveToDB(zone);

        return rankDarkrealmModel;
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

    public static RankDarkrealmModel copyFromDBtoObject(int season, long leagueId, Zone zone) {
        RankDarkrealmModel pInfo = null;
        try {
            String str = (String) getModel(String.valueOf(season).concat("_").concat(String.valueOf(leagueId)), RankDarkrealmModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, RankDarkrealmModel.class);
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
