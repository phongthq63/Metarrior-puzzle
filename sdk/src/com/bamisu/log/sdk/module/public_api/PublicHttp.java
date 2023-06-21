package com.bamisu.log.sdk.module.public_api;

import com.bamisu.gamelib.base.datacontroller.IDataController;
import com.bamisu.gamelib.email.EmailUtils;
import com.bamisu.gamelib.encryption.Encrypter;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.entities.ServerInfo;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.ValidateUtils;
import com.bamisu.log.sdk.module.account.AccountManager;
import com.bamisu.log.sdk.module.account.exception.SocialAccountAlreadyExist;
import com.bamisu.log.sdk.module.account.model.*;
import com.bamisu.log.sdk.module.auth.SocialNetworkUtils;
import com.bamisu.log.sdk.module.data.SDKDatacontroler;
import com.bamisu.log.sdk.module.multiserver.MultiServerManager;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.httpserver.ServletBase;
import com.bamisu.gamelib.socialnetwork.ESocialNetwork;
import com.bamisu.gamelib.utils.ServletUtil;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Create by Popeye on 11:43 AM, 4/23/2020
 */
public class PublicHttp extends ServletBase {

    private SmartFoxServer sfs;

    @Override
    public void init() throws ServletException {
        sfs = SmartFoxServer.getInstance();
    }

    @Override
    protected void process(HttpServletRequest req, HttpServletResponse resp) {
        super.process(req, resp);
        fixHeaders(resp);
        String method = ServletUtil.getStringParameter(req, "cmd", "");
        switch (method) {
            case "get_login_info":
                getLoginInfo(req, resp);
                break;
            case CMD.InternalMessage.HTTP_REGISTER:
                this.register(req, resp);
                break;
            case CMD.InternalMessage.HTTP_SEND_CODE:
                this.getRegistrationCode(req, resp);
                break;
            case CMD.InternalMessage.HTTP_REQUEST_RESET_PASSWORD:
                this.requestResetPassword(req, resp);
                break;
            case CMD.InternalMessage.HTTP_RESET_PASSWORD:
                this.resetPassword(req, resp);
                break;
            default:
                responseJson("404", resp);
        }
    }

