package com.bamisu.log.sdk.module.account;

import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.manager.UserManager;
import com.bamisu.gamelib.socialnetwork.ESocialNetwork;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.sdk.module.account.exception.SocialAccountAlreadyExist;
import com.bamisu.log.sdk.module.account.model.AccountLinkedModel;
import com.bamisu.log.sdk.module.account.model.AccountModel;
import com.bamisu.log.sdk.module.account.model.SocialAccountModel;
import com.bamisu.log.sdk.module.account.model.UsernameModel;
import com.bamisu.log.sdk.module.data.SDKDatacontroler;
import com.bamisu.log.sdk.module.giftcode.error.CreateAccountResult;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.httpserver.ServletBase;
import com.bamisu.gamelib.utils.ServletUtil;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Create by Popeye on 5:36 PM, 4/23/2020
 */
public class AccountHttp extends ServletBase {

    private SmartFoxServer sfs;

    @Override
    public void init() throws ServletException {
        sfs = SmartFoxServer.getInstance();
    }

    @Override
    protected void process(HttpServletRequest req, HttpServletResponse resp) {
        super.process(req, resp);
        fixHeaders(resp);

    }

}
