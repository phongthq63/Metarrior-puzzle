package com.bamisu.log.sdk.module.account.model;

import com.bamisu.gamelib.entities.JoinedServerInfo;
import com.bamisu.log.sdk.module.data.SDKDatacontroler;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Create by Popeye on 11:14 AM, 4/23/2020
 */

/**
 * Thông tin cơ bản của Account
 */
public class AccountModel extends DataModel {
    public String id;
    public int lastServerLogin = -1;
    public Map<Integer, Long> mapPlayer = new HashMap<>(); //server id map player id on server
    public Map<Integer, JoinedServerInfo> joinedServerInfo = new HashMap<>(); //server id map level player
    public String country = "";

    public AccountModel() {
        id = UUID.randomUUID().toString();
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

    public static AccountModel copyFromDB(String accountID, SDKDatacontroler sdkDatacontroler) {
        AccountModel model = null;
        try {
            String str = (String) getModel(accountID, AccountModel.class, sdkDatacontroler.getController());
            if (str != null) {
                model = Utils.fromJson(str, AccountModel.class);
                if (model != null) {
//                    model.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return model;
    }

    public static AccountModel create(SDKDatacontroler sdkDatacontroler) {
        AccountModel acountModel = new AccountModel();
        if (acountModel.save(sdkDatacontroler)) {
            AccountCountModel.create(acountModel.id, sdkDatacontroler);
            return acountModel;
        }

        return null;
    }

    public void joinServer(String accountID, int serverID, long userID, String displayName, String avatar, int avatarFrame, int level){
        mapPlayer.put(serverID, userID);
        joinedServerInfo.put(serverID, new JoinedServerInfo(serverID, level, displayName, avatar, avatarFrame));
    }
}
