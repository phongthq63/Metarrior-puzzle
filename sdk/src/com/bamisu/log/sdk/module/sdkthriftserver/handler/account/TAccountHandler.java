package com.bamisu.log.sdk.module.sdkthriftserver.handler.account;

import com.bamisu.gamelib.base.datacontroller.IDataController;
import com.bamisu.gamelib.entities.JoinedServerInfo;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.entities.ServerInfo;
import com.bamisu.gamelib.socialnetwork.ESocialNetwork;
import com.bamisu.gamelib.sql.sdk.dao.AccountDAO;
import com.bamisu.gamelib.sql.sdk.dao.IAPDAO;
import com.bamisu.gamelib.sql.sdk.dao.LoginLogDAO;
import com.bamisu.gamelib.sql.sdk.dbo.AccountDBO;
import com.bamisu.gamelib.sql.sdk.dbo.IAPDBO;
import com.bamisu.gamelib.sql.sdk.dbo.LoginLogDBO;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.ValidateUtils;
import com.bamisu.log.sdk.module.account.AccountManager;
import com.bamisu.log.sdk.module.account.exception.SocialAccountAlreadyExist;
import com.bamisu.log.sdk.module.account.model.*;
import com.bamisu.log.sdk.module.auth.SocialNetworkUtils;
import com.bamisu.log.sdk.module.data.SDKDatacontroler;
import com.bamisu.log.sdk.module.multiserver.MultiServerManager;
import com.bamisu.log.sdk.module.sql.SDKsqlManager;
import com.bamisu.log.sdkthrift.entities.*;
import com.bamisu.log.sdkthrift.exception.SDKThriftError;
import com.bamisu.log.sdkthrift.exception.ThriftSVException;
import com.bamisu.log.sdkthrift.service.account.AccountService;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;

/**
 * Create by Popeye on 10:26 AM, 4/25/2020
 */
public class TAccountHandler implements AccountService.Iface {
    @Override
    public TLoginResult loginGame(String token, int serverID, String clientIP, int os, String did) throws ThriftSVException {
        Logger logger = Logger.getLogger("sdklogin");
        TLoginResult loginResult = new TLoginResult();
        AccountModel accountModel = null;

        //TODO: nick cũ đăng nhập bằng TOKEN
        if (!token.isEmpty()) {
            LoginTokenModel loginTokenModel = LoginTokenModel.copyFromDBtoObject(token, SDKDatacontroler.getInstance());
            if (loginTokenModel != null) {
                accountModel = AccountModel.copyFromDB(loginTokenModel.accountID, SDKDatacontroler.getInstance());

                //tồn tại token
                loginResult.accountID = loginTokenModel.accountID;
                loginResult.token = loginTokenModel.token;
                //lấy những mạng xã hội đã liên kết với tài khoản này
                AccountLinkedModel accountLinkedModel = AccountLinkedModel.copyFromDB(loginTokenModel.accountID, SDKDatacontroler.getInstance());
                loginResult.socialNetworkLinked = Utils.toJson(accountLinkedModel.linkedAsList());

                //create last server login
                accountModel.lastServerLogin = serverID;
                accountModel.save(SDKDatacontroler.getInstance());
                accountLinkedModel.save(SDKDatacontroler.getInstance());

                logger.info("login " + accountModel.id + " " + serverID + " " + loginResult.token);
//                return loginResult;
            }
        } else { //TODO: đăng nhập = nick mới nếu token rỗng hoặc k tồn tại
            accountModel = AccountManager.getInstance().creatAccount();
            if (accountModel == null) {
                //tạo account thất bại
                throw SDKThriftError.SYSTEM_ERROR;
            }

            //tạo account thành công
            LoginTokenModel loginTokenModel = LoginTokenModel.create(accountModel.id, SDKDatacontroler.getInstance());
            if (loginTokenModel == null) {
                //tạo token thất bại
                throw SDKThriftError.SYSTEM_ERROR;
            }

            //tạo token thành công
            loginResult.accountID = loginTokenModel.accountID;
            loginResult.token = loginTokenModel.token;

            //lấy những mạng xã hội đã liên kết với tài khoản này
            AccountLinkedModel accountLinkedModel = AccountLinkedModel.copyFromDB(accountModel.id, SDKDatacontroler.getInstance());
            loginResult.socialNetworkLinked = Utils.toJson(accountLinkedModel.linkedAsList());

            //create last server login
            accountModel.lastServerLogin = serverID;
            accountModel.save(SDKDatacontroler.getInstance());
            accountLinkedModel.save(SDKDatacontroler.getInstance());

            logger.info("register " + accountModel.id + " " + serverID + " " + loginResult.token);
        }
        if(accountModel != null && loginResult != null){
            AccountLoginInfoModel accountLoginInfoModel = AccountLoginInfoModel.copyFromDB(accountModel.id, SDKDatacontroler.getInstance());
            if(Utils.deltaDay(accountLoginInfoModel.lastLogin, Utils.getTimestampInSecond()) == 1){
                accountLoginInfoModel.loginCount++;
            }

            if(Utils.deltaDay(accountLoginInfoModel.lastLogin, Utils.getTimestampInSecond()) > 1){
                accountLoginInfoModel.loginCount = 0;
            }

            accountLoginInfoModel.lastLogin = Utils.getTimestampInSecond();
            accountLoginInfoModel.save(SDKDatacontroler.getInstance());

            LoginLogDAO.save(SDKsqlManager.getInstance().getSqlController(), new LoginLogDBO(accountModel.id, Utils.getTimestampInSecond(), serverID, accountLoginInfoModel.loginCount, clientIP, os, did));
        }else {
            throw SDKThriftError.SYSTEM_ERROR;
        }
        return loginResult;
    }

