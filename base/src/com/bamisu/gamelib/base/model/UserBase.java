package com.bamisu.gamelib.base.model;

/**
 * Created by Popeye on 4/21/2017.
 */

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.datacontroller.ZoneDatacontroler;
import com.bamisu.gamelib.base.datacontroller.couchbase.CouchbaseDataController;
import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.datacontroller.ZoneDatacontroler;
import com.bamisu.gamelib.base.datacontroller.couchbase.CouchbaseDataController;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

public class UserBase extends DataModel {
    public static final String USER_ID_INCR_NAME = "INCR_USER_ID";
    public static final long MIN_USER_ID = 10000;

    public String accountID;
    public long userID;
    public int createTime = 0;

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(this.accountID, zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static UserBase create(String accountID, Zone zone, int serverID) {
        long minID = serverID * 10000000;
        UserBase userBase = new UserBase();
        userBase.accountID = accountID;

        ZoneDatacontroler zoneDatacontroler = ((BaseExtension) zone.getExtension()).getDataController();

        try {
            if (zoneDatacontroler.getController() instanceof CouchbaseDataController) {
                long tmp = ((CouchbaseDataController) zoneDatacontroler.getController()).getClient().incr(UserBase.USER_ID_INCR_NAME, 1, 1L);
                if (tmp < minID) {
                    ((CouchbaseDataController) zoneDatacontroler.getController()).getClient().incr(UserBase.USER_ID_INCR_NAME, minID - tmp - 1, 1L);
                }
                tmp = ((CouchbaseDataController) zoneDatacontroler.getController()).getClient().incr(UserBase.USER_ID_INCR_NAME, 1, 1L);
                while (UserModel.copyFromDBtoObject(tmp, zone) != null) {
                    tmp = ((CouchbaseDataController) zoneDatacontroler.getController()).getClient().incr(UserBase.USER_ID_INCR_NAME, 1, 1L);
                }
                userBase.userID = tmp;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        userBase.createTime = Utils.getTimestampInSecond();
        return userBase;

    }

    public static UserBase copyFromDBtoObject(String socialId, Zone zone) {
        UserBase userBase = null;
        try {
            String str = (String) DataModel.getModel(socialId, UserBase.class, zone);
            if (str != null) {
                userBase = Utils.fromJson(str, UserBase.class);
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return userBase;
    }

}
