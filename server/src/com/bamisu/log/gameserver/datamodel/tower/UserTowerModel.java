package com.bamisu.log.gameserver.datamodel.tower;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.ZoneExtension;
import com.smartfoxserver.v2.entities.Zone;

public class UserTowerModel extends DataModel {
    public long uid;
    public short floor;
    public int timeStamp = 0;
    public int lose;

    public static UserTowerModel createUserTowerModel(long uid, Zone zone) {
        UserTowerModel userTowerModel = new UserTowerModel();
        userTowerModel.uid = uid;
        userTowerModel.floor = 1;
        userTowerModel.saveToDB(zone);

        return userTowerModel;
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

    public static UserTowerModel copyFromDBtoObject(long uId, Zone zone) {
        return copyFromDBtoObject(String.valueOf(uId), zone);
    }

    private static UserTowerModel copyFromDBtoObject(String uId, Zone zone) {
        UserTowerModel pInfo = null;
        try {
            String str = (String) getModel(uId, UserTowerModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserTowerModel.class);
                if (pInfo != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }


    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    public int readLose() {
        return lose;
    }

    public boolean increaseLose(Zone zone) {
        lose++;
        return saveToDB(zone);
    }
}
