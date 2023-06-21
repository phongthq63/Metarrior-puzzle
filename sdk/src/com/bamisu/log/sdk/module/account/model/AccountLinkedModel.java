package com.bamisu.log.sdk.module.account.model;

/**
 * Create by Popeye on 11:57 AM, 4/23/2020
 */

import com.bamisu.gamelib.socialnetwork.ESocialNetwork;
import com.bamisu.gamelib.sql.sdk.dao.AccountDAO;
import com.bamisu.gamelib.sql.sdk.dao.LinkDAO;
import com.bamisu.gamelib.sql.sdk.dbo.AccountDBO;
import com.bamisu.gamelib.sql.sdk.dbo.LinkDBO;
import com.bamisu.log.sdk.module.data.SDKDatacontroler;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.sdk.module.sql.SDKsqlManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * dùng để check account đã liên kết với những tài khoản social network nào
 */
public class AccountLinkedModel extends DataModel {
    public String accountID;
    public Map<Integer, String> mapSocialNetwork = new HashMap<>(); //SocialNetwork map SocialNetworkKey

    public AccountLinkedModel() {
    }

    public AccountLinkedModel(String accountID) {
        this.accountID = accountID;
        mapSocialNetwork = new HashMap<>();
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

    public static AccountLinkedModel copyFromDB(String accountID, SDKDatacontroler sdkDatacontroler) {
        AccountLinkedModel model = null;
        try {
            String str = (String) getModel(accountID, AccountLinkedModel.class, sdkDatacontroler.getController());
            if (str != null) {
                model = Utils.fromJson(str, AccountLinkedModel.class);
                if (model != null) {
//                    model.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        if(model == null) {
            model = create(accountID, sdkDatacontroler);
        }

        return model;
    }

    public static AccountLinkedModel create(String accountID, SDKDatacontroler sdkDatacontroler){
        AccountLinkedModel accountLinkedModel = new AccountLinkedModel(accountID);
        if(accountLinkedModel.save(sdkDatacontroler)) return accountLinkedModel;
        return null;
    }

    public String linkedAsString() {
        String s = "";
        for(Integer id : mapSocialNetwork.keySet()){
            s += id + ",";
        }
        return s;
    }

    public Set<Integer> linkedAsList() {
        return mapSocialNetwork.keySet();
    }

    public void link(int socialNetwork, String socialNetworkKey) {
        mapSocialNetwork.put(socialNetwork, socialNetworkKey);
        save(SDKDatacontroler.getInstance());

        //create to sql
        LinkDAO.save(SDKsqlManager.getInstance().getSqlController(), new LinkDBO(accountID, ESocialNetwork.fromIntValue(socialNetwork).getName(), socialNetworkKey, Utils.getTimestampInSecond()));
        AccountDBO accountDBO = AccountDAO.get(SDKsqlManager.getInstance().getSqlController(), accountID);
        if(accountDBO != null){
            accountDBO.linked = 1;
            AccountDAO.save(SDKsqlManager.getInstance().getSqlController(), accountDBO);
        }
    }
}
