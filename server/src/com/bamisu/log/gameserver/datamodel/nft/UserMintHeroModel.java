package com.bamisu.log.gameserver.datamodel.nft;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.datamodel.nft.entities.HeroMintModel;
import com.bamisu.log.gameserver.module.hero.define.EHeroType;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Quach Thanh Phong
 * On 2/14/2022 - 8:24 PM
 */
public class UserMintHeroModel extends DataModel {

    public long uid;
    public List<HeroMintModel> listHeroMint = new ArrayList<>();        // Hero model mint chua claim
    public List<HeroModel> listHeroMine = new ArrayList<>();        // Hero model


    public static UserMintHeroModel createUserMintHeroModel (long uid, Zone zone) {
        UserMintHeroModel userMintHeroModel = new UserMintHeroModel();
        userMintHeroModel.uid = uid;
        userMintHeroModel.saveToDB(zone);

        return userMintHeroModel;
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

    public static UserMintHeroModel copyFromDBtoObject(long uId, Zone zone) {
        return copyFromDBtoObject(String.valueOf(uId), zone);
    }

    private static UserMintHeroModel copyFromDBtoObject(String uId, Zone zone) {
        UserMintHeroModel pInfo = null;
        try {
            String str = (String) getModel(uId, UserMintHeroModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserMintHeroModel.class);
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
