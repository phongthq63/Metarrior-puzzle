package com.bamisu.log.sdk.module.nft;

import com.bamisu.log.sdk.module.data.SDKDatacontroler;
import com.bamisu.log.sdk.module.iap.IAPManager;
import com.bamisu.log.sdk.module.iap.model.UserIAPPaymentModel;
import com.bamisu.log.sdk.module.nft.model.TransactionTokenModel;

/**
 * Created by Quach Thanh Phong
 * On 3/11/2022 - 11:42 PM
 */
public class NFTManager {
    private static NFTManager ourInstance = new NFTManager();

    public static NFTManager getInstance() {
        return ourInstance;
    }

    private NFTManager() {
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     * Kiem tra da mua IAP
     * @param transactionHash
     * @return
     */
    public boolean haveInstanceTranferToken(String transactionHash){
        return TransactionTokenModel.copyFromDBtoObject(transactionHash, SDKDatacontroler.getInstance()) != null;
    }

    /**
     * Luu IAP da mua vao data
     * @param transactionHash
     * @param count
     * @param uid
     * @return
     */
    public void saveInstanceTranferToken(String transactionHash, String count, long uid){
        TransactionTokenModel.createTransactionClaimModel(transactionHash, count, uid, SDKDatacontroler.getInstance());
    }
}
