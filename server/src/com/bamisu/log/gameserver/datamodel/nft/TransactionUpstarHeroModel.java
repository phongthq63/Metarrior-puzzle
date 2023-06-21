package com.bamisu.log.gameserver.datamodel.nft;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.smartfoxserver.v2.entities.Zone;

import java.util.List;

/**
 * Created by Quach Thanh Phong
 * On 3/21/2022 - 11:29 PM
 */
public class TransactionUpstarHeroModel extends DataModel {
    public String txhash;
    public HeroModel HeroModel;
    public List<HeroModel> listHeroModel;

    public static TransactionUpstarHeroModel create(String txhash, HeroModel heroUp, List<HeroModel> listHeroFission, Zone zone) {
        TransactionUpstarHeroModel transactionBurnHeroModel = new TransactionUpstarHeroModel();
        transactionBurnHeroModel.txhash = txhash;
        transactionBurnHeroModel.HeroModel = heroUp;
        transactionBurnHeroModel.listHeroModel = listHeroFission;
        transactionBurnHeroModel.saveToDB(zone);

        return transactionBurnHeroModel;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(txhash, zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static TransactionUpstarHeroModel copyFromDBtoObject(String uId, Zone zone) {
        TransactionUpstarHeroModel pInfo = null;
        try {
            String str = (String) getModel(uId, TransactionUpstarHeroModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, TransactionUpstarHeroModel.class);
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
