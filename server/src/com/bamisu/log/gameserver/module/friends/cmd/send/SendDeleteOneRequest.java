package com.bamisu.log.gameserver.module.friends.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendDeleteOneRequest extends BaseMsg {
    public SendDeleteOneRequest() {
        super(CMD.CMD_DELETE_ONE_REQUEST);
    }

    public SendDeleteOneRequest(short errorCode) {
        super(CMD.CMD_DELETE_ONE_REQUEST, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
    }
}