    /**
     * @param userID
     * @param serverID
     * @param socialNetwork
     * @param socialNetworkToken access token
     * @return
     * @throws ThriftSVException
     * @throws TException
     */
    @Override
    public TLinkAccountResult linkAccount(String accountID, long userID, int serverID, int socialNetwork, String socialNetworkToken) throws ThriftSVException, TException {
        String socialNetworkKey = "";
        switch (ESocialNetwork.fromIntValue(socialNetwork)) {
            case FACEBOOK:
                socialNetworkKey = SocialNetworkUtils.getIDforAppFacebook(socialNetworkToken);
                break;
            case GOOGLE:
                socialNetworkKey = SocialNetworkUtils.getIDforAppGoogle(socialNetworkToken);
                break;
            case APPLE:
                socialNetworkKey = SocialNetworkUtils.getIDforAppApple(socialNetworkToken);
                break;
            case SINGMAAN:
                socialNetworkKey = SocialNetworkUtils.getIDforAppSingmaan(socialNetworkToken);
                break;
            case BLOCKCHAIN:
                socialNetworkKey = SocialNetworkUtils.getAddressWalletBlockchain(socialNetworkKey);
                break;
            case GAME_CENTER:
                return new TLinkAccountResult();
        }
        if(socialNetworkKey.isEmpty()){
            Logger.getLogger("sdk").info("linkAccount|token_invalid|" + socialNetwork + "|" + socialNetworkToken);
            throw SDKThriftError.SYSTEM_ERROR;
        }

        AccountModel accountModel = AccountModel.copyFromDB(accountID, SDKDatacontroler.getInstance());

        //check account này đã được liên kết trước đó chưa
        AccountLinkedModel accountLinkedModel = AccountLinkedModel.copyFromDB(accountModel.id, SDKDatacontroler.getInstance());
        if (accountLinkedModel.mapSocialNetwork.containsKey(socialNetwork)) {
            Logger.getLogger("sdk").info("linkAccount|account_have_linked|" + socialNetwork + "|" + accountID);
            throw SDKThriftError.ACCOUNT_HAVE_LINKED;
        }

        try {
            //tạo SocialAccountModel
            SocialAccountModel socialAccountModel = SocialAccountModel.create(accountModel, socialNetwork, socialNetworkKey, SDKDatacontroler.getInstance());

            //cập nhật AccountLinkedModel
            accountLinkedModel.link(socialAccountModel.socialNetwork, socialAccountModel.socialNetworkKey);
        } catch (SocialAccountAlreadyExist socialAccountAlreadyExist) {
            //tk socical network nay da duoc su dung
            Logger.getLogger("sdk").info("linkAccount|social_account_have_linked|" + socialNetwork + "|" + socialNetworkKey);
            socialAccountAlreadyExist.printStackTrace();
            throw SDKThriftError.SOCIAL_ACCOUNT_HAVE_LINKED;
        }

        Logger.getLogger("sdk").info("linkAccount|success|" + socialNetwork + "|" + accountID + "|" + socialNetworkKey);
        return new TLinkAccountResult(userID, serverID, socialNetwork);
    }

