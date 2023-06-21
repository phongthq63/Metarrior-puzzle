package com.bamisu.log.gameserver.module.friends.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendAcceptAllRequest extends BaseMsg {
    public SendAcceptAllRequest() {
        super(CMD.CMD_ACCEPT_ALL_REQUEST);
    }

    public SendAcceptAllRequest(short errorCode) {
        super(CMD.CMD_ACCEPT_ALL_REQUEST, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
    }
}
