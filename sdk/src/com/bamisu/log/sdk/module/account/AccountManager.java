package com.bamisu.log.sdk.module.account;

import com.bamisu.log.sdk.module.account.model.AccountModel;
import com.bamisu.log.sdk.module.data.SDKDatacontroler;
import com.bamisu.log.sdk.module.invitecode.InviteManager;
import com.bamisu.log.sdk.module.invitecode.model.UserInviteModel;
import com.bamisu.log.sdk.module.sql.SDKsqlManager;
import com.bamisu.gamelib.sql.sdk.dao.AccountDAO;
import com.bamisu.gamelib.utils.Utils;

/**
 * Create by Popeye on 5:26 PM, 4/24/2020
 */
public class AccountManager {
    private static AccountManager ourInstance = new AccountManager();

    public static AccountManager getInstance() {
        return ourInstance;
    }

    private AccountManager() {
    }

    public AccountModel creatAccount() {
        //tạo
        AccountModel accountModel = AccountModel.create(SDKDatacontroler.getInstance());
        if(accountModel == null){
            return null;
        }else {
            UserInviteModel userInviteModel = InviteManager.getInstance().getUserInviteModelByID(accountModel.id);
            AccountDAO.create(SDKsqlManager.getInstance().getSqlController(), accountModel.id, userInviteModel.inviteCode, Utils.getTimestampInSecond());
            return accountModel;
        }
    }
}
