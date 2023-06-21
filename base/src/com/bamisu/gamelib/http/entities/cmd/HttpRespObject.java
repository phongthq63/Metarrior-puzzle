package com.bamisu.gamelib.http.entities.cmd;

import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.http.entities.HttpError;
import com.bamisu.gamelib.http.entities.HttpError;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

/**
 * Create by Popeye on 12:28 PM, 10/12/2019
 */
public class HttpRespObject {
    public int ec = 0;
    public String mess = "";
    public ISFSObject data = new SFSObject();

    public HttpRespObject() {
        this.ec = 0;
        this.mess = "Success";
    }

    public HttpRespObject(HttpError error) {
        this.ec = error.ec;
        this.mess = error.mess;
    }

    public ISFSObject getData() {
        return data;
    }

    public void setData(ISFSObject data) {
        this.data = data;
    }

    public String toJsonString(){
        SFSObject sfsObject = new SFSObject();
        sfsObject.putInt(Params.ERROR_CODE, ec);
        sfsObject.putUtfString(Params.MESS, mess);
        sfsObject.putSFSObject(Params.DATA, data);
        return sfsObject.toJson();
    }
}