    @Override
    public TSwitchAccountResult switchAccount(int socialNetwork, String socialNetworkToken) throws ThriftSVException, TException {
        TSwitchAccountResult switchAccountResult = null;

        String socialNetworkKey = "";
        switch (ESocialNetwork.fromIntValue(socialNetwork)) {
            case FACEBOOK:
                socialNetworkKey = SocialNetworkUtils.getIDforAppFacebook(socialNetworkToken);
                break;
            case GOOGLE:
                socialNetworkKey = SocialNetworkUtils.getIDforAppGoogle(socialNetworkToken);
                break;
            case APPLE:
                socialNetworkKey = SocialNetworkUtils.getIDforAppApple(socialNetworkToken);
                break;
            case SINGMAAN:
                socialNetworkKey = SocialNetworkUtils.getIDforAppSingmaan(socialNetworkToken);
                break;
            case BLOCKCHAIN:
                socialNetworkKey = SocialNetworkUtils.getAddressWalletBlockchain(socialNetworkToken);
                break;
            case GAME_CENTER:
                return new TSwitchAccountResult();
        }

        SocialAccountModel socialAccountModel = SocialAccountModel.copyFromDB(socialNetwork, socialNetworkKey, SDKDatacontroler.getInstance());
        if (socialAccountModel != null) {
            //Đã có account
            LoginTokenModel loginTokenModel = LoginTokenModel.create(socialAccountModel.accountID, SDKDatacontroler.getInstance());

            ServerInfo serverInfo = MultiServerManager.getInstance().getServerForToken(socialAccountModel.accountID);
            switchAccountResult = new TSwitchAccountResult();
            switchAccountResult.loginKey = loginTokenModel.token;
            switchAccountResult.addr = serverInfo.addr;
            switchAccountResult.port = serverInfo.port;
            switchAccountResult.zone = serverInfo.zone;
            switchAccountResult.serverID = serverInfo.serverID;
            return switchAccountResult;
        } else {
            //chưa có account, tạo mới account
            AccountModel accountModel = AccountManager.getInstance().creatAccount();
            if (accountModel == null) {
                //tạo account thất bại
                throw SDKThriftError.SYSTEM_ERROR;
            }

            //link với SocialAccount
            //tạo SocialAccountModel
            try {
                socialAccountModel = SocialAccountModel.create(accountModel, socialNetwork, socialNetworkKey, SDKDatacontroler.getInstance());
            } catch (SocialAccountAlreadyExist socialAccountAlreadyExist) {
                socialAccountAlreadyExist.printStackTrace();
                throw SDKThriftError.SOCIAL_ACCOUNT_HAVE_LINKED;
            }

            //cập nhật AccountLinkedModel
            AccountLinkedModel accountLinkedModel = AccountLinkedModel.copyFromDB(accountModel.id, SDKDatacontroler.getInstance());
            accountLinkedModel.link(socialAccountModel.socialNetwork, socialAccountModel.socialNetworkKey);

            //tạo login key
            LoginTokenModel loginTokenModel = LoginTokenModel.create(accountModel.id, SDKDatacontroler.getInstance());
            if (loginTokenModel == null) {
                //tạo token thất bại
                throw SDKThriftError.SYSTEM_ERROR;
            }

            ServerInfo serverInfo = MultiServerManager.getInstance().getServerForToken(accountModel.id);
            switchAccountResult = new TSwitchAccountResult();
            switchAccountResult.loginKey = loginTokenModel.token;
            switchAccountResult.addr = serverInfo.addr;
            switchAccountResult.port = serverInfo.port;
            switchAccountResult.zone = serverInfo.zone;
            switchAccountResult.serverID = serverInfo.serverID;
            return switchAccountResult;
        }
    }