    private void register(HttpServletRequest req, HttpServletResponse resp) {
        String ip = req.getRemoteAddr();
        //đang bảo trì
        if(ServerConstant.PRE_MAINTENANCE){
            if(!ServerConstant.white_list_ip.contains(ip)){
                responseJson(this.setErrorCode(ServerConstant.ErrorCode.ERR_SERVER_PRE_MAINTENANCE), resp);
                return;
            }
        }

        String jsonEncrypted = req.getParameter(Params.DATA);
        try {
            ISFSObject data = SFSObject.newFromJsonData(Encrypter.getInstance().decrypt(jsonEncrypted));
            String username = data.getText(Params.USER_NAME);
            String password = data.getText(Params.USER_PASSWORD);
            String email = data.getText(Params.USER_EMAIL).toLowerCase();
            String code = data.getText(Params.CODE);
            String wallet = "";
            SDKDatacontroler controller = SDKDatacontroler.getInstance();
            IDataController dataController = controller.getController();
            if (data.containsKey(Params.WALLET)) {
                wallet = data.getText(Params.WALLET);
                SocialAccountModel walletAccount = SocialAccountModel.copyFromDB(ESocialNetwork.BLOCKCHAIN.getIntValue(), wallet, controller);
                if (walletAccount != null) {
                    responseJson(this.setErrorCode(ServerConstant.ErrorCode.ERR_WALLET_ADDRESS_EXISTED), resp);
                    return;
                }
            }

            int type = ESocialNetwork.USERNAME.getIntValue();
            UserCodeModel codeModel = UserCodeModel.load(code, dataController);
            if (codeModel == null) {
                responseJson(this.setErrorCode(ServerConstant.ErrorCode.ERR_CODE_INVALID), resp);
                return;
            }

            if (!ValidateUtils.isEmail(email) || !codeModel.email.equalsIgnoreCase(email)) {
                responseJson(this.setErrorCode(ServerConstant.ErrorCode.ERR_EMAIL_INVALID), resp);
                return;
            }

            UsernameModel nameModel = UsernameModel.load(username, controller);
            if (nameModel != null) {
                responseJson(this.setErrorCode(ServerConstant.ErrorCode.ERR_USERNAME_ALREADY_EXIST), resp);
                return;
            }

            UserEmailModel emailModel = UserEmailModel.load(email, dataController);
            if (emailModel != null) {
                responseJson(this.setErrorCode(ServerConstant.ErrorCode.ERR_EMAIL_ALREADY_EXIST), resp);
                return;
            }

            // Tao account
            String country = "";
            if (data.containsKey("country")) {
                country = data.getText("country");
            }

            AccountModel accountModel = AccountManager.getInstance().creatAccount();
            if (accountModel == null) {
                responseJson(this.setErrorCode(ServerConstant.ErrorCode.ERR_SYS), resp);
                return;
            }

            accountModel.country = country;
            accountModel.save(SDKDatacontroler.getInstance());

            //link với SocialAccount
            //tạo SocialAccountModel
            SocialAccountModel socialAccountModel;
            SocialAccountModel walletAccount = null;
            try {
                socialAccountModel = SocialAccountModel.create(accountModel, type, username, SDKDatacontroler.getInstance());
            } catch (SocialAccountAlreadyExist socialAccountAlreadyExist) {
                responseJson(this.setErrorCode(ServerConstant.ErrorCode.ERR_SYS), resp);
                return;
            }

            if (socialAccountModel == null) {
                responseJson(this.setErrorCode(ServerConstant.ErrorCode.ERR_SYS), resp);
                return;
            }

            if (!wallet.isEmpty()) {
                try {
                    walletAccount = SocialAccountModel.create(accountModel, ESocialNetwork.BLOCKCHAIN.getIntValue(), username, controller);
                } catch (SocialAccountAlreadyExist socialAccountAlreadyExist) {
                    responseJson(this.setErrorCode(ServerConstant.ErrorCode.ERR_SYS), resp);
                    return;
                }

                if (walletAccount == null) {
                    responseJson(this.setErrorCode(ServerConstant.ErrorCode.ERR_SYS), resp);
                    return;
                }
            }

            //cập nhật AccountLinkedModel
            AccountLinkedModel accountLinkedModel = AccountLinkedModel.copyFromDB(accountModel.id, SDKDatacontroler.getInstance());
            accountLinkedModel.link(socialAccountModel.socialNetwork, socialAccountModel.socialNetworkKey);

            if (walletAccount != null) {
                accountLinkedModel.link(walletAccount.socialNetwork, walletAccount.socialNetworkKey);
            }
            // Tạo user model
            nameModel = UsernameModel.create(accountModel.id, username, password);
            nameModel.email = email;
            nameModel.save(controller);
            // Tạo email model
            emailModel = new UserEmailModel(accountModel.id, username, email);
            emailModel.save(dataController);
            // Xoa code model
           codeModel.delete(dataController);
            responseJson(this.setErrorCode(ServerConstant.ErrorCode.NONE), resp);
        } catch (Exception e) {
            responseJson(this.setErrorCode(ServerConstant.ErrorCode.ERR_SYS), resp);
        }
    }

    private void getRegistrationCode(HttpServletRequest req, HttpServletResponse resp) {
        String ip = req.getRemoteAddr();
        //đang bảo trì
        if(ServerConstant.PRE_MAINTENANCE){
            if(!ServerConstant.white_list_ip.contains(ip)){
                responseJson(this.setErrorCode(ServerConstant.ErrorCode.ERR_SERVER_PRE_MAINTENANCE), resp);
                return;
            }
        }

        String jsonEncrypted = req.getParameter(Params.DATA);
        try {
            ISFSObject data = SFSObject.newFromJsonData(Encrypter.getInstance().decrypt(jsonEncrypted));
            String email = data.getText(Params.USER_EMAIL).toLowerCase();
            String username = "";
            if (data.containsKey(Params.USER_NAME)) {
                username = data.getText(Params.USER_NAME);
            }
            responseJson(this.sendCode(email, true, username), resp);
        } catch (Exception e) {
            responseJson(this.setErrorCode(ServerConstant.ErrorCode.ERR_SYS), resp);
        }
    }

