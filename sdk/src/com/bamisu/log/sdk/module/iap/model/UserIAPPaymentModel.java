package com.bamisu.log.sdk.module.iap.model;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.sdk.module.data.SDKDatacontroler;
import com.smartfoxserver.v2.entities.Zone;

public class UserIAPPaymentModel extends DataModel {

    public String accountID;
    public String purchaseToken;



    public static UserIAPPaymentModel create(String accountID, String purchaseToken, SDKDatacontroler sdkDatacontroler){
        UserIAPPaymentModel userIAPPaymentModel = new UserIAPPaymentModel();
        userIAPPaymentModel.accountID = accountID;
        userIAPPaymentModel.purchaseToken = purchaseToken;
        userIAPPaymentModel.saveToDB(sdkDatacontroler);

        return userIAPPaymentModel;
    }

    public final boolean saveToDB(SDKDatacontroler sdkDatacontroler) {
        try {
            saveModel(purchaseToken, sdkDatacontroler.getController());
            return true;
        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static UserIAPPaymentModel copyFromDBtoObject(String accountID, String purchaseToken, SDKDatacontroler sdkDatacontroler) {
        if(purchaseToken == null) return null;
        UserIAPPaymentModel pInfo = null;
        try {
            String str = (String) getModel(purchaseToken, UserIAPPaymentModel.class, sdkDatacontroler.getController());
            if (str != null) {
                pInfo = Utils.fromJson(str, UserIAPPaymentModel.class);
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
