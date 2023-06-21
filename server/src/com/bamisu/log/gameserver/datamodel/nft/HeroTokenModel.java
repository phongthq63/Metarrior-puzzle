package com.bamisu.log.gameserver.datamodel.nft;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

/**
 * Created by Quach Thanh Phong
 * On 3/4/2022 - 12:02 AM
 */
public class HeroTokenModel extends DataModel {
    public String hashHero;
    public String txHash;
    public String tokenId;
    public long timestamp;


    public static HeroTokenModel createHeroTokenModel(String hashHero, String txHash, String tokenId, Zone zone) {
        HeroTokenModel heroTokenModel = new HeroTokenModel();
        heroTokenModel.hashHero = hashHero;
        heroTokenModel.txHash = txHash;
        heroTokenModel.tokenId = tokenId;
        heroTokenModel.timestamp = Utils.getTimestampInSecond();
        heroTokenModel.saveToDB(zone);

        return heroTokenModel;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(this.hashHero, zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static HeroTokenModel copyFromDBtoObject(String hashHero, Zone zone) {
        HeroTokenModel pInfo = null;
        try {
            String str = (String) getModel(hashHero, HeroTokenModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, HeroTokenModel.class);
                if (pInfo != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        if (pInfo == null) {
            pInfo = HeroTokenModel.createHeroTokenModel(hashHero, "", "", zone);
        }

        return pInfo;
    }
}