    private void requestResetPassword(HttpServletRequest req, HttpServletResponse resp) {
        String ip = req.getRemoteAddr();
        //đang bảo trì
        if(ServerConstant.PRE_MAINTENANCE){
            if(!ServerConstant.white_list_ip.contains(ip)){
                responseJson(this.setErrorCode(ServerConstant.ErrorCode.ERR_SERVER_PRE_MAINTENANCE), resp);
                return;
            }
        }

        String jsonEncrypted = req.getParameter(Params.DATA);
        try {
            ISFSObject data = SFSObject.newFromJsonData(Encrypter.getInstance().decrypt(jsonEncrypted));
            String email = data.getText(Params.USER_EMAIL).toLowerCase();
            responseJson(this.sendCode(email, false, ""), resp);
        } catch (Exception e) {
            responseJson(this.setErrorCode(ServerConstant.ErrorCode.ERR_SYS), resp);
        }
    }

    private String sendCode(String email, boolean isRegistration, String username) {
        IDataController dataController = SDKDatacontroler.getInstance().getController();
        UserEmailModel emailModel = UserEmailModel.load(email, dataController);
        String title = "[No-Reply] Metarrior - Confirm Your Registration";
        String content;
        if (!isRegistration) {
            if (emailModel == null) {
                return this.setErrorCode(ServerConstant.ErrorCode.ERR_EMAIL_INVALID);
            }
            title = "[No-Reply] Metarrior - Reset Your Password";
            content = Utils.loadConfig(ServerConstant.User.FILE_PATH_EMAIL_FORGOT_PASSWORD);
        } else {
            if (emailModel != null) {
                return this.setErrorCode(ServerConstant.ErrorCode.ERR_EMAIL_ALREADY_EXIST);
            }

            content = Utils.loadConfig(ServerConstant.User.FILE_PATH_EMAIL_REGISTER);
        }

        UserCodeModel codeModel = new UserCodeModel(email);
        codeModel.save(dataController);
        content = content.replace("$code", codeModel.code);
        content = content.replace("$email", codeModel.email);
        if (emailModel != null) {
            content = content.replace("$username", emailModel.username);
        }

        if (!username.equalsIgnoreCase("")) {
            content = content.replace("$username", username);
        }

        EmailUtils utils = new EmailUtils();
        utils.suportEmail.sendMail(email, title, content);

        return this.setErrorCode(ServerConstant.ErrorCode.NONE, codeModel.code);
    }

    private void resetPassword(HttpServletRequest req, HttpServletResponse resp) {
        String ip = req.getRemoteAddr();
        //đang bảo trì
        if(ServerConstant.PRE_MAINTENANCE){
            if(!ServerConstant.white_list_ip.contains(ip)){
                responseJson(this.setErrorCode(ServerConstant.ErrorCode.ERR_SERVER_PRE_MAINTENANCE), resp);
                return;
            }
        }

        String jsonEncrypted = req.getParameter(Params.DATA);
        try {
            ISFSObject data = SFSObject.newFromJsonData(Encrypter.getInstance().decrypt(jsonEncrypted));
            String code = data.getText(Params.CODE);
            String password = data.getText(Params.USER_PASSWORD);
            IDataController dataController = SDKDatacontroler.getInstance().getController();
            UserCodeModel codeModel = UserCodeModel.load(code, dataController);
            if (codeModel == null) {
                responseJson(this.setErrorCode(ServerConstant.ErrorCode.ERR_CODE_INVALID), resp);
                return;
            }

            UserEmailModel emailModel = UserEmailModel.load(codeModel.email, dataController);
            if (emailModel == null) {
                responseJson(this.setErrorCode(ServerConstant.ErrorCode.ERR_EMAIL_INVALID), resp);
                return;
            }

            if (!emailModel.isVerified) {
                responseJson(this.setErrorCode(ServerConstant.ErrorCode.ERR_ACCOUNT_NOT_ACTIVATED), resp);
                return;
            }

            UsernameModel usernameModel = UsernameModel.load(emailModel.username, SDKDatacontroler.getInstance());
            if (usernameModel == null) {
                responseJson(this.setErrorCode(ServerConstant.ErrorCode.ERR_INVALID_USERNAME_OR_PASSWORD), resp);
                return;
            }

            usernameModel.changePassword(password);
            responseJson(this.setErrorCode(ServerConstant.ErrorCode.NONE), resp);
        } catch (Exception e) {
            responseJson(this.setErrorCode(ServerConstant.ErrorCode.ERR_SYS), resp);
        }
    }


