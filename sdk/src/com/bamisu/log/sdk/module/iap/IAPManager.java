package com.bamisu.log.sdk.module.iap;

import com.bamisu.log.sdk.module.data.SDKDatacontroler;
import com.bamisu.log.sdk.module.iap.model.UserIAPPaymentModel;

public class IAPManager {
    private static IAPManager ourInstance = new IAPManager();

    public static IAPManager getInstance() {
        return ourInstance;
    }

    private IAPManager() {
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     * Kiem tra da mua IAP
     * @param accountID
     * @param purchaseToken
     * @return
     */
    public boolean haveInstanceBuyIAP(String accountID, String purchaseToken){
        return UserIAPPaymentModel.copyFromDBtoObject(accountID, purchaseToken, SDKDatacontroler.getInstance()) != null;
    }

    /**
     * Luu IAP da mua vao data
     * @param accountID
     * @param purchaseToken
     * @return
     */
    public void saveInstanceBuyIAP(String accountID, String purchaseToken){
        UserIAPPaymentModel.create(accountID, purchaseToken, SDKDatacontroler.getInstance());
    }
}