    @Override
    public void updateLevel(String accountID, int serverID, int level) throws ThriftSVException, TException {
        AccountModel accountModel = AccountModel.copyFromDB(accountID, SDKDatacontroler.getInstance());
        if (accountModel != null && accountModel.joinedServerInfo.containsKey(serverID)) {
            JoinedServerInfo joinedServerInfo = accountModel.joinedServerInfo.get(serverID);
            joinedServerInfo.level = level;

            if(joinedServerInfo.level >= 50){
                AccountDBO accountDBO = AccountDAO.get(SDKsqlManager.getInstance().getSqlController(), accountID);
                if(accountDBO != null && accountDBO.level50 == 0){
                    accountDBO.level50 = 1;
                    AccountDAO.save(SDKsqlManager.getInstance().getSqlController(), accountDBO);
                }
            }
            accountModel.save(SDKDatacontroler.getInstance());
        }
    }

    @Override
    public void updateAvatar(String accountID, int serverID, String avatar, int frame) throws ThriftSVException, TException {
        AccountModel accountModel = AccountModel.copyFromDB(accountID, SDKDatacontroler.getInstance());
        if (accountModel != null && accountModel.joinedServerInfo.containsKey(serverID)) {
            JoinedServerInfo joinedServerInfo = accountModel.joinedServerInfo.get(serverID);
            joinedServerInfo.avatar = avatar;
            joinedServerInfo.frame = frame;
            accountModel.save(SDKDatacontroler.getInstance());
        }
    }

    @Override
    public void updateDisplayName(String accountID, int serverID, String displayName) throws ThriftSVException, TException {
        AccountModel accountModel = AccountModel.copyFromDB(accountID, SDKDatacontroler.getInstance());
        if (accountModel != null && accountModel.joinedServerInfo.containsKey(serverID)) {
            JoinedServerInfo joinedServerInfo = accountModel.joinedServerInfo.get(serverID);
            joinedServerInfo.name = displayName;
            accountModel.save(SDKDatacontroler.getInstance());
        }
    }

    @Override
    public void joinServer(String accountID, int serverID, long userID, String displayName, String avatar, int avatarFrame, int level) throws ThriftSVException, TException {
        AccountModel accountModel = AccountModel.copyFromDB(accountID, SDKDatacontroler.getInstance());
        if (accountModel == null) throw new ThriftSVException();
        accountModel.joinServer(accountID, serverID, userID, displayName, avatar, avatarFrame, level);
        accountModel.save(SDKDatacontroler.getInstance());
    }

    @Override
    public void logIAP(String accountID, int serverId, long userID, int gate, String idAPI, String purchaseToken, String transactionId) throws TException {
        IAPDAO.save(SDKsqlManager.getInstance().getSqlController(), IAPDBO.create(userID, idAPI, purchaseToken, transactionId, gate, serverId, accountID));
    }

    @Override
    public String getUniqueIdSocialNetwork(String accountID, int socialNetworkID) throws ThriftSVException, TException {
        AccountModel accountModel = AccountModel.copyFromDB(accountID, SDKDatacontroler.getInstance());
        if (accountModel == null) throw new ThriftSVException();
        AccountLinkedModel accountLinkedModel = AccountLinkedModel.copyFromDB(accountModel.id, SDKDatacontroler.getInstance());
        return accountLinkedModel.mapSocialNetwork.getOrDefault(socialNetworkID, "");
    }

    @Override
    public long getUserId(String socialNetworkKey, int socialNetworkID, String serverId) throws ThriftSVException, TException {
        SocialAccountModel socialAccountModel = SocialAccountModel.copyFromDB(socialNetworkID, socialNetworkKey, SDKDatacontroler.getInstance());
        if (socialAccountModel == null) throw new ThriftSVException();
        AccountModel accountModel = AccountModel.copyFromDB(socialAccountModel.accountID, SDKDatacontroler.getInstance());
        if (accountModel == null) throw new ThriftSVException();

        return accountModel.mapPlayer.getOrDefault(Integer.parseInt(serverId), -1l);
    }

