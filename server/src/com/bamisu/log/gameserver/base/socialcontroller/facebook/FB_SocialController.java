package com.bamisu.log.gameserver.base.socialcontroller.facebook;

import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.socialcontroller.business.ISocialController;
import com.bamisu.gamelib.utils.socialcontroller.exceptions.SocialControllerException;
import com.smartfoxserver.v2.entities.Zone;
import org.apache.log4j.Logger;

public class FB_SocialController implements ISocialController {

    public static final int ID = ServerConstant.Auth.AUTH_FB;

    private Logger logger = Logger.getLogger(this.getClass());

    @Override
    public UserModel getUserInfo(String token, Zone zone) throws SocialControllerException {
//        UserBase userBase = UserBase.copyFromDBtoObject(token, zone);
//        UserModel userModel = null;
//        if (UserBase.copyFromDBtoObject(token, zone) != null) {
//            userModel = UserModel.copyFromDBtoObject(userBase.userId, zone);
//        } else {
//            userModel = UserModel.createUserModel(ID, token, "", zone);
//        }
//
//        return userModel;

        return null;
    }

    public UserModel getUserInfo(String access_token, String filter) throws SocialControllerException {
        return null;
    }

    @Override
    public boolean feedOpenApi2(String session_key, int template_bundle_id,
                                String message, String name, String href, String caption,
                                String description, String media_type, String media_src,
                                String media_href) {
        return false;
    }

    @Override
    public UserModel register(String access_token, String filter) {
        return null;
    }

    public UserModel doRegisterFromWeb(String uname, String password) throws SocialControllerException {
        return getUserInfo(uname, password);
    }
}
