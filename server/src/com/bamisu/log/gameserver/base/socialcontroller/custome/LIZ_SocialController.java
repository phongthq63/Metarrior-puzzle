package com.bamisu.log.gameserver.base.socialcontroller.custome;

import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.ValidateUtils;
import com.bamisu.gamelib.utils.socialcontroller.business.ISocialController;
import com.bamisu.gamelib.utils.socialcontroller.exceptions.SocialControllerException;
import com.smartfoxserver.v2.entities.Zone;

public class LIZ_SocialController implements ISocialController {
    public static final String PREFIX = "LIZ";

    @Override
    public UserModel getUserInfo(String token, Zone zone) throws SocialControllerException {
        return null;
    }

    @Override
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
    public UserModel register(String access_token, String filter) throws SocialControllerException {
        return null;
    }


    public void validateRegInfo(String uname, String pass) throws SocialControllerException {
        if(!ValidateUtils.isEmail(uname))
            throw new SocialControllerException(ServerConstant.ErrorCode.ERR_EMAIL_INVALID, "Email không hợp lệ");

//        if (uname.length() < 6 || uname.length() > 15)
//            throw new SocialControllerException(ServerConstant.ErrorCode.ERR_UNAME_LONG, "Tên đăng nhập phải từ 6 - 15 ký tự");

        if (pass.length() < 6 || pass.length() > 15)
            throw new SocialControllerException(ServerConstant.ErrorCode.ERR_PASS_LONG, "Mật khẩu phải từ 6 - 15 ký tự");

//        if (!uname.matches("^[A-Za-z0-9]{6,15}$"))
//            throw new SocialControllerException(ServerConstant.ErrorCode.ERR_UNAME_SPECIAL_CHAR, "Tên đăng nhập không được chứa ký tự đặc biệt");

//        if (ValidateUtils.containsWordsSwearing(uname))
//            throw new SocialControllerException(ServerConstant.ErrorCode.ERR_UNAME_SPECIAL_CHAR, "Tên đăng nhập chứa từ bị cấm");

    }
}