    @Override
    public TLoginResult usernameLoginGame(String username, String password, int serverID, String clientIP, int os, String did) throws ThriftSVException, TException {
        Logger logger = Logger.getLogger("sdklogin");
        TLoginResult loginResult = new TLoginResult();

        SDKDatacontroler sdkDatacontroler = SDKDatacontroler.getInstance();
        UsernameModel usernameModel = UsernameModel.load(username, sdkDatacontroler);
        logger.info("login username: " + username + " password: " + password);
        if (usernameModel == null || !usernameModel.verifyPassword(password)) {
            logger.error("login error " + usernameModel);
            throw SDKThriftError.WRONG_USERNAME_OR_PASSWORD;
        }

        loginResult.accountID = usernameModel.accountID;
        AccountModel accountModel = AccountModel.copyFromDB(usernameModel.accountID, sdkDatacontroler);
        accountModel.lastServerLogin = serverID;
        accountModel.save(sdkDatacontroler);
        logger.info("login " + accountModel.id + " " + serverID + " " + username);

        // Tạo token
        LoginTokenModel loginTokenModel = LoginTokenModel.create(accountModel.id, SDKDatacontroler.getInstance());
        if (loginTokenModel == null) {
            //tạo token thất bại
            throw SDKThriftError.SYSTEM_ERROR;
        }

        //tạo token thành công
        loginResult.accountID = loginTokenModel.accountID;
        loginResult.token = loginTokenModel.token;

        //lấy những mạng xã hội đã liên kết với tài khoản này
        AccountLinkedModel accountLinkedModel = AccountLinkedModel.copyFromDB(usernameModel.accountID, sdkDatacontroler);
        loginResult.socialNetworkLinked = Utils.toJson(accountLinkedModel.linkedAsList());

        // lưu thông tin
        AccountLoginInfoModel accountLoginInfoModel = AccountLoginInfoModel.copyFromDB(accountModel.id, sdkDatacontroler);
        if(Utils.deltaDay(accountLoginInfoModel.lastLogin, Utils.getTimestampInSecond()) == 1){
            accountLoginInfoModel.loginCount++;
        }

        if(Utils.deltaDay(accountLoginInfoModel.lastLogin, Utils.getTimestampInSecond()) > 1){
            accountLoginInfoModel.loginCount = 0;
        }

        accountLoginInfoModel.lastLogin = Utils.getTimestampInSecond();
        accountLoginInfoModel.save(SDKDatacontroler.getInstance());
        try {
            LoginLogDAO.save(SDKsqlManager.getInstance().getSqlController(), new LoginLogDBO(accountModel.id, Utils.getTimestampInSecond(), serverID, accountLoginInfoModel.loginCount, clientIP, os, did));
        } catch (Exception e) {
            e.printStackTrace();
        }


        return loginResult;
    }

    @Override
    public boolean updateUsernameAndPassword(String accountId, String username, String password, String email, String code) throws ThriftSVException, TException {
        SDKDatacontroler sdkDatacontroler = SDKDatacontroler.getInstance();
        IDataController dataController = sdkDatacontroler.getController();
        UserCodeModel codeModel = UserCodeModel.load(code, dataController);
        if (codeModel == null) {
            throw new ThriftSVException(ServerConstant.ErrorCode.ERR_CODE_INVALID, "");
        }

        if (!ValidateUtils.isEmail(email) || !codeModel.email.equalsIgnoreCase(email)) {
            throw new ThriftSVException(ServerConstant.ErrorCode.ERR_EMAIL_INVALID, "");
        }

        UserEmailModel emailModel = UserEmailModel.load(codeModel.email, dataController);
        if (emailModel != null ) {
            throw new ThriftSVException(ServerConstant.ErrorCode.ERR_EMAIL_ALREADY_EXIST, "");
        }

        UsernameModel usernameModel = UsernameModel.load(username, sdkDatacontroler);
        if (usernameModel != null) {
            throw new ThriftSVException(ServerConstant.ErrorCode.ERR_USERNAME_ALREADY_EXIST, String.valueOf(ServerConstant.ErrorCode.ERR_USERNAME_ALREADY_EXIST));
        }

        AccountModel accountModel = AccountModel.copyFromDB(accountId, sdkDatacontroler);
        if (accountModel == null) {
            throw new ThriftSVException(ServerConstant.ErrorCode.ERR_USER_NOT_FOUND, String.valueOf(ServerConstant.ErrorCode.ERR_USER_NOT_FOUND));
        }

        // get linked account model
        AccountLinkedModel linkedModel = AccountLinkedModel.copyFromDB(accountId, sdkDatacontroler);
        int networkId = ESocialNetwork.USERNAME.getIntValue();
        if (linkedModel == null || linkedModel.mapSocialNetwork.get(networkId) != null) {
            throw new ThriftSVException(ServerConstant.ErrorCode.ERR_USERNAME_ALREADY_EXIST, String.valueOf(ServerConstant.ErrorCode.ERR_USERNAME_ALREADY_EXIST));
        }

        // create social account model
        try {
            SocialAccountModel.create(accountModel, networkId, username, SDKDatacontroler.getInstance());
            linkedModel.mapSocialNetwork.put(networkId, username);
            linkedModel.save(SDKDatacontroler.getInstance());
        } catch (SocialAccountAlreadyExist e) {
            throw new ThriftSVException(ServerConstant.ErrorCode.ERR_USER_NOT_FOUND, String.valueOf(ServerConstant.ErrorCode.ERR_USER_NOT_FOUND));
        }

        // create username model
        UsernameModel model = UsernameModel.create(accountId, username, password);
        if (model == null) {
            throw new ThriftSVException(ServerConstant.ErrorCode.ERR_SYS, String.valueOf(ServerConstant.ErrorCode.ERR_SYS));
        }

        model.email = email;
        model.save(sdkDatacontroler);
        // create email model
        emailModel = new UserEmailModel(accountModel.id, username, email);
        emailModel.save(dataController);

        // xoa code
        codeModel.delete(dataController);

        return true;
    }

