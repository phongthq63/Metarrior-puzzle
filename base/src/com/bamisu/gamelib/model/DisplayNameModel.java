package com.bamisu.gamelib.model;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

/**
 * Created by Popeye on 6/29/2017.
 */
public class DisplayNameModel extends DataModel {
    public String dName; //id
    public long userID;

    public DisplayNameModel() {

    }

    public DisplayNameModel(UserModel userModel) {
        dName = userModel.displayName.toLowerCase();
        userID = userModel.userID;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(dName, zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static final DisplayNameModel copyFromDBtoObject(String dName, Zone zone) {
        DisplayNameModel pInfo = null;
        try {
            String str = (String) DataModel.getModel(dName.toLowerCase(), DisplayNameModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, DisplayNameModel.class);
                if (pInfo != null) {
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }

    public static DisplayNameModel create(UserModel userModel, Zone zone) {
        DisplayNameModel d = new DisplayNameModel(userModel);
        if (d.saveToDB(zone)) {
            return d;
        }
        return null;
    }
}
