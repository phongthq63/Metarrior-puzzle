package com.bamisu.log.sdk.module.invitecode.model;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.sdk.module.data.SDKDatacontroler;

public class InviteCodeModel extends DataModel {
    public String inviteCode;
    public String accountID;

    private final Object lockCode = new Object();



    public static InviteCodeModel create(UserInviteModel userInviteModel, SDKDatacontroler sdkDatacontroler){
        InviteCodeModel inviteCodeModel = new InviteCodeModel();
        inviteCodeModel.inviteCode = userInviteModel.inviteCode;
        inviteCodeModel.accountID = userInviteModel.accountID;
        inviteCodeModel.saveToDB(sdkDatacontroler);

        return inviteCodeModel;
    }


    public final boolean saveToDB(SDKDatacontroler sdkDatacontroler) {
        try {
            this.saveModel(inviteCode, sdkDatacontroler.getController());
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static InviteCodeModel copyFromDBtoObject(String inviteCode, SDKDatacontroler sdkDatacontroler) {
        InviteCodeModel pInfo = null;
        try {
            String str = (String) getModel(inviteCode, InviteCodeModel.class, sdkDatacontroler.getController());
            if (str != null) {
                pInfo = Utils.fromJson(str, InviteCodeModel.class);
                if (pInfo != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }

    public boolean removeFromDB(SDKDatacontroler sdkDatacontroler){
        accountID = "";
        return saveToDB(sdkDatacontroler);
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    public boolean updateCode(String newCode, SDKDatacontroler sdkDatacontroler){
        synchronized (lockCode){
            this.inviteCode = newCode;
            return saveToDB(sdkDatacontroler);
        }
    }
}
