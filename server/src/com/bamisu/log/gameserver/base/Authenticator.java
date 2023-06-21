package com.bamisu.log.gameserver.base;

import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.socialcontroller.exceptions.SocialControllerException;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.WoL.WoLModel;
import com.bamisu.log.gameserver.datamodel.WoL.WoLUserModel;
import com.bamisu.log.gameserver.datamodel.bag.entities.AdventureModel;
import com.bamisu.log.gameserver.datamodel.campaign.UserCampaignDetailModel;
import com.bamisu.log.gameserver.module.sdkthriftclient.SDKGateAccount;
import com.bamisu.log.gameserver.sql.user.dao.PlayerDAO;
import com.bamisu.log.sdkthrift.entities.TLoginResult;
import com.bamisu.gamelib.auth.LoginType;
import com.bamisu.gamelib.base.model.UserBase;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;

public class Authenticator {
    /**
     * Login từ client
     * @param token
     * @param loginType
     * @param zone
     * @return
     * @throws Exception
     */
    public static UserModel doLogin(String token, LoginType loginType, String clientIP, int os, String did, Zone zone) throws Exception {
        switch (loginType){
            case GUESST:
                token = "";
                break;
            case TOKEN:
                break;
        }

        //TODO: gửi token cho SDK để lấy Account ID
        TLoginResult loginResult = SDKGateAccount.loginGame(token, ((ZoneExtension) zone.getExtension()).getServerID(), clientIP, os, did);
        return login(loginResult, zone);
    }

    public static UserModel doLogin(String username, String password, String clientIP, int os, String did, Zone zone) throws Exception {
        //TODO: gửi token cho SDK để lấy Account ID
        TLoginResult loginResult = SDKGateAccount.usernameLoginGame(username, password, ((ZoneExtension) zone.getExtension()).getServerID(), clientIP, os, did);
        if (loginResult == null) {
            throw new SocialControllerException(ServerConstant.ErrorCode.ERR_INVALID_USERNAME_OR_PASSWORD, "");
        }

        return login(loginResult, zone);
    }

    private static UserModel login(TLoginResult loginResult, Zone zone) {
        UserBase userBase = UserBase.copyFromDBtoObject(loginResult.accountID, zone);
        UserModel userModel;
        if (userBase != null) {
            userModel = ((ZoneExtension) zone.getExtension()).getUserManager().getUserModel(userBase.userID);
        } else {    //tạo player mới
            userModel = createUser(loginResult.accountID, zone, ((ZoneExtension) zone.getExtension()).getServerID());
            SDKGateAccount.joinServer(userModel, ((ZoneExtension) zone.getExtension()).getServerID());
            //
            UserCampaignDetailModel.copyFromDBtoObject(userModel.userID, zone);
            AdventureModel.copyFromDBtoObject(userModel.userID, zone);
            WoLModel.copyFromDBtoObject(zone);
            WoLUserModel.copyFromDBtoObject(userModel.userID, zone);

        }
        PlayerDAO.save(zone, userModel.userID, userModel.accountID);
        //test
//        if(userModel == null){
//            userModel = createUser(loginResult.accountID, zone);
//        }

        try {
            userModel.linked = Utils.fromJson(loginResult.socialNetworkLinked, userModel.linked.getClass());
        }catch (Exception e){
            e.printStackTrace();
            userModel.linked = new ArrayList<>();
        }

        userModel.access_token = loginResult.token;
        return userModel;
    }

    /**
     * Tạo mới 1 UserModel từ 1 Account ID
     * @param accountID
     * @param zone
     * @return
     */
    public static UserModel createUser(String accountID, Zone zone, int serverID){
        return UserModel.createUserModel(accountID, zone, serverID);
    }
}

