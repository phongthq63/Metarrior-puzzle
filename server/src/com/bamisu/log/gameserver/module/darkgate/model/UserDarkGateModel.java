package com.bamisu.log.gameserver.module.darkgate.model;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.darkgate.model.entities.UserDarkRealmInfo;
import com.bamisu.log.gameserver.module.darkgate.model.entities.UserEndlessNightInfo;
import com.smartfoxserver.v2.entities.Zone;

/**
 * Create by Popeye on 10:24 AM, 11/17/2020
 */
public class UserDarkGateModel extends DataModel {
    public long uid;
    public UserDarkRealmInfo userDarkRealmInfo = new UserDarkRealmInfo(2, Utils.getTimestampInSecond());
    public UserEndlessNightInfo userEndlessNightInfo = new UserEndlessNightInfo(2, Utils.getTimestampInSecond());

    public UserDarkGateModel() {
    }

    public UserDarkGateModel(long uid) {
        this.uid = uid;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(uid + "", zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static UserDarkGateModel copyFromDBtoObject(long uid, Zone zone) {
        UserDarkGateModel model = null;
        try {
            String str = (String) getModel(uid + "", UserDarkGateModel.class, zone);
            if (str != null) {
                model = Utils.fromJson(str, UserDarkGateModel.class);
                if (model != null) {
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        if(model == null){
            model = new UserDarkGateModel(uid);
            model.saveToDB(zone);
        }

        return model;
    }

    public boolean canFightDarkRealm(Zone zone){
        return getDarkRealmTurn(zone) > 0;
    }

    public boolean fightDarkRealm(Zone zone){
//        if(getDarkRealmTurn(zone) < 1) return false;
        userDarkRealmInfo.turn --;
        saveToDB(zone);
        return true;
    }

    public boolean canFightEndlessNight(Zone zone){
        return getEndlessNightTurn(zone) > 0;
    }

    public boolean fightEndlessNight(Zone zone){
//        if(getEndlessNightTurn(zone) < 1) return false;
        userEndlessNightInfo.turn --;
        saveToDB(zone);
        return true;
    }

    public int getDarkRealmTurn(Zone zone){
        if(Utils.isNewDay(userDarkRealmInfo.lastCheckTurn)){
            userDarkRealmInfo.turn = 2;
            userDarkRealmInfo.lastCheckTurn = Utils.getTimestampInSecond();
            saveToDB(zone);
        }else { //sang ngày mới

        }
        return userDarkRealmInfo.turn;
    }

    public int getEndlessNightTurn(Zone zone){
        if(Utils.isNewDay(userEndlessNightInfo.lastCheckTurn)){
            userEndlessNightInfo.turn = 2;
            userEndlessNightInfo.lastCheckTurn = Utils.getTimestampInSecond();
            saveToDB(zone);
        }else { //sang ngày mới

        }
        return userEndlessNightInfo.turn;
    }
}
