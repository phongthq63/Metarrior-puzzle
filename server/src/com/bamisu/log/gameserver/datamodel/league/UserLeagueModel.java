package com.bamisu.log.gameserver.datamodel.league;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

/**
 * Created by Quach Thanh Phong
 * On 5/22/2023 - 12:05 AM
 */
public class UserLeagueModel extends DataModel {

    public Long uid;

    public Integer season;

    public boolean firstFight = true;

    public static UserLeagueModel createUserLeagueModel(long uid, int season, Zone zone){
        UserLeagueModel userLeagueModel = new UserLeagueModel();
        userLeagueModel.uid = uid;
        userLeagueModel.season = season;
        userLeagueModel.saveToDB(zone);

        return userLeagueModel;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(uid).concat("_").concat(String.valueOf(season)), zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static UserLeagueModel copyFromDBtoObject(long uid, int season, Zone zone) {
        UserLeagueModel pInfo = null;
        try {
            String str = (String) getModel(String.valueOf(uid).concat("_").concat(String.valueOf(season)), RankEndlessnightModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserLeagueModel.class);
                if (pInfo != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        if (pInfo == null) {
            pInfo = UserLeagueModel.createUserLeagueModel(uid, season, zone);
        }

        return pInfo;
    }

}
