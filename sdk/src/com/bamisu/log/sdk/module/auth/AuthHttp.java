package com.bamisu.log.sdk.module.auth;

import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.httpserver.ServletBase;
import com.bamisu.gamelib.utils.ServletUtil;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.extensions.ISFSExtension;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Create by Popeye on 2:02 AM, 4/18/2020
 */
public class AuthHttp extends ServletBase {

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
        }
    }
}
