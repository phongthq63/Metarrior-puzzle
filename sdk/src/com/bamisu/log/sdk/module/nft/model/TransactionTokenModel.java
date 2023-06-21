package com.bamisu.log.sdk.module.nft.model;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.sdk.module.data.SDKDatacontroler;
import com.smartfoxserver.v2.entities.Zone;

/**
 * Created by Quach Thanh Phong
 * On 3/6/2022 - 3:55 AM
 */
public class TransactionTokenModel extends DataModel {
    public String transactionHash;
    public String count;
    public long from;


    public static TransactionTokenModel createTransactionClaimModel(String transactionHash, String count, long uid, SDKDatacontroler sdkDatacontroler) {
        TransactionTokenModel transactionTokenModel = new TransactionTokenModel();
        transactionTokenModel.transactionHash = transactionHash;
        transactionTokenModel.count = count;
        transactionTokenModel.from = uid;
        transactionTokenModel.saveToDB(sdkDatacontroler);

        return transactionTokenModel;
    }

    public final boolean saveToDB(SDKDatacontroler sdkDatacontroler) {
        try {
            saveModel(this.transactionHash, sdkDatacontroler.getController());
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static TransactionTokenModel copyFromDBtoObject(String hash, SDKDatacontroler sdkDatacontroler) {
        TransactionTokenModel pInfo = null;
        try {
            String str = (String) getModel(hash, TransactionTokenModel.class, sdkDatacontroler.getController());
            if (str != null) {
                pInfo = Utils.fromJson(str, TransactionTokenModel.class);
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
