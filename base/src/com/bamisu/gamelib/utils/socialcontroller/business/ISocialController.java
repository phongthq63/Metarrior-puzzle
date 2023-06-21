package com.bamisu.gamelib.utils.socialcontroller.business;

import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.utils.socialcontroller.exceptions.SocialControllerException;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.utils.socialcontroller.exceptions.SocialControllerException;
import com.smartfoxserver.v2.entities.Zone;

public interface ISocialController {
    UserModel getUserInfo(String token, Zone zone) throws SocialControllerException;

    UserModel getUserInfo(String access_token, String filter) throws SocialControllerException;
    
    boolean feedOpenApi2(String session_key, int template_bundle_id,
                         String message, String name, String href,
                         String caption, String description, String media_type,
                         String media_src, String media_href);

    UserModel register(String access_token, String filter) throws SocialControllerException ;
}
