package com.bamisu.gamelib.base.socialcontroller.google;

import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.utils.socialcontroller.business.ISocialController;
import com.bamisu.gamelib.utils.socialcontroller.exceptions.SocialControllerException;
import com.smartfoxserver.v2.entities.Zone;

import java.util.List;

/**
 * Created by Popeye on 4/21/2017.
 */
public class GG_SocialController implements ISocialController {

    @Override
    public UserModel getUserInfo(String token, Zone zone) throws SocialControllerException {
        return null;
    }

    @Override
    public UserModel getUserInfo(String access_token, String filter) throws SocialControllerException {
        return null;
    }

    @Override
    public boolean feedOpenApi2(String session_key, int template_bundle_id, String message, String name, String href, String caption, String description, String media_type, String media_src, String media_href) {
        return false;
    }

    @Override
    public UserModel register(String access_token, String filter) {
        return null;
    }
}
