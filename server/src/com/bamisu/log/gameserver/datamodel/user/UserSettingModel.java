package com.bamisu.log.gameserver.datamodel.user;

import com.bamisu.log.gameserver.datamodel.user.entities.PushNotificationSetting;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

/**
 * Create by Popeye on 4:20 PM, 4/15/2020
 */
public class UserSettingModel extends DataModel {
    public long uid;
    public PushNotificationSetting pushNotificationSetting = new PushNotificationSetting();

    public UserSettingModel() {
    }

    public UserSettingModel(long uid) {
        this.uid = uid;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(uid), zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static final UserSettingModel copyFromDBtoObject(String uid, Zone zone) {
        return copyFromDBtoObject(Long.parseLong(uid), zone);

    }

    public static final UserSettingModel copyFromDBtoObject(long uid, Zone zone) {
        UserSettingModel model = null;
        try {
            String str = (String) DataModel.getModel(String.valueOf(uid), UserSettingModel.class, zone);
            if (str != null) {
                model = Utils.fromJson(str, UserSettingModel.class);
                if (model != null) {
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        if(model == null){
            model = new UserSettingModel(uid);
            model.saveToDB(zone);
        }
        return model;
    }

}
