package com.bamisu.log.gameserver.module.friends.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendAcceptOneRequest extends BaseMsg {
    public SendAcceptOneRequest() {
        super(CMD.CMD_ACCEPT_ONE_REQUEST);
    }

    public SendAcceptOneRequest(short errorCode) {
        super(CMD.CMD_ACCEPT_ONE_REQUEST, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
    }
}
