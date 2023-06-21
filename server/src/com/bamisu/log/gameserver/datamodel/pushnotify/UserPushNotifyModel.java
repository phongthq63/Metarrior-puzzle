package com.bamisu.log.gameserver.datamodel.pushnotify;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.campaign.entities.PushNotifyInfo;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create by Popeye on 4:05 PM, 12/28/2020
 */
public class UserPushNotifyModel extends DataModel {
    public long uid;
    public List<PushNotifyInfo> pushNotifyInfoList;

    public UserPushNotifyModel() {
    }

    public UserPushNotifyModel(long uid) {
        this.uid = uid;
        this.pushNotifyInfoList = new ArrayList<>();
    }

    public UserPushNotifyModel(long uid, List<PushNotifyInfo> pushNotifyInfoList) {
        this.uid = uid;
        this.pushNotifyInfoList = pushNotifyInfoList;
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

    public static UserPushNotifyModel copyFromDBtoObject(long uid, Zone zone) {
        UserPushNotifyModel model = null;
        try {
            String str = (String) getModel(String.valueOf(uid), UserPushNotifyModel.class, zone);
            if (str != null) {
                model = Utils.fromJson(str, UserPushNotifyModel.class);
                if (model != null) {
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        if (model == null) {
            model = new UserPushNotifyModel(uid);
            model.saveToDB(zone);
        }

        return model;
    }

    public void update(int platform, String id, Zone zone) {
        for(PushNotifyInfo pushNotifyInfo : pushNotifyInfoList){
            if(pushNotifyInfo.platform == platform){
                if(!pushNotifyInfo.id.equalsIgnoreCase(id)){
                    pushNotifyInfo.id = id;
                    saveToDB(zone);
                }
                return;
            }
        }

        pushNotifyInfoList.add(new PushNotifyInfo(platform, id));
        saveToDB(zone);
    }

    public List<String> getAllKeys() {
        List<String> keys = new ArrayList<>();
        for(PushNotifyInfo pushNotifyInfo : pushNotifyInfoList){
            keys.add(pushNotifyInfo.id);
        }

        return keys;
    }
}
