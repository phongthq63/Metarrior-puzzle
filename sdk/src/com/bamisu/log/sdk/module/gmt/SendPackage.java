package com.bamisu.log.sdk.module.gmt;

import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

public class SendPackage {
    public int ec = GMTErrorCode.SUCCESS;
    public ISFSObject data = new SFSObject();

    public SendPackage() {
    }

    public SendPackage(int ec) {
        this.ec = ec;
    }

    public SendPackage setErrorCode(int ec){
        this.ec = ec;
        return this;
    }

    public String toJson() {
        ISFSObject pack = new SFSObject();
        pack.putInt(Params.ERROR_CODE, ec);
        if (ec == GMTErrorCode.SUCCESS) {
            pack.putSFSObject(Params.DATA, data);
        }
        return pack.toJson();
    }
}
