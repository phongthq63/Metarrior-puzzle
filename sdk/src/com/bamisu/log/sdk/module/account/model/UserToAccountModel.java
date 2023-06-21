package com.bamisu.log.sdk.module.account.model;

import com.bamisu.log.sdk.module.data.SDKDatacontroler;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;

/**
 * Create by Popeye on 8:24 AM, 5/4/2020
 */
public class UserToAccountModel extends DataModel {
    public int serverID;
    public String userID;
    public String accountID;

    public UserToAccountModel() {
    }

    public UserToAccountModel(int serverID, String userID, String accountID) {
        this.serverID = serverID;
        this.userID = userID;
        this.accountID = accountID;
    }

    private static String genKey(int serverID, String userID) {
        return serverID + ":" + userID;
    }

    public boolean save(SDKDatacontroler sdkDatacontroler) {
        try {
            this.saveModel(genKey(serverID, userID), sdkDatacontroler.getController());
            return true;
        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static UserToAccountModel copyFromDB(int serverID, String userID, SDKDatacontroler sdkDatacontroler) {
        UserToAccountModel model = null;
        try {
            String str = (String) getModel(genKey(serverID, userID), UserToAccountModel.class, sdkDatacontroler.getController());
            if (str != null) {
                model = Utils.fromJson(str, UserToAccountModel.class);
                if (model != null) {
//                    model.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        return model;
    }

    public static UserToAccountModel create(int serverID, String userID, String accountID, SDKDatacontroler sdkDatacontroler){
        UserToAccountModel userToAccountModel = new UserToAccountModel(serverID, userID, accountID);
        if(userToAccountModel.save(sdkDatacontroler)) return userToAccountModel;
        return null;
    }
}
