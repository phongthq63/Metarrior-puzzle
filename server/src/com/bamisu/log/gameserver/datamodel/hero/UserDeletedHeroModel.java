package com.bamisu.log.gameserver.datamodel.hero;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Quach Thanh Phong
 * On 12/23/2022 - 1:00 AM
 */
public class UserDeletedHeroModel extends DataModel {

    public long uid;
    public List<HeroModel> heroModels = new ArrayList<>();

    private final Object lockHero = new Object();


    public static UserDeletedHeroModel createUserDeletedHeroModel(long uid, Zone zone){
        UserDeletedHeroModel userDeletedHeroModel = new UserDeletedHeroModel();
        userDeletedHeroModel.uid = uid;
        userDeletedHeroModel.saveToDB(zone);

        return userDeletedHeroModel;
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

    public static UserDeletedHeroModel copyFromDBtoObject(long uId, Zone zone) {
        UserDeletedHeroModel userDeletedHeroModel = copyFromDBtoObject(String.valueOf(uId), zone);
        if(userDeletedHeroModel == null){
            userDeletedHeroModel = createUserDeletedHeroModel(uId, zone);
        }
        return userDeletedHeroModel;
    }

    private static UserDeletedHeroModel copyFromDBtoObject(String uId, Zone zone) {
        UserDeletedHeroModel pInfo = null;
        try {
            String str = (String) getModel(uId, UserDeletedHeroModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserDeletedHeroModel.class);
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
}
