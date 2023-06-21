package com.bamisu.log.gameserver.datamodel.nft;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.nft.entities.TokenTransactionModel;
import com.smartfoxserver.v2.entities.Zone;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Quach Thanh Phong
 * On 5/3/2022 - 2:10 PM
 */
public class UserTokenClaimModel extends DataModel {

    public long uid;
    public Map<String, TokenTransactionModel> mapTransaction = new HashMap<>();



    public static UserTokenClaimModel createUseTokenTransactionModel(long uid, Zone zone) {
        UserTokenClaimModel useTokenTransactionModel = new UserTokenClaimModel();
        useTokenTransactionModel.uid = uid;
        useTokenTransactionModel.saveToDB(zone);

        return useTokenTransactionModel;
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

    public static UserTokenClaimModel copyFromDBtoObject(long uId, Zone zone) {
        return copyFromDBtoObject(String.valueOf(uId), zone);
    }

    private static UserTokenClaimModel copyFromDBtoObject(String uId, Zone zone) {
        UserTokenClaimModel pInfo = null;
        try {
            String str = (String) getModel(uId, UserTokenClaimModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserTokenClaimModel.class);
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
    public TokenTransactionModel getTokenTransactionModel(String transactionId) {
        return this.mapTransaction.get(transactionId);
    }

    public boolean saveTransaction(String transactionId, TokenTransactionModel model, Zone zone) {
        this.mapTransaction.put(transactionId, model);
        return saveToDB(zone);
    }

    public boolean clearTransaction(String transactionId, Zone zone) {
        TokenTransactionModel tokenTransactionModel = this.mapTransaction.remove(transactionId);
        if (tokenTransactionModel == null) return false;
        return saveToDB(zone);
    }

    public boolean clearTransaction(int amount, String id, Zone zone) {
        if (this.mapTransaction.size() == 0) {
            return false;
        }

        for (Map.Entry<String, TokenTransactionModel> entry : this.mapTransaction.entrySet()) {
            ResourcePackage resourcePackage = entry.getValue().tokens.get(0);
            if (resourcePackage == null) return false;
            if (resourcePackage.id.equalsIgnoreCase(id) && resourcePackage.amount == amount) {
                this.mapTransaction.remove(entry.getKey());
                return saveToDB(zone);
            }
        }

        return false;
    }
}
