package com.bamisu.log.sdk.module.gmt;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

import javax.servlet.http.HttpServletRequest;

/**
 * Create by Popeye on 9:52 AM, 10/16/2020
 */
public class RecPackage{
    public ISFSObject data;

    public RecPackage(HttpServletRequest req) {
        this.data = SFSObject.newFromJsonData(req.getParameter("data"));
    }
}
