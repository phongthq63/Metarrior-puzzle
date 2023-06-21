package com.bamisu.gamelib.base.data;

import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

/**
 * Created by Popeye on 4/15/2017.
 */
public abstract class BaseMsg {
    protected int cmdId = -1;

    public void setErrorCode(short errorCode) {
        this.errorCode = errorCode;
    }

    protected short errorCode = 0;
    protected SFSObject data;

    public BaseMsg(int cmdId) {
        this.cmdId = cmdId;
        this.errorCode = 0;
        this.data = new SFSObject();
    }

    public BaseMsg(int cmdId, short errorCode) {
        this.cmdId = cmdId;
        this.errorCode = errorCode;
        this.data = new SFSObject();
    }

    public void packData() {
        data.putInt(Params.CMD_ID, this.cmdId);
        data.putShort(Params.ERROR_CODE, errorCode);
    }

    public ISFSObject getData() {
        return this.data;
    }
    public int getCmdId() {
        return cmdId;
    }

    protected boolean isError() {
        return errorCode != 0;
    }
}
