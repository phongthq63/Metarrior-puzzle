package com.bamisu.log.gameserver.datamodel.luckydraw;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.luckydraw.entities.TotalBusdUserInSeason;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

public class TotalBusdOfUserInSeasonModel extends DataModel {
    public long uid = 1;
    public int time;
    public List<TotalBusdUserInSeason> listTotalBusdUserInSeason;

    public static TotalBusdOfUserInSeasonModel copyFromDBtoObject(Zone zone) {
        return copyFromDBtoObject(String.valueOf(1), zone);
    }

    public static TotalBusdOfUserInSeasonModel copyFromDBtoObject(String uId, Zone zone) {
        TotalBusdOfUserInSeasonModel pInfo = null;
        try {
            String str = (String) getModel(uId, TotalBusdOfUserInSeasonModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, TotalBusdOfUserInSeasonModel.class);
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

    public static TotalBusdOfUserInSeasonModel createTotalBusdOfUsers(Zone zone) {
        TotalBusdOfUserInSeasonModel tbouism = new TotalBusdOfUserInSeasonModel();
        tbouism.uid = 1;
        tbouism.time = Utils.getTimestampInSecond();
        tbouism.listTotalBusdUserInSeason = new ArrayList<>();
        tbouism.saveToDB(zone);
        return tbouism;
    }

    public static TotalBusdOfUserInSeasonModel createHistoryUser(List<TotalBusdUserInSeason> list, Zone zone) {
        TotalBusdOfUserInSeasonModel tbouism = new TotalBusdOfUserInSeasonModel();
        tbouism.listTotalBusdUserInSeason = list;
        tbouism.saveToDB(zone);
        return tbouism;
    }
}