    @Override
    public boolean changePassword(String accountId, String password) throws ThriftSVException, TException {
        SDKDatacontroler sdkDatacontroler = SDKDatacontroler.getInstance();
        int networkId = ESocialNetwork.USERNAME.getIntValue();

        // get linked account
        AccountLinkedModel linkedModel = AccountLinkedModel.copyFromDB(accountId, sdkDatacontroler);
        if (linkedModel == null || linkedModel.mapSocialNetwork.get(networkId) == null) {
            throw new ThriftSVException(ServerConstant.ErrorCode.ERR_USER_NOT_FOUND, String.valueOf(ServerConstant.ErrorCode.ERR_USER_NOT_FOUND));
        }

        String username = linkedModel.mapSocialNetwork.get(networkId);
        // get username model
        UsernameModel model = UsernameModel.load(username, sdkDatacontroler);
        if (model == null) {
            throw new ThriftSVException(ServerConstant.ErrorCode.ERR_USER_NOT_FOUND, String.valueOf(ServerConstant.ErrorCode.ERR_USER_NOT_FOUND));
        }

        if (!model.changePassword(password)) {
            return false;
        }

        return true;
    }

    @Override
    public String getUsername(String accountId) throws ThriftSVException, TException {
        // Lay username
        AccountLinkedModel linkedModel = AccountLinkedModel.copyFromDB(accountId, SDKDatacontroler.getInstance());
        int networkId = ESocialNetwork.USERNAME.getIntValue();
        if (linkedModel.mapSocialNetwork.get(networkId) != null) {
            return linkedModel.mapSocialNetwork.get(networkId);
        }

        return "";
    }

    @Override
    public String getWalletByAccountId(String accountId) throws TException {
        AccountLinkedModel linkedModel = AccountLinkedModel.copyFromDB(accountId, SDKDatacontroler.getInstance());
        if (linkedModel == null) {
            throw new ThriftSVException(ServerConstant.ErrorCode.ERR_USER_NOT_FOUND, String.valueOf(ServerConstant.ErrorCode.ERR_USER_NOT_FOUND));
        }

        int networkId = ESocialNetwork.BLOCKCHAIN.getIntValue();
        return linkedModel.mapSocialNetwork.getOrDefault(networkId, "");
    }

