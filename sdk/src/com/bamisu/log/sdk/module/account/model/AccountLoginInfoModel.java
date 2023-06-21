package com.bamisu.log.sdk.module.account.model;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.sdk.module.data.SDKDatacontroler;

/**
 * Create by Popeye on 10:49 AM, 10/12/2020
 */
public class AccountLoginInfoModel extends DataModel {
    public String id;
    public int lastLogin = 0;
    public int loginCount = 0;

    public AccountLoginInfoModel() {
    }

    public AccountLoginInfoModel(String accountID) {
        this.id = accountID;
    }

    public boolean save(SDKDatacontroler sdkDatacontroler) {
        try {
            this.saveModel(id, sdkDatacontroler.getController());
            return true;
        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static AccountLoginInfoModel copyFromDB(String accountID, SDKDatacontroler sdkDatacontroler) {
        AccountLoginInfoModel model = null;
        try {
            String str = (String) getModel(accountID, AccountLoginInfoModel.class, sdkDatacontroler.getController());
            if (str != null) {
                model = Utils.fromJson(str, AccountLoginInfoModel.class);
                if (model != null) {
//                    model.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        if(model == null){
            model = new AccountLoginInfoModel(accountID);
        }
        return model;
    }
}
