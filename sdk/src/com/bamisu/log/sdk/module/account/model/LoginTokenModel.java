package com.bamisu.log.sdk.module.account.model;

import com.bamisu.log.sdk.module.data.SDKDatacontroler;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

/**
 * Create by Popeye on 8:23 PM, 4/23/2020
 */
public class LoginTokenModel extends DataModel {
    public String token;
    public String accountID;

    public LoginTokenModel() {

    }

    public LoginTokenModel(String accountID) {
        this.accountID = accountID;
        this.token = Utils.genToken();
    }

    public final boolean saveToDB(SDKDatacontroler sdkDatacontroler) {
        try {
            saveModel(this.token, sdkDatacontroler.getController());
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static LoginTokenModel copyFromDBtoObject(String token, SDKDatacontroler sdkDatacontroler) {
        LoginTokenModel model = null;
        try {
            String str = (String) DataModel.getModel(token, LoginTokenModel.class, sdkDatacontroler.getController());
            if (str != null) {
                model = Utils.fromJson(str, LoginTokenModel.class);
                if (model != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return model;
    }

    public static LoginTokenModel create(String accountID, SDKDatacontroler sdkDatacontroler){
        LoginTokenModel loginTokenModel = new LoginTokenModel(accountID);
        if(loginTokenModel.saveToDB(sdkDatacontroler)){
            return loginTokenModel;
        }

        return null;
    }
}