    /**
     * lấy thông tin login khi mở game
     * @param req
     * @param resp
     */
    private void getLoginInfo(HttpServletRequest req, HttpServletResponse resp) {
        ISFSObject sendPack = new SFSObject();
        String ip = req.getRemoteAddr();
        //đang bảo trì
        if(ServerConstant.PRE_MAINTENANCE){
            if(!ServerConstant.white_list_ip.contains(ip)){
                sendPack.putInt(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_SERVER_PRE_MAINTENANCE);
                responseJson(sendPack.toJson(), resp);
                return;
            }
        }
        Logger logger = Logger.getLogger("sdklogin");
        String jsonEncrypted = req.getParameter(Params.DATA);
        String token = "";
        int socialNetwork = 0;
        String password = "";
        try {
            ISFSObject data = SFSObject.newFromJsonData(Encrypter.getInstance().decrypt(jsonEncrypted));
            logger.info("getLoginInfo " + ip + " client send|" + data.toJson());

            token = data.getUtfString(Params.TOKEN);
            if(data.containsKey(Params.TYPE)){
                socialNetwork = data.getInt(Params.TYPE);
            }

            if (data.containsKey(Params.USER_PASSWORD)) {
                password = data.getText(Params.USER_PASSWORD);
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(jsonEncrypted);
            responseJson("404", resp);
            return;
        }

        //login bằng game token
        LoginTokenModel loginTokenModel;
        if(socialNetwork == 0){
            String accountID = "";
            String loginKey = "";
            if(token == null ||token.isEmpty()){
                loginKey = "";
                sendPack = MultiServerManager.getInstance().getNewServer().toSFSObject();
            }else {
                loginTokenModel = LoginTokenModel.copyFromDBtoObject(token, SDKDatacontroler.getInstance());
                if(loginTokenModel == null){
                    loginKey = "";
                    sendPack = MultiServerManager.getInstance().getNewServer().toSFSObject();
                }else {
                    loginKey = loginTokenModel.token;
                    accountID = loginTokenModel.accountID;
                    sendPack = MultiServerManager.getInstance().getServerForToken(accountID).toSFSObject();
                }
            }
            sendPack.putUtfString(Params.LOGIN_KEY, loginKey);
            logger.info("getLoginInfo " + ip + "|" + "rec|" + token + "|" + "send|" + loginKey);
            responseJson(sendPack.toJson(), resp);
            return;
        }else { //login bằng tài khoản mạng xã hội
            String socialNetworkKey = "";
            SocialAccountModel socialAccountModel;
            ESocialNetwork networkName = ESocialNetwork.fromIntValue(socialNetwork);
            switch (networkName){
                case FACEBOOK:
                    socialNetworkKey = SocialNetworkUtils.getIDforAppFacebook(token);
                    break;
                case GOOGLE:
                    socialNetworkKey = SocialNetworkUtils.getIDforAppGoogle(token);
                    break;
                case APPLE:
                    socialNetworkKey = SocialNetworkUtils.getIDforAppApple(token);
                    break;
                case SINGMAAN:
                    socialNetworkKey = SocialNetworkUtils.getIDforAppSingmaan(token);
                    break;
                case DEVICE_ID:
                    if(token.length() < 10){
                        sendPack.putInt(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_SYS);
                        responseJson(sendPack.toJson(), resp);
                        return ;
                    }
                    socialNetworkKey = token;
                    break;
                case BLOCKCHAIN:
                    socialNetworkKey = SocialNetworkUtils.getAddressWalletBlockchain(token);
                    break;
                case USERNAME:
                    socialNetworkKey = token;
                    break;
                default:
                    break;
            }
            logger.info("socialNetworkKey: " + socialNetworkKey);
            if(socialNetworkKey.isEmpty()) return;
            socialAccountModel = SocialAccountModel.copyFromDB(socialNetwork, socialNetworkKey, SDKDatacontroler.getInstance());
            if(socialAccountModel != null){
                //Đã có account
                String accessToken;
                if (networkName == ESocialNetwork.USERNAME) {
                    accessToken = socialNetworkKey;
                    UsernameModel usernameModel = UsernameModel.load(accessToken, SDKDatacontroler.getInstance());
                    if (usernameModel == null || !usernameModel.verifyPassword(password)) {
                        responseJson(this.setErrorCode(ServerConstant.ErrorCode.ERR_INVALID_USERNAME_OR_PASSWORD), resp);
                        return;
                    }

//                    if (usernameModel.email.equalsIgnoreCase("")) {
//                        responseJson(this.setErrorCode(ServerConstant.ErrorCode.ERR_ACCOUNT_NOT_ACTIVATED), resp);
//                        return;
//                    }
                }

                loginTokenModel = LoginTokenModel.create(socialAccountModel.accountID, SDKDatacontroler.getInstance());
                accessToken = loginTokenModel.token;

                sendPack = MultiServerManager.getInstance().getServerForToken(socialAccountModel.accountID).toSFSObject();
                sendPack.putUtfString(Params.LOGIN_KEY, accessToken);
                logger.info("getLoginInfo " + ip + "|" + "rec|" + token + "|" + "send|" + accessToken);
                responseJson(sendPack.toJson(), resp);
                return;
            }else {
                // chưa có account khi đăng nhập = username thì trả về lỗi luôn
                if (networkName == ESocialNetwork.USERNAME) {
                    sendPack.putInt(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_INVALID_USERNAME_OR_PASSWORD);
                    responseJson(sendPack.toJson(), resp);
                    return;
                }
                //chưa có account, tạo mới account
                AccountModel accountModel = AccountManager.getInstance().creatAccount();
                if (accountModel == null) {
                    logger.info("getLoginInfo " + ip + "|" + "rec|" + token + "|" + "accountModel null");
                    responseJson(sendPack.toJson(), resp);
                    return;
                }

                //link với SocialAccount
                //tạo SocialAccountModel
                try {
                    socialAccountModel = SocialAccountModel.create(accountModel, socialNetwork, socialNetworkKey, SDKDatacontroler.getInstance());
                } catch (SocialAccountAlreadyExist socialAccountAlreadyExist) {
                    logger.info("getLoginInfo " + ip + "|" + "rec|" + token + "|" + "socialAccountAlreadyExist");
                    responseJson(sendPack.toJson(), resp);
                    return;
                }

                //cập nhật AccountLinkedModel
                AccountLinkedModel accountLinkedModel = AccountLinkedModel.copyFromDB(accountModel.id, SDKDatacontroler.getInstance());
                accountLinkedModel.link(socialAccountModel.socialNetwork, socialAccountModel.socialNetworkKey);

                //tạo login key
                loginTokenModel = LoginTokenModel.create(accountModel.id, SDKDatacontroler.getInstance());
                if (loginTokenModel == null) {
                    //tạo token thất bại
                    logger.info("getLoginInfo " + ip + "|" + "rec|" + token + "|Fail");
                    responseJson(sendPack.toJson(), resp);
                    return;
                }
                sendPack = MultiServerManager.getInstance().getServerForToken(accountModel.id).toSFSObject();
                sendPack.putUtfString(Params.LOGIN_KEY, loginTokenModel.token);
                logger.info("getLoginInfo " + ip + "|" + "rec|" + token + "|" + "send|" + loginTokenModel.token);
                responseJson(sendPack.toJson(), resp);
                return;
            }
        }
    }
}
