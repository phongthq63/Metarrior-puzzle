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
public class RankEndlessnightModel extends DataModel {

    public Integer season;

    public Long leagueId;

    public Integer type = 0;

    public List<UserRankInfo> rank = new ArrayList<>();


    public static RankEndlessnightModel createRankEndlessnightModel(int season, long leagueId, int type, Zone zone){
        RankEndlessnightModel rankEndlessnightModel = new RankEndlessnightModel();
        rankEndlessnightModel.season = season;
        rankEndlessnightModel.leagueId = leagueId;
        rankEndlessnightModel.type = type;
        rankEndlessnightModel.saveToDB(zone);

        return rankEndlessnightModel;
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

    public static RankEndlessnightModel copyFromDBtoObject(int season, long leagueId, Zone zone) {
        RankEndlessnightModel pInfo = null;
        try {
            String str = (String) getModel(String.valueOf(season).concat("_").concat(String.valueOf(leagueId)), RankEndlessnightModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, RankEndlessnightModel.class);
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