    @Override
    public long linkWallet(String accountId, String address, String username, String password, int zoneId) throws ThriftSVException, TException {
        SDKDatacontroler sdkDatacontroler = SDKDatacontroler.getInstance();
        AccountModel accountModel = AccountModel.copyFromDB(accountId, sdkDatacontroler);
        if (accountModel == null) {
            throw new ThriftSVException(ServerConstant.ErrorCode.ERR_USER_NOT_FOUND, "");
        }

        int networkId = ESocialNetwork.BLOCKCHAIN.getIntValue();
        SocialAccountModel socialAccountModel = SocialAccountModel.copyFromDB(networkId, address, sdkDatacontroler);
        if (socialAccountModel == null) {
            throw new ThriftSVException(ServerConstant.ErrorCode.ERR_WALLET_ADDRESS_INVALID, "");
        }

        // check tai khoan da link chua
        String wallet = "";
        AccountLinkedModel linkedModel = AccountLinkedModel.copyFromDB(accountId, sdkDatacontroler);
        if (linkedModel == null || !linkedModel.mapSocialNetwork.containsKey(networkId) || linkedModel.mapSocialNetwork.containsKey(ESocialNetwork.USERNAME.getIntValue())) {
            throw new ThriftSVException(ServerConstant.ErrorCode.ERR_ACCOUNT_NOT_LINK_SOCIAL, "");
        }

        // check username
        wallet = linkedModel.mapSocialNetwork.get(networkId);
        UsernameModel usernameModel = UsernameModel.load(username, sdkDatacontroler);
        if (usernameModel == null || !usernameModel.verifyPassword(password)) {
            throw new ThriftSVException(ServerConstant.ErrorCode.ERR_INVALID_USERNAME_OR_PASSWORD, String.valueOf(ServerConstant.ErrorCode.ERR_USERNAME_ALREADY_EXIST));
        }

        // check username linked
        AccountLinkedModel usernameLinkedModel = AccountLinkedModel.copyFromDB(usernameModel.accountID, sdkDatacontroler);
        if (usernameLinkedModel == null || usernameLinkedModel.mapSocialNetwork.containsKey(networkId)) {
            throw new ThriftSVException(ServerConstant.ErrorCode.ERR_ACCOUNT_LINKED, "");
        }

//        usernameModel.accountID = accountId;
        try {
            // Tao linked
            usernameLinkedModel.link(ESocialNetwork.BLOCKCHAIN.getIntValue(), wallet);
            socialAccountModel.accountID = usernameModel.accountID;
            socialAccountModel.save(sdkDatacontroler);
            AccountModel accountUserModel = AccountModel.copyFromDB(usernameModel.accountID, sdkDatacontroler);
            if (accountUserModel != null) {
                return accountUserModel.mapPlayer.getOrDefault(zoneId, -1L);
            }
//            usernameModel.save(sdkDatacontroler);
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public TLinkedAccount getLinkedAccount(String accountId) throws TException {
        AccountLinkedModel linkedModel = AccountLinkedModel.copyFromDB(accountId, SDKDatacontroler.getInstance());
        if (linkedModel == null) {
            throw new ThriftSVException(ServerConstant.ErrorCode.ERR_USER_NOT_FOUND, String.valueOf(ServerConstant.ErrorCode.ERR_USER_NOT_FOUND));
        }

        String email = "";
        TLinkedAccount linkedAccount = new TLinkedAccount();
        linkedAccount.walletAddress = linkedModel.mapSocialNetwork.getOrDefault(ESocialNetwork.BLOCKCHAIN.getIntValue(), "");
        linkedAccount.username = linkedModel.mapSocialNetwork.getOrDefault(ESocialNetwork.USERNAME.getIntValue(), "");
        if (!linkedAccount.username.isEmpty()) {
            UsernameModel usernameModel = UsernameModel.load(linkedAccount.username, SDKDatacontroler.getInstance());
            if (usernameModel != null) {
                email = usernameModel.email;
            }
        }

        linkedAccount.email = email;
        return linkedAccount;
    }

    @Override
    public TUserInfo getUserInfo(String walletAddress) throws TException {
        SDKDatacontroler sdkDatacontroler = SDKDatacontroler.getInstance();
        SocialAccountModel socialAccountModel = SocialAccountModel.copyFromDB(ESocialNetwork.BLOCKCHAIN.getIntValue(), walletAddress, sdkDatacontroler);
        if (socialAccountModel == null) {
            return null;
        }

        AccountModel accountModel = AccountModel.copyFromDB(socialAccountModel.accountID, sdkDatacontroler);
        if (accountModel == null) {
            return null;
        }

        int serverId = accountModel.lastServerLogin;
        long userId = accountModel.mapPlayer.get(serverId);
        return new TUserInfo(String.valueOf(userId), String.valueOf(serverId), walletAddress);
    }
}
