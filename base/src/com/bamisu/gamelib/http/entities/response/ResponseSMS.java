package com.bamisu.gamelib.http.entities.response;

import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Created by Popeye on 10/3/2017.
 */
public class ResponseSMS {
    public int ec = 0;
    public String un = "";
    public String dn = "";
    public ResponseSMS(ISFSObject res) {
        this.ec = res.getShort(Params.ERROR_CODE);
        this.un = res.getUtfString(Params.HttpParams.USER_NAME);
        this.dn = res.getUtfString(Params.HttpParams.DISPLAY_NAME);
    }
}
