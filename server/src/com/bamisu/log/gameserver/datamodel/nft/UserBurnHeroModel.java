package com.bamisu.log.gameserver.datamodel.nft;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.nft.entities.HeroUpstarBurn;
import com.smartfoxserver.v2.entities.Zone;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Quach Thanh Phong
 * On 3/23/2022 - 8:23 PM
 */
public class UserBurnHeroModel extends DataModel {

    public long uid;
    public Map<String, HeroUpstarBurn> mapUpstar = new HashMap<>();



    public static UserBurnHeroModel createUserBurnHeroModel(long uid, Zone zone) {
        UserBurnHeroModel userBurnHeroModel = new UserBurnHeroModel();
        userBurnHeroModel.uid = uid;
        userBurnHeroModel.saveToDB(zone);

        return userBurnHeroModel;
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

    public static UserBurnHeroModel copyFromDBtoObject(long uid, Zone zone) {
        UserBurnHeroModel userBurnHeroModel = copyFromDBtoObject(String.valueOf(uid), zone);
        if (userBurnHeroModel == null) {
            userBurnHeroModel = createUserBurnHeroModel(uid, zone);
        }
        return userBurnHeroModel;
    }

    private static UserBurnHeroModel copyFromDBtoObject(String uId, Zone zone) {
        UserBurnHeroModel pInfo = null;
        try {
            String str = (String) getModel(uId, UserBurnHeroModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserBurnHeroModel.class);
                if (pInfo != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        return pInfo;
    }

    public boolean saveHeroUpstar(HeroUpstarBurn heroBurnModel, Zone zone) {
        this.mapUpstar.put(heroBurnModel.heroModel.hash, heroBurnModel);
        return saveToDB(zone);
    }
}
