package com.bamisu.log.gameserver.datamodel.hero;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Quach Thanh Phong
 * On 2/27/2022 - 4:57 AM
 */
public class UserBlockHeroModel extends DataModel {

    public long uid;
    public List<HeroModel> listHeroModel = new ArrayList<>();



    public static UserBlockHeroModel createUserBlockHeroModel(long uid, Zone zone){
        UserBlockHeroModel userBlockHeroModel = new UserBlockHeroModel();
        userBlockHeroModel.uid = uid;
        userBlockHeroModel.saveToDB(zone);

        return userBlockHeroModel;
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

    public static UserBlockHeroModel copyFromDBtoObject(long uId, Zone zone) {
        UserBlockHeroModel userAllHeroModel = copyFromDBtoObject(String.valueOf(uId), zone);
        if(userAllHeroModel == null){
            userAllHeroModel = createUserBlockHeroModel(uId, zone);
        }
        return userAllHeroModel;
    }

    private static UserBlockHeroModel copyFromDBtoObject(String uId, Zone zone) {
        UserBlockHeroModel pInfo = null;
        try {
            String str = (String) getModel(uId, UserBlockHeroModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserBlockHeroModel.class);
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
