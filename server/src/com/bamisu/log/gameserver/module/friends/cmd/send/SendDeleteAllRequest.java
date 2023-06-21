package com.bamisu.log.gameserver.module.friends.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendDeleteAllRequest extends BaseMsg {
    public SendDeleteAllRequest() {
        super(CMD.CMD_DELETE_ALL_REQUEST);
    }

    public SendDeleteAllRequest(short errorCode) {
        super(CMD.CMD_DELETE_ALL_REQUEST, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
    }
}
