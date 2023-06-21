package com.bamisu.log.sdk.module.giftcode.model;

import com.bamisu.gamelib.sql.sdk.dao.GiftcodeDAO;
import com.bamisu.log.sdk.module.data.SDKDatacontroler;
import com.bamisu.log.sdk.module.giftcode.entities.UseGiftcodeLog;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.sdk.module.sql.SDKsqlManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by Popeye on 9:20 AM, 4/23/2020
 */
public class AccountUseGiftcodeModel extends DataModel {
    public String accountID;
    public Map<String, List<UseGiftcodeLog>> mapUseCodeLog = new HashMap<>();

    public AccountUseGiftcodeModel() {
    }

    public AccountUseGiftcodeModel(String accountID) {
        this.accountID = accountID;
    }

    public boolean save(SDKDatacontroler sdkDatacontroler) {
        try {
            this.saveModel(accountID, sdkDatacontroler.getController());
            return true;
        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static AccountUseGiftcodeModel copyFromDB(String accountID, SDKDatacontroler sdkDatacontroler) {
        AccountUseGiftcodeModel model = null;
        try {
            String str = (String) getModel(accountID, AccountUseGiftcodeModel.class, sdkDatacontroler.getController());
            if (str != null) {
                model = Utils.fromJson(str, AccountUseGiftcodeModel.class);
                if (model != null) {
//                    model.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        if (model == null) {
            model = new AccountUseGiftcodeModel(accountID);
            if (!model.save(sdkDatacontroler)) {
                return null;
            }
        }

        return model;
    }

    /**
     * check account đã sử dụng code ở server này chưa
     * @param code
     * @param serverID
     * @return
     */
    public boolean haveUsed(String code, String serverID) {
        if(!mapUseCodeLog.containsKey(serverID)){
            return false;
        }

        List<UseGiftcodeLog> logs = mapUseCodeLog.get(serverID);
        for(UseGiftcodeLog log : logs){
            if(log.giftcode.equalsIgnoreCase(code)){
                return true;
            }
        }

        return false;
    }

    /**
     *  @param code
     * @param serverID
     * @param uid
     * @param sdkDatacontroler
     */
    public boolean activeCode(String code, String serverID, String uid, SDKDatacontroler sdkDatacontroler) {
        if(!mapUseCodeLog.containsKey(serverID)){
            mapUseCodeLog.put(serverID, new ArrayList<>());
        }

        mapUseCodeLog.get(serverID).add(new UseGiftcodeLog(accountID, uid, code, Utils.getTimestampInSecond()));
        return save(sdkDatacontroler);
    }
}
